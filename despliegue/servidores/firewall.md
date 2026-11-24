# Firewall: abrir SOLO los puertos necesarios

| Servidor | Puerto | Quién se conecta | Ubuntu (ufw) | Windows (PowerShell como administrador) |
|---|---|---|---|---|
| VM-Frontend | 80/tcp | Cualquier PC de la red (navegador) | `sudo ufw allow 80/tcp` | `New-NetFirewallRule -DisplayName "SIVER Apache 80" -Direction Inbound -Protocol TCP -LocalPort 80 -Action Allow` |
| VM-Backend | 8080/tcp | Navegadores de la red (Angular llama a la API) | `sudo ufw allow 8080/tcp` | `New-NetFirewallRule -DisplayName "SIVER Tomcat 8080" -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow` |
| VM-BD | 3306/tcp | **Solo** la IP del backend | `sudo ufw allow from 192.168.56.20 to any port 3306 proto tcp` | `New-NetFirewallRule -DisplayName "SIVER MySQL desde backend" -Direction Inbound -Protocol TCP -LocalPort 3306 -RemoteAddress 192.168.56.20 -Action Allow` |

En Ubuntu, después: `sudo ufw enable` y `sudo ufw status`.

¿Por qué el puerto 8080 del backend sí se abre a toda la red? Porque Angular corre en el navegador
del usuario, y es el navegador el que llama a `http://192.168.56.20:8080/siver/api`.
La base de datos, en cambio, solo la usa el backend; por eso el 3306 se restringe a su IP.

Comprobaciones desde otra máquina:

```bash
# desde la VM-Backend: ¿llego a MySQL?
nc -zv 192.168.56.30 3306          # Windows: Test-NetConnection 192.168.56.30 -Port 3306
# desde la PC del usuario: ¿llego a la API?
curl http://192.168.56.20:8080/siver/v3/api-docs
# desde la PC del usuario: ¿llego al frontend?
curl -I http://192.168.56.10/pages/dashboard   # debe responder 200 (rewrite a index.html)
```
