![amity imagen agregada por que si](https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/67791701-3cdb-45e2-a89c-99022f89acce/deqm8p0-85be0247-de78-4535-88bf-237b82a6af5d.png/v1/fill/w_988,h_808/amity_being_cute_by_the_mimic_deqm8p0-pre.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTA0NyIsInBhdGgiOiJcL2ZcLzY3NzkxNzAxLTNjZGItNDVlMi1hODljLTk5MDIyZjg5YWNjZVwvZGVxbThwMC04NWJlMDI0Ny1kZTc4LTQ1MzUtODhiZi0yMzdiODJhNmFmNWQucG5nIiwid2lkdGgiOiI8PTEyODAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.9oWRYq8nF_DTbNtp9Jm4biUinY9ybwI4T706u7EmX5Y)

## Notas Extras

### Para saber más: OpenAPI Initiative

La documentación es algo muy importante en un proyecto, especialmente si se trata de una API Rest, ya que en este caso podemos tener varios clientes que necesiten comunicarse con ella y necesiten documentación que les enseñe cómo realizar esta comunicación de manera correcta.

Durante mucho tiempo no existió un formato estándar para documentar una API Rest, hasta que en 2010 surgió un proyecto conocido como Swagger, cuyo objetivo era ser una especificación open source para el diseño de APIs Rest. Después de un tiempo, se desarrollaron algunas herramientas para ayudar a los desarrolladores a implementar, visualizar y probar sus APIs, como Swagger UI, Swagger Editor y Swagger Codegen, lo que lo convirtió en un proyecto muy popular y utilizado en todo el mundo.

En 2015, Swagger fue comprado por la empresa SmartBear Software, que donó la parte de la especificación a la fundación Linux. A su vez, la fundación renombró el proyecto a OpenAPI. Después de esto, se creó la OpenAPI Initiative, una organización centrada en el desarrollo y la evolución de la especificación OpenAPI de manera abierta y transparente.

OpenAPI es actualmente la especificación más utilizada y también la principal para documentar una API Rest. La documentación sigue un patrón que puede ser descrito en formato YAML o JSON, lo que facilita la creación de herramientas que puedan leer dichos archivos y automatizar la creación de documentación, así como la generación de código para el consumo de una API.

# JWT en tu documentacion

Recordando el problema que se nos presenta mal final de `Notas2.md`, para resolverlo haremos lo siguiente:

1.  Dentro de la <a href="https://springdoc.org/">documentacion</a> iremos a la seccion de `How do I add authorization header in requests?` se puede acceder rapidamente buscando con `ctrl+g`

2.  Dicha seccion nos dara un par de codigos que tenemos que agregar a nuestro codigo, empezando por el que contiene el `@Bean` lo copiamos y volviendo al codigo dentro del paquete `infra` crearemos uno nuevo de nombre `springdoc` y dentro de este crearemos una clase llamada `SpringDocConfiguration.java`

3.  En la clase creada pegaremos el codigo que obtuvimos de la documentacion:

        @Configuration
        public class SpringDocConfiguration {
            @Bean
            public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .components(new Components()
                                .addSecuritySchemes("bearer-key",
                                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
                                                .bearerFormat("JWT")));
            }
        }

    Pero ademas debemos agregarle el @Configuration para que spring sepa que hacer con dicha clase

4.  Ahora agregamos otro metodo solo con un simple mensaje:

        @Bean
        public void message(){
            System.out.println("bearer is working");
        }

5.  Ahora dentro de la documentacion en la misma seccion veremos otro codigo como este:

        @SecurityRequirement(name = "bearer-key")

    dicho codigo es para colocarlo en los controller que vayan a poder hacer uso de dicha funcionalidad

6.  Lo colocaremos en los siguientes Controller `Consulta`, `Medico` y `Paciente`. Quedando como se ve aqui:

        @RestController
        @RequestMapping("/consultas")
        @SecurityRequirement(name = "bearer-key")
        public class ConsultaController

7.  volvemos a la pagina de <a href="http://localhost:8080/swagger-ui/index.html">swagger</a> y notaremos que ahora aparece un boton de `Authorize` en el cual una vez generemos nuestro token prodremos dar click ahi y colocarlo para asi poder hacer uso de nuestras demas consultas

# Testes con Spring Boot

## Test de caja blanca y negra

En términos "pruebas de caja blanca" (white-box testing) y "pruebas de caja negra" (black-box testing) se refieren a dos enfoques diferentes para probar un sistema o una aplicación. Estos enfoques tienen distintas finalidades y se centran en aspectos diferentes de la prueba.

### Pruebas de caja blanca (White-box testing):

Las pruebas de caja blanca implican tener conocimiento sobre la estructura interna del código y la lógica subyacente de una aplicación. Estas pruebas se diseñan considerando la lógica interna, las rutas de ejecución y los componentes individuales del código. El objetivo es evaluar cómo funciona el código desde el interior, lo que permite identificar problemas en la lógica, la cobertura de código y otros aspectos técnicos.

En el contexto de Java Spring Boot, las pruebas de caja blanca podrían implicar la escritura de pruebas unitarias donde se prueban funciones o métodos específicos, se manipulan los datos de entrada y se verifican los resultados esperados. También se puede evaluar la cobertura de código para asegurarse de que todas las rutas posibles en el código hayan sido probadas.

### Pruebas de caja negra (Black-box testing):

Las pruebas de caja negra, por otro lado, se centran en el comportamiento externo de una aplicación y no requieren conocimiento detallado del código interno. Los testers se centran en probar las entradas y observar las salidas sin preocuparse por cómo se logra internamente. El objetivo principal es verificar si la aplicación cumple con los requisitos funcionales y de diseño, y si se comporta como se espera.

En el contexto de Java Spring Boot, las pruebas de caja negra podrían incluir pruebas de integración o pruebas de extremo a extremo, donde se simulan interacciones de usuarios o de sistemas externos con la aplicación y se evalúa si se obtienen los resultados correctos según los requisitos.

En resumen, la principal diferencia entre las pruebas de caja blanca y las de caja negra radica en si se tiene conocimiento sobre la estructura interna del código (caja blanca) o si solo se evalúa el comportamiento externo (caja negra) de una aplicación. Ambos enfoques son importantes para asegurar la calidad de un software, y generalmente se utilizan en conjunto para lograr una cobertura integral de pruebas.
