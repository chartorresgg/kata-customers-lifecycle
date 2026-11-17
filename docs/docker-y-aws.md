# Detalle de Docker y del despliegue en AWS

Complemento de las secciones [Docker](../README.md#cómo-ejecutar-con-docker-opcional) y
[Despliegue en la nube (AWS)](../README.md#despliegue-en-la-nube-aws) del README.

## Docker

Con Docker no hace falta tener Java ni Maven instalados: la imagen trae todo lo necesario para construir y ejecutar la aplicación.

### Construir la imagen

Desde la raíz del proyecto (el punto final hace parte del comando):

```bash
docker build -t kata-customers .
```

El `Dockerfile` tiene dos etapas:

1. **Construir:** parte de una imagen con Maven y el JDK 21, copia el código y ejecuta `mvn clean package`. Las pruebas también se ejecutan aquí: si alguna falla, no se genera la imagen.
2. **Ejecutar:** parte de una imagen que solo tiene el JRE 21 y recibe únicamente el `app.jar` de la primera etapa.

La imagen final no incluye Maven, el JDK ni el código fuente: solo lo necesario para ejecutar.

### Ejecutar en DEV

```bash
docker run --rm -p 8080:8080 kata-customers
```

### Ejecutar en PROD

```bash
docker run --rm -p 9090:9090 -e SPRING_PROFILES_ACTIVE=prod kata-customers
```

Qué significa cada opción:

| Opción | Qué hace |
|---|---|
| `-p 8080:8080` | Conecta un puerto de la máquina (izquierda) con el puerto del contenedor (derecha) |
| `-e SPRING_PROFILES_ACTIVE=prod` | Indica el perfil con una variable de entorno. Sin ella, arranca en DEV |
| `--rm` | Borra el contenedor al detenerlo |

La imagen es la misma para los dos ambientes: solo cambian la variable de entorno y el puerto. Los dos contenedores se pueden ejecutar al mismo tiempo, y cada uno guarda sus propios clientes en memoria.

El mensaje de arranque y los endpoints son los mismos que al ejecutar con `java -jar`. Para detener un contenedor: `Ctrl + C`.

## Despliegue en la nube (AWS)

La aplicación está desplegada en AWS, en el ambiente PROD:

```bash
curl http://54.91.255.229:9090/api/info
curl http://54.91.255.229:9090/api/customers
```

> Es un despliegue temporal y esta instancia se eliminará después de la sustentación. Usa `http` (sin HTTPS) y solo debe recibir datos de ejemplo.

### Cómo se desplegó

| Componente                    | Valor                                                                |
| ----------------------------- | -------------------------------------------------------------------- |
| Servicio                      | Amazon EC2: una máquina virtual `t3.micro` (2 vCPU, 1 GB de memoria) |
| Sistema operativo             | Amazon Linux 2023                                                    |
| Grupo de seguridad (firewall) | Solo el puerto `9090` abierto a internet                             |
| Acceso para administrar       | EC2 Instance Connect, desde la consola web de AWS                    |
| Ejecución                     | Un contenedor de Docker con el perfil `prod`                         |

Pasos, dentro de la instancia:

```bash
# 1. Instalar Docker y Git
sudo dnf install -y docker git
sudo systemctl enable --now docker

# 2. Traer el código y construir la imagen (el mismo Dockerfile)
git clone https://github.com/chartorresgg/kata-customers-lifecycle.git
cd kata-customers-lifecycle
sudo docker build -t kata-customers .

# 3. Arrancar el contenedor en PROD
sudo docker run -d --name kata-prod --restart unless-stopped -p 9090:9090 -e SPRING_PROFILES_ACTIVE=prod kata-customers
```

Diferencias con el comando local: `-d` ejecuta el contenedor en segundo plano y `--restart unless-stopped` lo vuelve a arrancar si se cae o si la máquina se reinicia.

La instancia no tiene Java ni Maven instalados: la imagen de Docker trae todo lo necesario. Antes de construir se agregaron 2 GB de memoria de intercambio (*swap*), porque la máquina solo tiene 1 GB de memoria.

### Seguridad del despliegue

- No se creó un par de claves SSH ni claves de acceso de AWS: no existe ningún secreto que pueda quedar expuesto en el repositorio.
- El puerto 22 (SSH) solo estuvo abierto durante la configuración; después se eliminó la regla.
- La API es pública y no tiene autenticación: cualquiera que conozca la dirección puede crear clientes. Es aceptable para una demostración con datos ficticios, no para un sistema real.
