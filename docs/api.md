# Detalle de la API

Complemento de la sección [Endpoints](../README.md#endpoints) del README.

Los ejemplos usan el puerto de DEV (`8080`). Para PROD, cambiar a `9090`.

| Método | Ruta | Qué hace | Respuestas |
|---|---|---|---|
| `POST` | `/api/customers` | Crea un cliente | `201` creado · `400` datos inválidos · `409` email ya registrado |
| `GET` | `/api/customers` | Lista los clientes, ordenados por id | `200` |
| `GET` | `/api/info` | Muestra el ambiente, el nombre y el puerto de la instancia | `200` |

## Crear un cliente

```bash
curl -X POST localhost:8080/api/customers -H "Content-Type: application/json" -d '{"name":"Ana","email":"ana@example.com"}'
```

Respuesta `201 Created`:

```json
{"id":1,"name":"Ana","email":"ana@example.com"}
```

El `id` lo asigna el servidor. El email se guarda en minúsculas.

> En Windows PowerShell 5.1, el JSON debe escribirse con las comillas escapadas: `'{\"name\":\"Ana\",\"email\":\"ana@example.com\"}'`.

## Listar los clientes

```bash
curl localhost:8080/api/customers
```

Respuesta `200 OK`:

```json
[{"id":1,"name":"Ana","email":"ana@example.com"}]
```

## Consultar el ambiente

```bash
curl localhost:8080/api/info
```

Respuesta `200 OK`:

```json
{"environment":"DEV","application":"customers-dev","port":8080}
```

## Respuestas de error

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
