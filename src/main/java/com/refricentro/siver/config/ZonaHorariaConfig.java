package com.refricentro.siver.config;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.context.annotation.Configuration;

/**
 * Fija la hora del backend en America/Lima.
 *
 * Un servidor (una VM Linux o un contenedor Docker) suele venir en UTC, cinco
 * horas adelante de Peru. Sin esto, "las ventas de hoy" del dashboard y las
 * fechas de auditoria saldrian corridas segun donde se despliegue.
 */
@Configuration
public class ZonaHorariaConfig {

    public static final String ZONA = "America/Lima";

    @PostConstruct
    void fijarZona() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZONA));
    }
}
