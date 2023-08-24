# IMPORTANTE

Por si el programa no compila debido a que esta ocupado el puerto 8080 se debe hacer en el windows powershell
en modo administrador los comandos:

    $connection = Get-NetTCPConnection -LocalPort 8080
    $processId = $connection.OwningProcess
    Stop-Process -Id $processId

Una vez hecho lo anterior podemos corroborar que se mato el proceso con el comando:

    Get-NetTCPConnection -LocalPort 8080

O simplemente navegando al localhost:8080

# Iniciar el proyecto:

Una vez que desde Eclipse se descargan todas las dependencias podremos trabajar desde VSCode,
simplemente tendremos que dar run al codigo de _ApiApplication.java_ lo cual hará que spring
automaticamente levante un servidor en el puerto 8080 que es donde vamos a ver lo que realicemos.

# 1. Hello World

1.  creamos el package med.voll.api.controller

2.  dentro de este agregamos el siguiente codigo:

        // Importaciones necesarias para usar las anotaciones y clases de Spring Framework
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        // Anotación que marca esta clase como un controlador de Spring y permite gestionar solicitudes HTTP
        @RestController
        // Anotación que establece la ruta base para las solicitudes que manejará este controlador
        @RequestMapping("/hello")
        public class HelloController {

            // Anotación que indica que este método manejará solicitudes HTTP GET en la ruta definida por la clase y el método
            @GetMapping
            // Método que se ejecutará al recibir una solicitud GET en la ruta "/hello"
            public String helloWorld() {
                // Devuelve una cadena de texto como respuesta a la solicitud
                return "Hello World from México";
            }

        }

-   Explicación del codigo anterior:
    -   En resumen, este código define un controlador de Spring llamado HelloController, que se encargará de manejar las solicitudes HTTP GET dirigidas a la ruta base "/hello". Cuando se reciba una solicitud GET en esta ruta, el método helloWorld() se ejecutará y devolverá la cadena de texto "Hello World from México" como respuesta. Este controlador y método son parte de una aplicación web construida con Spring Framework, y se encargan de responder con un saludo específico al ser accedidos a través de la ruta "/hello".

# Enviando y recibiendo datos

1. Como no es un curso que abarque el front necesitaremos probar de otra manera la API, por lo que
   se hará uso de Insomnia

2. Insomnia es un programa, por lo que fue descargado e instalado, pero en general es otro RapidAPI client, Thunder Client o Postman.

3. Creamos una nueva organización (se crea sola iniciando sesión)

4. Create -> Lo nombramos Voll med request, osea creamos una colección de peticiones

5. New HTTP request -> POST -> Body = JSON

6. Una vez realizado lo anterior agregamos lo siguiente al body:

    {
    "nombre": "Rodrigo Lopez",
    "email": "rodrigo.lopez@voll.med",
    "documento": "123456",
    "especialidad": "ortopedia",
    "direccion": {
    "calle": "calle 1",
    "distrito": "distrito 1",
    "ciudad": "Lima",
    "numero": "1",
    "complemento": "a"
    }
    }

y agregamos la url _http://localhost:8080/medicos_ que claramente no hemos creado en el codigo por lo que
nos dara un error 404.

7.  Dentro del paquete Controller creamos la clase _MedicoController.java_

8.  Creamos el codigo para que Insomniac pueda enviar algo:

        package med.voll.api.controller;

        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        @RestController
        @RequestMapping("/medicos")
        public class MedicoController {

            @PostMapping
            public void registrarMedico() {
                System.out.println("El request llega correctamente");
            }

        }

## Explicacion:

    // Importaciones necesarias para usar las anotaciones y clases de Spring Framework
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    // Anotación que marca esta clase como un controlador de Spring y permite gestionar solicitudes HTTP
    @RestController
    // Anotación que establece la ruta base para las solicitudes que manejará este controlador
    @RequestMapping("/medicos")
    public class MedicoController {

        // Anotación que indica que este método manejará solicitudes HTTP POST en la ruta definida por la clase y el método
        @PostMapping
        // Método que se ejecutará al recibir una solicitud POST en la ruta "/medicos"
        public void registrarMedico() {
            // Imprime un mensaje en la consola cuando se llama a este método
            System.out.println("El request llega correctamente");
        }

    }

-   Importaciones: Se importan las clases necesarias del paquete org.springframework.web.bind.annotation para poder utilizar las anotaciones y clases relacionadas con el manejo de solicitudes HTTP.

-   Anotación @RestController: Esta anotación marca la clase MedicoController como un controlador de Spring encargado de manejar solicitudes HTTP. Además de gestionar las solicitudes, también automáticamente serializa las respuestas en formato JSON.

-   Anotación @RequestMapping("/medicos"): Establece la ruta base para las solicitudes que serán manejadas por este controlador. Esto significa que todas las solicitudes dirigidas a la ruta "/medicos" serán atendidas por los métodos de este controlador.

-   Definición de clase MedicoController: Aquí se declara la clase del controlador.

-   Anotación @PostMapping: Indica que el método registrarMedico() manejará solicitudes HTTP POST. Esto significa que este método se ejecutará cuando se reciba una solicitud POST en la ruta definida por la combinación de la anotación @RequestMapping de la clase y la anotación @PostMapping del método.

-   Método registrarMedico(): Este método se ejecutará cuando llegue una solicitud POST a la ruta "/medicos". En este caso, el método simplemente imprime un mensaje en la consola utilizando System.out.println(), indicando que el request llegó correctamente.

9.  Volvemos al Insomniac y reintentamos el send request, ahora deberia aparecer un estatus 200
    indicando que todo salio bien, pero claro no se esta guardando nada.

10. Si bien aún como tal no se guardara la información si se podra ver en consola, para ello solo
    debemos modificar un poco el codigo:

        @PostMapping
        public void registrarMedico(@RequestBody String parametro) {
            System.out.println("El request llega correctamente");
            System.out.println(parametro);
        }

## Explicacion:

En este fragmento de código, se ha modificado el método registrarMedico() agregando un parámetro @RequestBody String parametro. Aquí tienes la explicación de lo que se ha hecho:

-   Agregando @RequestBody: Se ha añadido la anotación @RequestBody al parámetro String parametro. Esta anotación indica que el valor del cuerpo de la solicitud HTTP (la información enviada en la solicitud POST) se debe vincular al parámetro parametro de tipo String.

-   Nuevo parámetro String parametro: Ahora el método registrarMedico() acepta un parámetro de tipo String llamado parametro. Este parámetro contendrá los datos enviados en el cuerpo de la solicitud POST.

-   Impresión del parámetro: Se ha agregado una línea adicional de código que imprime el contenido del parámetro parametro utilizando System.out.println(parametro). Esto mostrará en la consola los datos que se han enviado en el cuerpo de la solicitud.

11. Realizado lo anterior volvemos a hacer el send en Insomniac y deberíamos ver en consola la info que mandamos en JSON

12. Claro no olvidemos que recibirlo como _String_ no es lo ideal, lo ideal seria como un _objeto_.

# Algunos conceptos utiles

## CORS

CORS es un mecanismo utilizado para agregar encabezados HTTP que le indican a los navegadores que permitan que una aplicación web se ejecute en un origen y acceda a los recursos desde un origen diferente. Este tipo de acción se denomina _cross-origin HTTP request_. En la práctica, les dice a los navegadores si se puede acceder o no a un recurso en particular.

## Same-origin policy

Por defecto, una aplicación Front-end, escrita en JavaScript, solo puede acceder a los recursos ubicados en el mismo origen de la solicitud. Esto sucede debido a la política del mismo origen (same-origin policy), que es un mecanismo de seguridad de los navegadores que restringe la forma en que un documento o script de un origen interactúa con los recursos de otro. Esta política tiene como objetivo detener los ataques maliciosos.

Dos URL comparten el mismo origen si el protocolo, el puerto (si se especifica) y el host son los mismos. Comparemos posibles variaciones considerando la URL

    https://cursos.alura.com.br/category/programacao

| URL                                                    | Resultado     | Motivo                     |
| ------------------------------------------------------ | ------------- | -------------------------- |
| https://cursos.alura.com.br/category/front-end         | Mismo origen  | Solo camino diferente      |
| http://cursos.alura.com.br/category/programacao        | Error de CORS | Protocolo diferente (http) |
| https://faculdade.alura.com.br:80/category/programacao | Error de CORS | Host diferente             |

Ahora, la pregunta sigue siendo: ¿qué hacer cuando necesitamos consumir una API con una URL diferente sin tener problemas con CORS? Como, por ejemplo, cuando queremos consumir una API que se ejecuta en el puerto 8000 desde una aplicación React que se ejecuta en el puerto 3000. ¡Compruébalo!

Al enviar una solicitud a una API de origen diferente, la API debe devolver un header llamado _Access-Control-Allow-Origin_. Dentro de ella es necesario informar los diferentes orígenes que serán permitidas de consumir la API, en nuestro caso:

    Access-Control-Allow-Origin: http://localhost:3000

Puede permitir el acceso desde cualquier origen utilizando el símbolo _ (asterisco): Access-Control-Allow-Origin: _. Pero esta no es una medida recomendada, ya que permite que fuentes desconocidas accedan al servidor, a menos que sea intencional, como en el caso de una API pública. Ahora veamos cómo hacer esto en Spring Boot correctamente.

## Habilitación de diferentes orígenes en Spring Boot

Para configurar el CORS y permitir que un origen específico consuma la API, simplemente cree una clase de configuración como la siguiente:

    @Configuration
    public class CorsConfiguration implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT");
        }
    }

http://localhost:3000 sería la dirección de la aplicación Front-end y .allowedMethods los métodos que se permitirán ejecutar. Con esto, podrás consumir tu API sin problemas desde una aplicación front-end.

# DTO Java Record

1.  Creamos un package de nombre medico

2.  Agregamos un Record llamado DatosRegistroMedico.java

3.  Agregamos un enum de nombre Especialidad.java

4.  Fuera de dicho paquete, dentro de api creamos el Record de nombre DatosDireccion.java, este lo creamos fuera ya que no sera algo exclusivo de los medicos, capaz tambien lo usamos para pasientes o usuarios.

5.  Los codigos quedan de la siguiete manera:

    -   Medico controller:

            package med.voll.api.controller;
            import org.springframework.web.bind.annotation.PostMapping;
            import org.springframework.web.bind.annotation.RequestBody;
            import org.springframework.web.bind.annotation.RequestMapping;
            import org.springframework.web.bind.annotation.RestController;

            import med.voll.api.medico.DatosRegistroMedico;

            @RestController
            @RequestMapping("/medicos")
            public class MedicoController {

                @PostMapping
                public void registrarMedico(@RequestBody DatosRegistroMedico parametro) {
                    System.out.println("El request llega correctamente");
                    System.out.println(parametro);
                }

            }

    -   DatosRegistroMedico.java:

            package med.voll.api.medico;

            import med.voll.api.DatosDireccion;

            public record DatosRegistroMedico(
            String nombre, String email,
            String documento, Especialidad especialidad, DatosDireccion direccion) {

        }

    -   DatosDireccion.java:

            package med.voll.api;

            public record DatosDireccion(String calle, String distrito, String ciudad, int numero, String complemento) {

            }

## Explicacion general:

### MedicoController.java

Este controlador Java, llamado MedicoController, se encarga de manejar solicitudes POST relacionadas con el registro de médicos. Está mapeado a la ruta base "/medicos". Cuando se recibe una solicitud POST en esta ruta, el método registrarMedico() se activa. Este método espera recibir un objeto DatosRegistroMedico en el cuerpo de la solicitud, que contiene información sobre el registro de un médico. Luego, imprime un mensaje en la consola confirmando que la solicitud llegó correctamente y muestra los datos del médico registrados.

### DatosRegistroMedico.java

Esta clase define un objeto llamado DatosRegistroMedico utilizando Java Records (introducidos en Java 16). Este registro almacena información relevante para el registro de un médico, como nombre, email, documento, especialidad y dirección. La dirección está representada por un objeto DatosDireccion. Este registro está diseñado para facilitar la estructura y manipulación de los datos necesarios para registrar un médico.

### DatosDireccion.java

Esta clase define un objeto DatosDireccion que almacena información sobre la dirección. Contiene detalles como la calle, el distrito, la ciudad, el número y cualquier complemento adicional. Está diseñado para organizar y gestionar información de direcciones de manera coherente.

En resumen, estos códigos están diseñados para manejar el registro de médicos en una API. El controlador MedicoController maneja las solicitudes POST en la ruta "/medicos" y recibe objetos DatosRegistroMedico en el cuerpo de la solicitud. La clase DatosRegistroMedico representa la información completa del médico que se está registrando, incluida la dirección, utilizando registros Java. La clase DatosDireccion define la estructura de la información de dirección utilizada en el registro de médicos.

## Continuando

6.  Hacemos otro leve cambio a MedicoController.java:

        public void registrarMedico(@RequestBody DatosRegistroMedico datosRegistroMedico) {
            System.out.println(datosRegistroMedico);
        }

7.  Si intentamos mandar otro send request nos dara un error 404 y en la consola se vera el mensaje:

        2023-08-13T19:28:32.188-06:00 WARN 2176 --- [nio-8080-exec-3] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Cannot deserialize value of type `med.voll.api.medico.Especialidad` from String "ortopedia": not one of the values accepted for Enum class: [ORTOPEDIA, PEDIATRIA, CARDIOLOGIA, GINECOLOGIA]]

    El error ocurre cuando Spring intenta deserializar (convertir) el objeto JSON enviado en la solicitud a un objeto Java. En este caso, el problema está relacionado con el campo especialidad del objeto DatosRegistroMedico.

    En el código que proporcionaste, no se incluye la definición de la enumeración Especialidad, pero podemos inferir que existe y que se utiliza para representar la especialidad del médico, como "ortopedia", "pediatría", etc.

    El error indica que Spring no pudo convertir la cadena "ortopedia" en un valor de la enumeración Especialidad, ya que no coincide con ninguno de los valores permitidos en esa enumeración.

8.  La solución rapida para esto es simplemente cambiar a mayusculas el valor del JSON:

        {
            "nombre": "Rodrigo Lopez",
            "email": "rodrigo.lopez@voll.med",
            "documento": "123456",
            "especialidad": "ORTOPEDIA",
            "direccion": {
                "calle": "calle 1",
                "distrito": "distrito 1",
                "ciudad": "Lima",
                "numero": "1",
                "complemento": "a"
            }
        }

    Respuesta en consola:

        DatosRegistroMedico[nombre=Rodrigo Lopez, email=rodrigo.lopez@voll.med, documento=123456, especialidad=ORTOPEDIA, direccion=DatosDireccion[calle=calle 1, distrito=distrito 1, ciudad=Lima, numero=1, complemento=a]]

9.  Finalmente movemos DatosDireccion.java a un paquete nuevo de nombre direccion:

        package med.voll.api.direccion;

## Que es un DTO?

A nivel conceptual, un DTO (Objeto de Transferencia de Datos, por sus siglas en inglés) es una clase que se utiliza para transportar datos entre diferentes partes de una aplicación, especialmente entre la capa de acceso a datos y la capa de presentación. Su principal objetivo es facilitar la transferencia de información sin exponer innecesariamente los detalles internos de los objetos subyacentes.

Imagina que tienes una aplicación web de una tienda en línea. Quieres mostrar los detalles de un producto en una página de producto. En este caso, podrías usar un DTO para transportar los detalles específicos del producto desde la base de datos hasta la interfaz de usuario.

### Ejemplo 1: ProductoDTO

    public class ProductoDTO {
        private Long id;
        private String nombre;
        private double precio;

        // Constructor, getters y setters
    }

En este ejemplo, ProductoDTO es un DTO que contiene información sobre un producto, como su ID, nombre y precio. Cuando se necesita mostrar los detalles de un producto en la interfaz de usuario, puedes crear un objeto ProductoDTO y llenarlo con los datos necesarios antes de enviarlo desde la capa de servicio hasta la capa de controlador, y finalmente presentarlo en la vista.

### Ejemplo 2: PedidoDTO

    public class PedidoDTO {
        private Long id;
        private List<ProductoDTO> productos;
        private double total;

        // Constructor, getters y setters
    }

En este caso, PedidoDTO es un DTO que podría usarse para transportar los detalles de un pedido. Además de la información básica del pedido, contiene una lista de ProductoDTO que representan los productos en ese pedido. Esto permite transferir todos los datos necesarios para mostrar y gestionar un pedido sin tener que exponer la estructura interna de los objetos de dominio (como Pedido y Producto) fuera de la capa de servicio.

En resumen, los DTOs son clases que se utilizan para transportar datos de manera estructurada entre diferentes partes de una aplicación sin exponer los detalles internos. Son especialmente útiles en arquitecturas en capas para separar claramente las responsabilidades y mejorar la eficiencia en la transferencia de datos.

# Agregando nuevas dependencias

1. volvemos a https://start.spring.io/

2. agregamos las dependencias de:

    - ### Spring Data JPA:

        Spring Data JPA es un subproyecto de Spring que simplifica la implementación de la capa de acceso a datos en aplicaciones Java basadas en Spring utilizando la especificación JPA (Java Persistence API). JPA es una API de Java que permite interactuar con bases de datos relacionales de manera más orientada a objetos, lo que facilita el manejo de entidades y sus relaciones.

        Spring Data JPA proporciona abstracciones y facilidades para trabajar con JPA, reduciendo la cantidad de código repetitivo que normalmente se necesita para realizar operaciones CRUD (crear, leer, actualizar, eliminar) y consultas en la base de datos. Utiliza anotaciones y convenciones para mapear las clases Java a tablas en la base de datos y viceversa.

        Al incluir la dependencia de Spring Data JPA en tu proyecto, puedes aprovechar su poder para simplificar el acceso a datos y agilizar el desarrollo de la capa de persistencia.

    - ### MySQL Driver:

        Un controlador JDBC (Java Database Connectivity) es esencial para permitir que una aplicación Java se comunique con una base de datos MySQL. MySQL Driver es un controlador específico para MySQL que implementa las interfaces de JDBC y proporciona las implementaciones necesarias para permitir que tu aplicación se conecte, ejecute consultas y actualizaciones en la base de datos MySQL.

        En el contexto de Spring y Spring Data JPA, el controlador MySQL es necesario para establecer la conexión entre tu aplicación y la base de datos MySQL. Debes agregar esta dependencia para que Spring y Spring Data JPA puedan comunicarse con la base de datos.

    - ### Flyway Migration:

        Flyway es una herramienta de migración de bases de datos que se integra muy bien con el ecosistema de Spring. Las migraciones de bases de datos son scripts que evolucionan el esquema de la base de datos a medida que la aplicación se desarrolla y cambia con el tiempo.

        La dependencia de Flyway Migration en tu proyecto Spring te permite definir y administrar las migraciones de la base de datos de manera controlada y reproducible. Puedes escribir scripts de migración que describan los cambios en la estructura de la base de datos a lo largo del tiempo. Flyway se encarga de aplicar estos scripts en el orden correcto y asegura que la base de datos esté siempre en la versión adecuada para la versión de la aplicación.

3. No vamos a volver a generar un poryecto, en cambio vamos a sleccionar la opción de maven y seguido de esto al botón de explore, nos dara una vista previa del proyecto que se va a generar y simplemente copiamos y pegamos lo necesario a nuestro pom.xml

# Configurando la conexion a MySQL

1. vamos a la carpeta resources y abrimos el archivo application.properties

2. dentro de este agregamos lo siguiente:

spring.datasource.url=jdbc:mysql://localhost:3307/vollmed_api
spring.datasource.username=root
spring.datasource.password=Pantera09?

3. iniciamos la aplicacion para comprobar que todo funciona como debería

## Notas adicionales:

La configuración de una aplicación Spring Boot se realiza en archivos externos, y podemos usar el archivo de propiedades o el archivo YAML. En este “Para saber más”, abordaremos las principales diferencias entre ellos.

### Archivo de propiedades

De forma predeterminada, Spring Boot accede a las configuraciones definidas en el archivo application.properties, que utiliza un formato clave=valor:

    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.datasource.url=jdbc:mysql://localhost:3306/clinica
    spring.datasource.username=root
    spring.datasource.password=root

Cada fila es una configuración única, por lo que necesitamos expresar datos jerárquicos usando los mismos prefijos para nuestras claves, es decir, necesitamos repetir los prefijos, en este caso spring y datasource.

### Configuración YAML

YAML es otro formato muy utilizado para definir datos de configuración jerárquicos, como se hace en Spring Boot.

Tomando el mismo ejemplo de nuestro archivo application.properties, podemos convertirlo a YAML cambiando su nombre a application.yml y modificando su contenido a:

    spring:
        datasource:
            driver-class-name: com.mysql.cj.jdbc.Driver
            url: jdbc:mysql://localhost:3306/clinica
            username: root
            password: root

Con YAML, la configuración se ha vuelto más legible ya que no contiene prefijos repetitivos. Además de la legibilidad y la reducción de repeticiones, el uso de YAML facilita el almacenamiento de variables de configuración del entorno, como lo recomienda 12 Factor App (*https://12factor.net/es/*), una metodología conocida y utilizada que define 12 mejores prácticas para crear una aplicación moderna, escalable y de sencillo mantenimiento.

### Pero después de todo, ¿qué formato usar?

A pesar de las ventajas que nos aportan los archivos YAML frente al archivo properties, la decisión de elegir uno u otro es una cuestión de gusto personal. Además, no se recomienda tener ambos tipos de archivos en el mismo proyecto al mismo tiempo, ya que esto puede generar problemas inesperados en la aplicación.

Si elige usar YAML, tenga en cuenta que escribirlo al principio puede ser un poco laborioso debido a sus reglas de tabulación.

# Entidades JPA

Es hora de definir el modelo de tablas que vamos a necesitar para el proyecto

1.  vamos al paquete de medico para crear la clase de este mismo (mejor dicho entidad para nuestra base de datos, esto es usando JPA), los atributos que tendra pues seran los mismos que vimos en el json que usamos a modo de prueba:

        package med.voll.api.medico;

        import jakarta.persistence.*;
        import med.voll.api.direccion.Direccion;

        // Indica que esta clase se mapea a una tabla llamada "medicos" en la base de datos
        @Table(name = "medicos")
        // Indica que esta clase es una entidad JPA y se puede manejar con JPA
        @Entity(name = "Medico")
        public class Medico {

            // Anotación que marca el campo como clave primaria y configurado para autoincrementar
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

            // Campos simples que se mapean directamente a columnas en la tabla "medicos"
            private String nombre;
            private String emal; // Aquí hay un error tipográfico, debería ser "email" en lugar de "emal"
            private String documento;

            // Enumerado que se mapea a una columna, usando EnumType.STRING se almacena como cadena
            @Enumerated(EnumType.STRING)
            private Especialidad especialidad;

            // Objeto incrustado (Embeddable) que se mapea a la tabla de "direcciones"
            @Embedded
            private Direccion direccion;

            // Constructor, getters y setters no están incluidos en este ejemplo
        }

### Explicación del codigo:

-   @Table(name = "medicos"): Esta anotación indica que la clase Medico está mapeada a la tabla de la base de datos llamada "medicos".

-   @Entity(name = "Medico"): Esta anotación declara que la clase Medico es una entidad JPA, lo que significa que se puede administrar usando JPA.

-   @Id y @GeneratedValue(strategy = GenerationType.IDENTITY): Estas anotaciones marcan el campo id como la clave primaria de la entidad. @GeneratedValue configura cómo se generan los valores de las claves primarias, en este caso, se usa una estrategia de autoincremento.

-   Campos simples: Los campos nombre, emal, y documento son campos regulares que se mapean directamente a las columnas correspondientes en la tabla "medicos".

-   @Enumerated(EnumType.STRING): Esta anotación se utiliza para mapear el enumerado Especialidad como una columna en la tabla. La opción EnumType.STRING indica que el valor se almacenará como una cadena.

-   @Embedded: Esta anotación indica que el campo direccion es un objeto incrustado (Embeddable) y que se debe mapear a una tabla separada (si es necesario).

## Continuando...

2.  Como podemos notar del codigo anterior se crea también una clase para direccion la cual contiene el siguiente codigo:

        package med.voll.api.direccion;

        import jakarta.persistence.*;

        // Indica que esta clase se usará como componente incrustado en otras entidades
        @Embeddable
        public class Direccion {

            private String calle;
            private Integer numero;
            private String complemento;
            private String distrito;
            private String ciudad;

            // Constructor, getters y setters no están incluidos en este ejemplo
        }

### Explicacion del codigo:

-   @Embeddable: Esta anotación marca la clase Direccion como un componente incrustado. Esto significa que esta clase se utilizará como parte de otras entidades y no se mapeará directamente a una tabla separada en la base de datos.

-   Campos simples: Los campos calle, numero, complemento, distrito y ciudad son campos regulares de la clase Direccion.

La relación con la clase Medico y cómo se utiliza el concepto de @Embedded:

En la clase Medico, tienes el siguiente campo:

    @Embedded
    private Direccion direccion;

Aquí es donde entra en juego la anotación @Embedded.

El campo direccion en la clase Medico indica que estás utilizando la clase Direccion como un componente incrustado dentro de la entidad Medico. Esto significa que los campos de Direccion se mapearán directamente a las columnas de la tabla "medicos", como si fueran parte de la misma entidad. No se crea una tabla separada para la dirección; en cambio, los campos de Direccion se incorporan en la tabla de Medico.

En resumen, el uso de @Embedded permite incorporar objetos complejos, como la dirección en este caso, directamente en la entidad principal (Medico). Esto mejora la organización de los datos y simplifica las consultas y operaciones, ya que los datos relacionados están almacenados en la misma tabla.

## Continuando...

3.  Ahora para ahorrarnos codigo haremos uso de _lombok_, una de las dependencias que instalamos al principio del proyecto, quedando de la siguiente manera:

        package med.voll.api.medico;

        import jakarta.persistence.*;
        import lombok.*;

        import med.voll.api.direccion.Direccion;

        // Indica que esta clase se mapea a una tabla llamada "medicos" en la base de datos
        @Table(name = "medicos")
        // Indica que esta clase es una entidad JPA y se puede manejar con JPA
        @Entity(name = "Medico")
        // La anotación @Getter genera automáticamente los métodos getter para los campos
        @Getter
        // La anotación @NoArgsConstructor genera automáticamente un constructor sin argumentos
        @NoArgsConstructor
        // La anotación @AllArgsConstructor genera automáticamente un constructor que acepta todos los campos como argumentos
        @AllArgsConstructor
        // La anotación @EqualsAndHashCode genera automáticamente los métodos equals() y hashCode() basados en el campo "id"
        @EqualsAndHashCode(of = "id")
        public class Medico {

            // Anotación que marca el campo como clave primaria y configurado para autoincrementar
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

            // Campos simples que se mapean directamente a columnas en la tabla "medicos"
            private String nombre;
            private String emal; // Aquí hay un error tipográfico, debería ser "email" en lugar de "emal"
            private String documento;

            // Enumerado que se mapea a una columna, usando EnumType.STRING se almacena como cadena
            @Enumerated(EnumType.STRING)
            private Especialidad especialidad;

            // Objeto incrustado (Embeddable) que se mapea a la tabla de "direcciones"
            @Embedded
            private Direccion direccion;

            // Constructor, getters y setters no están incluidos debido al uso de Lombok
        }

### Explicacion del codigo (lombok):

-   @Getter: Esta anotación generará automáticamente métodos getter para todos los campos de la clase Medico, lo que te permite acceder a los valores de los campos sin necesidad de escribir los métodos getter manualmente.

-   @NoArgsConstructor: Esta anotación generará automáticamente un constructor sin argumentos para la clase Medico, lo que facilita la creación de instancias sin necesidad de proporcionar valores iniciales para los campos.

-   @AllArgsConstructor: Esta anotación generará automáticamente un constructor que acepta todos los campos como argumentos. Esto es útil para inicializar rápidamente todas las propiedades de un objeto.

-   @EqualsAndHashCode(of = "id"): Esta anotación generará automáticamente los métodos equals() y hashCode() basados en el campo id. Esto es útil para comparar y usar objetos en colecciones de manera efectiva.

En resumen, Lombok es una biblioteca que permite reducir la cantidad de código boilerplate (código repetitivo) en tus clases al generar automáticamente métodos y constructores comunes. Las anotaciones utilizadas en el código de Medico demuestran cómo Lombok simplifica la creación de constructores, métodos getter y los métodos equals() y hashCode().

## Continuando...

4.  Agregamos lo mismo (lo necesario) para la clase de direccion:

        package med.voll.api.direccion;

        import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
        import lombok.Getter;
        import lombok.NoArgsConstructor;

        @Embeddable
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public class Direccion {

            private String calle;
            private Integer numero;
            private String complemento;
            private String distrito;
            private String ciudad;

        }

5.  Finalmente con esto ya tenemos las entidades mapeadas, pero ahora tenemos que hacer que se reciban los datos y seguido de esto hacerlos _persistir_ en la basde de datos

6.  Antes era con el patron DAO, pero podemos reducir eso haicendo uso de las bondades de spring.

# Interfaces Repository

1.  Vamos a nuestro paquete de medico y crearemos la interface de MedicoRepository y simplemente debido a las bondades de Spring solo hara falta escribir lo siguiente:

        package med.voll.api.medico;

        import org.springframework.data.jpa.repository.JpaRepository;

        public interface MedicoRepository extends JpaRepository<Medico, Long> {

        }

### Explicando el codigo:

-   MedicoRepository es una interfaz: Esta interfaz define un contrato para acceder y manipular datos relacionados con la entidad Medico en la base de datos. Aunque no tienes que implementar los métodos en esta interfaz, Spring Data JPA generará automáticamente implementaciones en tiempo de ejecución según los métodos que declares aquí.

-   JpaRepository<Medico, Long>: Esta interfaz extiende la interfaz genérica JpaRepository proporcionada por Spring Data JPA. Esta interfaz genérica requiere dos parámetros: el tipo de entidad que deseas manejar (Medico en este caso) y el tipo de dato de la clave primaria de esa entidad (Long en este caso, que es el tipo de dato del campo id en la clase Medico).

-   Métodos de manipulación de datos generados: Al extender JpaRepository, MedicoRepository hereda una serie de métodos predefinidos para operaciones CRUD (crear, leer, actualizar y eliminar) y consultas relacionadas con la entidad Medico. Estos métodos son generados automáticamente por Spring Data JPA basándose en el nombre de los métodos. Por ejemplo:

    -   save: Para crear o actualizar un registro de Medico.
    -   findById: Para buscar un Medico por su ID.
    -   findAll: Para recuperar todos los registros de Medico.
    -   deleteById: Para eliminar un Medico por su ID, entre otros.

-   Implementación automática: Spring Data JPA se encargará de generar la implementación real de estos métodos en tiempo de ejecución. Esto significa que no tienes que escribir la lógica de acceso a datos de manera manual. Spring Data JPA interpreta los nombres de los métodos y crea consultas SQL correspondientes para llevar a cabo las operaciones.

En resumen, MedicoRepository es una interfaz que aprovecha la potencia de Spring Data JPA para proporcionar operaciones CRUD y consultas específicas de manera sencilla. Al extender JpaRepository y definir los tipos de entidad y clave primaria, te liberas de escribir la lógica repetitiva de acceso a datos y consultas SQL, permitiéndote enfocarte en la funcionalidad de tu aplicación.

## Continuando...

2.  Vamos a MedicoController para agregar lo que hemos estado haciendo hasta el momento, pero se generara un pequeño problema:

        package med.voll.api.controller;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import med.voll.api.medico.DatosRegistroMedico;
        import med.voll.api.medico.MedicoRepository;

        @RestController
        @RequestMapping("/medicos")
        public class MedicoController {

            @Autowired
            private MedicoRepository medicoRepository;

            @PostMapping
            public void registrarMedico(@RequestBody DatosRegistroMedico datosRegistroMedico) {
                medicoRepository.save(datosRegistroMedico);
            }

        }

    Estamos recibiendo las cosas en datosRegistroMedico pero medicoRepository hace uso de Medico, por lo que tendremos que modificar dicha linea de la siguiente manera:

        medicoRepository.save(new Medico(datosRegistroMedico));

    Por lo que en Medico debemos crear el constructor que lo recibe y mapearlo, quedando de la siguiente manera:

        package med.voll.api.medico;

        import jakarta.persistence.*;

        import lombok.*;

        import med.voll.api.direccion.Direccion;

        @Table(name = "medicos")
        @Entity(name = "Medico")
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode(of = "id")
        public class Medico {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;
            private String nombre;
            private String email;
            private String documento;

            @Enumerated(EnumType.STRING)
            private Especialidad especialidad;

            @Embedded
            private Direccion direccion;

            public Medico(DatosRegistroMedico datosRegistroMedico) {
                this.nombre = datosRegistroMedico.nombre();
                this.email = datosRegistroMedico.email();
                this.documento = datosRegistroMedico.documento();
                this.especialidad = datosRegistroMedico.especialidad();
                this.direccion = new Direccion(datosRegistroMedico.direccion());

            }

        }

    Donde sucede el mismo problema con direccion por lo que tambien debemos crear un constructor nuevo y mapearlo:

        package med.voll.api.direccion;

        import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
        import lombok.Getter;
        import lombok.NoArgsConstructor;

        @Embeddable
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public class Direccion {

            private String calle;
            private Integer numero;
            private String complemento;
            private String distrito;
            private String ciudad;

            public Direccion(DatosDireccion direccion) {
                this.calle = direccion.calle();
                this.numero = direccion.numero();
                this.distrito = direccion.distrito();
                this.complemento = direccion.complemento();
                this.ciudad = direccion.ciudad();
            }

        }

### Explicando el codigo:

    package med.voll.api.controller;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import med.voll.api.medico.DatosRegistroMedico;
    import med.voll.api.medico.Medico;
    import med.voll.api.medico.MedicoRepository;

    @RestController
    @RequestMapping("/medicos")
    public class MedicoController {

        // Inyección de dependencia usando @Autowired
        @Autowired
        private MedicoRepository medicoRepository;

        @PostMapping
        public void registrarMedico(@RequestBody DatosRegistroMedico datosRegistroMedico) {
            // Guardar un nuevo objeto Medico utilizando la información de DatosRegistroMedico
            medicoRepository.save(new Medico(datosRegistroMedico));
        }

    }

-   @Autowired y la inyección de dependencia:

    -   @Autowired se usa en el campo medicoRepository para indicarle a Spring que debe inyectar automáticamente una instancia de MedicoRepository en este campo.

    -   La inyección de dependencia es una técnica en Spring para gestionar las dependencias de las clases. En este caso, se inyecta MedicoRepository para que el controlador pueda acceder a la lógica de acceso a datos (CRUD) proporcionada por esa interfaz.

-   @RestController y @RequestMapping:

    -   @RestController indica que esta clase es un controlador que maneja solicitudes HTTP y devuelve respuestas JSON.

    -   @RequestMapping("/medicos") especifica que las rutas de este controlador comenzarán con "/medicos".

-   @PostMapping:

    -   Este método maneja las solicitudes HTTP POST que llegan a la ruta /medicos.

    -   @RequestBody indica que el objeto DatosRegistroMedico se espera en el cuerpo de la solicitud HTTP.

-   registrarMedico:

    -   Este método crea un nuevo objeto Medico utilizando la información proporcionada en datosRegistroMedico.

    -   Utiliza el método save de medicoRepository para guardar el objeto Medico en la base de datos.

### Ahora, sobre por qué no se recomienda @Autowired en algunos casos:

-   Buena práctica: Si bien @Autowired es conveniente, puede ocultar las dependencias reales de una clase y hacer que el código sea menos explícito. Esto puede dificultar la comprensión de las dependencias y las relaciones entre las clases. Además, dificulta la escritura de pruebas unitarias efectivas.

          private final MedicoRepository medicoRepository;

          public MedicoController(MedicoRepository medicoRepository) {
              this.medicoRepository = medicoRepository;
          }

    En este ejemplo, el controlador declara una dependencia directamente en el constructor, lo que facilita ver y entender las dependencias de la clase.

    Por último, sobre el uso de _new Medico_ en lugar de pasar directamente _datosRegistroMedico:_

    El uso de new Medico permite crear una instancia de la entidad Medico, lo que es necesario para poder persistirlo en la base de datos usando medicoRepository.save(). DatosRegistroMedico es un registro de datos y no representa una entidad directamente. Usar new Medico crea un objeto de la entidad con los datos necesarios para ser guardado en la base de datos.

### Explicando el mapeo:

    public class Medico {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String nombre;
        private String email;
        private String documento;

        @Enumerated(EnumType.STRING)
        private Especialidad especialidad;

        @Embedded
        private Direccion direccion;

        public Medico(DatosRegistroMedico datosRegistroMedico) {
            this.nombre = datosRegistroMedico.nombre();
            this.email = datosRegistroMedico.email();
            this.documento = datosRegistroMedico.documento();
            this.especialidad = datosRegistroMedico.especialidad();
            this.direccion = new Direccion(datosRegistroMedico.direccion());
        }
    }

-   DatosRegistroMedico y su constructor:

    -   La clase Medico tiene un constructor que toma un objeto DatosRegistroMedico como argumento. Esto permite mapear los datos del registro de registro de médico a una instancia de Medico.

-   Asignación de campos:

    -   Las líneas dentro del constructor Medico asignan los valores de los campos de DatosRegistroMedico a los campos correspondientes en la entidad Medico.

-   this.nombre = datosRegistroMedico.nombre();:

    -   Aquí se toma el valor del nombre del objeto DatosRegistroMedico y se asigna al campo nombre de la entidad Medico.

-   this.email = datosRegistroMedico.email();:

    -   De manera similar, se asigna el valor del email del objeto DatosRegistroMedico al campo email de la entidad Medico.

-   this.direccion = new Direccion(datosRegistroMedico.direccion());:

    -   Para el campo direccion, se crea una nueva instancia de Direccion usando el constructor de Direccion que toma un objeto DatosDireccion como argumento. Esto mapea los datos de dirección de DatosRegistroMedico a un objeto Direccion.

En resumen, el constructor Medico recibe un objeto DatosRegistroMedico y utiliza los métodos de acceso (nombre(), email(), etc.) para obtener los valores de los campos del registro de médico. Luego, asigna esos valores a los campos correspondientes de la entidad Medico. También crea una instancia de Direccion utilizando los datos de dirección de DatosRegistroMedico y la asigna al campo direccion de la entidad Medico. De esta manera, se mapean los datos de registro de médico a una instancia de la entidad Medico que luego se guarda en la base de datos.

## Continuando...

3. Habiendo hecho todo lo anterior queda probar el codigo, si compilamos todo funcionara, pero si vamos a insomniac y probamos nuestro post request nos dara un error 500, esto es por que _la tabla de medicos no existe_ en nuestra base de datos.

4. Podríamos crear una tabla directamente como sabemos desde mysql, pero para evitar eso usaremos la herramienta de _flyway_.

# CONTINUACIÓN EN notas2.md

El resto de la guía esta en notas2.md 🦆
