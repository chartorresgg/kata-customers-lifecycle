# Kata técnica: API de Clientes con Spring Boot

## Descripción

Esta es una aplicación que le permite al usuario crear nuevos clientes y poder consultarlos. Además, demuestra la importancia de interactuar con un ambiente de desarrollo para la configuración de nuevas funcionalidades y un ambiente de producción, en la que el usuario final podrá crear los nuevos clientes y consultarlos.

## Requisitos

| Herramienta | Versión | Notas |
|---|---|---|
| Java (JDK) | 21 | Obligatorio |
| Maven | 3.9+ | Opcional: el proyecto incluye el wrapper (`mvnw` / `mvnw.cmd`) |
| Git | Cualquiera reciente | Para clonar el repositorio |

Tecnologías usadas: Spring Boot 4.1.2 (Spring Web MVC y Validation). Maven las descarga al construir; no hay que instalarlas.

Los comandos de este documento se probaron en Windows 11 con PowerShell 7. También funcionan en Bash (Linux, macOS, Git Bash).

## ¿Cómo construir?

Clonar el repositorio y entrar a la carpeta:

```bash
git clone https://github.com/chartorresgg/kata-customers-lifecycle.git
cd kata-customers-lifecycle
```

Construir con Maven:

```bash
mvn clean package
```

Si no hay Maven instalado, usar el wrapper incluido:

```bash
.\mvnw.cmd clean package     # Windows (PowerShell o CMD)
./mvnw clean package         # Linux, macOS o Git Bash
```

Qué hace el comando:

1. `clean` borra la carpeta `target/`.
2. `package` compila el código, ejecuta las pruebas y empaqueta la aplicación.

Resultado: `target/app.jar`, un JAR ejecutable que incluye todas sus dependencias.

Si alguna prueba falla, el build se detiene y **no** se genera el JAR.

## ¿Cómo ejecutar el ambiente DEV (Desarrollo)?

```bash
java -jar target/app.jar --spring.profiles.active=dev
```

Resultado esperado al final del arranque:

```
Ejecutando en DEV | app=customers-dev | puerto=8080
```

Si se ejecuta sin indicar perfil (`java -jar target/app.jar`), la aplicación arranca en **DEV**, que es el perfil por defecto.

## ¿Cómo ejecutar el ambiente PROD (Producción)?

```bash
java -jar target/app.jar --spring.profiles.active=prod
```

Resultado esperado al final del arranque:

```
Ejecutando en PROD | app=customers-prod | puerto=9090
```

El perfil también se puede indicar con una variable de entorno:

```bash
# PowerShell
$env:SPRING_PROFILES_ACTIVE = "prod"
java -jar target/app.jar

# Bash
SPRING_PROFILES_ACTIVE=prod java -jar target/app.jar
```

DEV y PROD se pueden ejecutar al mismo tiempo, cada uno en su terminal, porque usan puertos distintos. Cada instancia guarda sus propios clientes en memoria.

Para detener la aplicación: `Ctrl + C`.

## Endpoints

Los ejemplos usan el puerto de DEV (`8080`). Para PROD, cambiar a `9090`.

| Método | Ruta | Qué hace | Respuestas |
|---|---|---|---|
| `POST` | `/api/customers` | Crea un cliente | `201` creado · `400` datos inválidos · `409` email ya registrado |
| `GET` | `/api/customers` | Lista los clientes, ordenados por id | `200` |
| `GET` | `/api/info` | Muestra el ambiente, el nombre y el puerto de la instancia | `200` |

### Crear un cliente

```bash
curl -X POST localhost:8080/api/customers -H "Content-Type: application/json" -d '{"name":"Ana","email":"ana@example.com"}'
```

Respuesta `201 Created`:

```json
{"id":1,"name":"Ana","email":"ana@example.com"}
```

El `id` lo asigna el servidor. El email se guarda en minúsculas.

> En Windows PowerShell 5.1, el JSON debe escribirse con las comillas escapadas: `'{\"name\":\"Ana\",\"email\":\"ana@example.com\"}'`.

### Listar los clientes

```bash
curl localhost:8080/api/customers
```

Respuesta `200 OK`:

```json
[{"id":1,"name":"Ana","email":"ana@example.com"}]
```

### Consultar el ambiente

```bash
curl localhost:8080/api/info
```

Respuesta `200 OK`:

```json
{"environment":"DEV","application":"customers-dev","port":8080}
```

### Respuestas de error

Los errores usan el formato estándar `ProblemDetail` (RFC 9457).

Datos inválidos (`name` vacío, `email` vacío o con formato incorrecto), `400 Bad Request`:

```json
{
  "title": "Datos inválidos",
  "status": 400,
  "detail": "Los datos enviados no son válidos.",
  "instance": "/api/customers",
  "errors": { "email": "El email debe ser válido" }
}
```

Email ya registrado (sin distinguir mayúsculas), `409 Conflict`:

```json
{
  "title": "Cliente duplicado",
  "status": 409,
  "detail": "Ya existe un cliente con ese correo.",
  "instance": "/api/customers"
}
```

## Diferencias entre ambientes

El código y el JAR son los mismos. Solo cambia la configuración que se carga según el perfil.

| | DEV | PROD |
|---|---|---|
| Perfil | `dev` (por defecto) | `prod` |
| Archivo de configuración | `application-dev.properties` | `application-prod.properties` |
| Puerto | `8080` | `9090` |
| Nombre de la aplicación | `customers-dev` | `customers-prod` |
| Log de arranque | `Ejecutando en DEV` | `Ejecutando en PROD` |
| Nivel de log de la aplicación | `DEBUG` | `INFO` |

Por el nivel de log, el mensaje `Consultando todos los clientes registrados` aparece en la consola de DEV al listar los clientes, y no aparece en PROD.

La configuración común (por ejemplo, el perfil por defecto) está en `application.properties`.

Orden de prioridad de la configuración, de mayor a menor: línea de comandos, variables de entorno, `application-{perfil}.properties`, `application.properties`. Por ejemplo, este comando arranca PROD en el puerto 9999 sin cambiar ningún archivo:

```bash
java -jar target/app.jar --spring.profiles.active=prod --server.port=9999
```

## Decisiones y limitaciones

## Ciclo de vida
