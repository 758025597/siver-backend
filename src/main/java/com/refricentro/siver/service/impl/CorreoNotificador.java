package com.refricentro.siver.service.impl;

import com.refricentro.siver.model.Usuario;
import com.refricentro.siver.repository.UsuarioRepository;
import com.refricentro.siver.security.Roles;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Envia por correo las notificaciones a los usuarios activos de los roles
 * destino.
 *
 * @Async: corre en otro hilo. El usuario que registro la venta no espera a
 * que responda el servidor de correo (puede tardar segundos o no responder).
 *
 * Se activa con MAIL_HABILITADO=true. En Docker se usa Mailpit, un servidor
 * SMTP de pruebas con bandeja web en http://localhost:8025.
 */
@Slf4j
@Component
public class CorreoNotificador {

    private final ObjectProvider<JavaMailSender> mailSender;
    private final UsuarioRepository usuarioRepository;
    private final boolean habilitado;
    private final String remitente;

    public CorreoNotificador(ObjectProvider<JavaMailSender> mailSender,
                             UsuarioRepository usuarioRepository,
                             @Value("${siver.mail.habilitado:false}") boolean habilitado,
                             @Value("${siver.mail.remitente:siver@refricentro.pe}") String remitente) {
        this.mailSender = mailSender;
        this.usuarioRepository = usuarioRepository;
        this.habilitado = habilitado;
        this.remitente = remitente;
    }

    @Async
    @Transactional(readOnly = true)
    public void enviar(String titulo, String mensaje, String[] roles) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (!habilitado || sender == null) {
            return;
        }
        Set<String> destino = Set.copyOf(Arrays.asList(roles));
        List<String> correos = usuarioRepository.findByActivoTrue().stream()
                .filter(u -> destino.isEmpty() || destino.contains(rolDe(u)))
                .map(Usuario::getCorreo)
                .toList();
        if (correos.isEmpty()) {
            return;
        }
        try {
            SimpleMailMessage correo = new SimpleMailMessage();
            correo.setFrom(remitente);
            correo.setTo(correos.toArray(String[]::new));
            correo.setSubject("[SIVER] " + titulo);
            correo.setText(mensaje + "\n\n--\nSIVER · Refricentro Peregrino\n"
                    + "Este es un aviso automatico, no respondas este correo.");
            sender.send(correo);
            log.info("Correo '{}' enviado a {} destinatario(s)", titulo, correos.size());
        } catch (RuntimeException e) {
            log.warn("No se pudo enviar el correo '{}': {}", titulo, e.getMessage());
        }
    }

    private static String rolDe(Usuario u) {
        return Roles.normalizar(u.getRol() != null ? u.getRol().getNombre() : null);
    }
}
