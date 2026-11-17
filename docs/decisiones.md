# Decisiones, limitaciones y ciclo de vida

Complemento de las secciones [Diferencias entre ambientes](../README.md#diferencias-entre-ambientes) y
[Ciclo de vida](../README.md#ciclo-de-vida) del README.

## Decisiones

- **Perfil por defecto:** Si no se elige un perfil al iniciar la aplicación, esta cargará el perfil DEV por la configuración `spring.profiles.default=dev`, que se encuentra en `application.properties`.

  Esta decisión se debe a que representa un menor riesgo iniciar la aplicación por descuido en modo DEV que en modo PROD.
- **Almacenamiento:** Cada instancia maneja sus propios datos, los cuales estarán almacenados en un almacén en memoria seguro para múltiples hilos y cuya estructura de datos es un `Map`.

  No se usa una base de datos dado que no hace parte del alcance de los requerimientos y, principalmente, porque el objetivo de la kata es el ciclo de vida.
- **Concurrencia:** Se decidió utilizar un `ConcurrentHashMap` en lugar de un `HashMap`, ya que es más seguro para trabajar con múltiples hilos (solicitudes) al mismo tiempo; un `HashMap` puede generar pérdida de datos o lanzar un error al leer mientras otro hilo escribe.

  También se decidió usar `AtomicLong` en lugar de un `long` tradicional, ya que permite modificar los valores de los IDs de forma segura entre múltiples hilos mediante operaciones atómicas. En cambio, con un `long`, la operación `id++` no es atómica y, por tanto, dos peticiones simultáneas podrían recibir el mismo ID.
- **Email único:** Si se crea un cliente con un email repetido, la aplicación mostrará que no es posible crear el cliente.

  El email se convierte a minúsculas antes de compararlo y guardarlo. La API responde con el código `409 Conflict`.
- **Validación:** Se valida que el nombre no sea un campo vacío y que el email, además de no ser vacío, cuente con el formato de correo (`@Email`). En caso de fallar, responderá con el código `400 Bad Request`, con el detalle por campo.
- **Formato de errores:** `ProblemDetail` es un estándar (RFC 9457) que Spring ya incluye y se usa en lugar de un JSON propio para garantizar interoperabilidad: cualquier cliente que conozca el estándar puede interpretar los errores automáticamente sin acoplarse a un formato personalizado.
- **Datos personales:** El email es un dato personal y no debe quedar en los logs. Este log de creación registra el ID y no el email.
- **Pruebas:** Se desarrollaron 3 clases y 8 pruebas:
  1. Unitarias del repositorio, que se encargan de validar la forma en la que los datos se guardan en el almacén de memoria.
  2. Perfil de producción, que se encarga de validar que el perfil de producción se ejecute correctamente de acuerdo a los valores de los archivos `properties`.
  3. `CustomersApplicationTests`, que se encarga de verificar que la aplicación arranque.

  Estas pruebas se llevan a cabo al ejecutar el comando `mvn clean package`. Si llegan a fallar, no se genera el archivo JAR.

  No se usa Mockito ya que no hay dependencias externas, como una base de datos u otra API. Es decir, no hay nada que aislar.
- **Docker:** El `Dockerfile` usa dos etapas para que la imagen final sea más pequeña y solo tenga lo necesario para ejecutar la aplicación.

  El perfil no queda escrito dentro de la imagen: se indica al arrancar el contenedor, con una variable de entorno. Así, una sola imagen sirve para DEV y para PROD, igual que un solo JAR.
- **Nube:** Se usó una instancia EC2 de AWS con Docker por ser la opción más simple de explicar: una máquina virtual que ejecuta el mismo contenedor que se probó en local. La instancia se administra desde la consola web, sin claves guardadas en ningún equipo.

## Limitaciones

- **Memoria volátil:** De acuerdo al diseño de la aplicación y el requerimiento de usar un ambiente de producción simulado, la memoria volátil implica que no se guarden los datos de los clientes que se hayan creado al reiniciar la aplicación. Cada instancia, al ser un proceso con su propia memoria, gestiona sus datos por separado.
- **Una sola instancia:** `synchronized` verifica y guarda como un solo paso, dentro de una instancia. No resuelve el caso en que existen varias réplicas, que se resolvería con una restricción `UNIQUE` en una base de datos.
- **Validación del email:** La validación del email a través de `@Email` acepta correos que no tengan el componente `.com`, `.co`, `.org`, etc. Por ejemplo, acepta el correo `ana@example`.
- **Cobertura de pruebas:** Aún no se han desarrollado las pruebas de integración de la API: no están probados los endpoints de `/api/customers` ni la regla del email duplicado.
- **PROD simulado:** El ambiente de producción es una simulación en memoria y, por tanto, omite infraestructura productiva real: no persiste datos en una base de datos, no utiliza cifrado mediante HTTPS ni autenticación, y prescinde de credenciales reales (las cuales, en un entorno productivo real, se inyectarían mediante variables de entorno o un gestor de secretos). El despliegue en AWS es una sola instancia temporal, configurada a mano: no tiene dominio propio, réplicas, monitoreo ni despliegue automático.

## Configuración por ambiente

Por el nivel de log, el mensaje `Consultando todos los clientes registrados` aparece en la consola de DEV al listar los clientes, y no aparece en PROD.

La configuración común (por ejemplo, el perfil por defecto) está en `application.properties`.

Orden de prioridad de la configuración, de mayor a menor: línea de comandos, variables de entorno, `application-{perfil}.properties`, `application.properties`. Por ejemplo, este comando arranca PROD en el puerto 9999 sin cambiar ningún archivo:

```bash
java -jar target/app.jar --spring.profiles.active=prod --server.port=9999
```

## Ciclo de vida, paso a paso

El [diagrama del README](../README.md#ciclo-de-vida) muestra el recorrido de la aplicación, desde el código hasta su ejecución en un ambiente:

1. **Código:** el código fuente y los tres archivos de configuración (`application.properties`, `application-dev.properties` y `application-prod.properties`) están en el repositorio.
2. **Construcción:** `mvn clean package` borra el resultado anterior, compila el código y ejecuta las pruebas.
3. **Pruebas:** si alguna prueba falla, el proceso se detiene y no se genera el JAR. Así, nunca se ejecuta una versión que no pasó las pruebas.
4. **Empaquetado:** si las pruebas pasan, se genera `target/app.jar`, un único JAR ejecutable que incluye las dependencias y las tres configuraciones.
5. **Ejecución:** el mismo JAR se ejecuta en DEV o en PROD. El ambiente se elige al arrancar, con el perfil, y cada uno carga su puerto, su nombre y su nivel de log.

En esta kata los pasos se ejecutan a mano. En un entorno real los automatizaría una herramienta de integración y despliegue continuo (CI/CD): cada cambio en el repositorio dispararía la construcción y las pruebas, y el JAR resultante se desplegaría en cada ambiente. Con Docker, lo que se despliega es la imagen, que ya contiene el JAR y el Java que necesita.
