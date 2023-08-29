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

# Tests con Spring Boot

## Test de caja blanca y negra

En términos "pruebas de caja blanca" (white-box testing) y "pruebas de caja negra" (black-box testing) se refieren a dos enfoques diferentes para probar un sistema o una aplicación. Estos enfoques tienen distintas finalidades y se centran en aspectos diferentes de la prueba.

### Pruebas de caja blanca (White-box testing):

Las pruebas de caja blanca implican tener conocimiento sobre la estructura interna del código y la lógica subyacente de una aplicación. Estas pruebas se diseñan considerando la lógica interna, las rutas de ejecución y los componentes individuales del código. El objetivo es evaluar cómo funciona el código desde el interior, lo que permite identificar problemas en la lógica, la cobertura de código y otros aspectos técnicos.

En el contexto de Java Spring Boot, las pruebas de caja blanca podrían implicar la escritura de pruebas unitarias donde se prueban funciones o métodos específicos, se manipulan los datos de entrada y se verifican los resultados esperados. También se puede evaluar la cobertura de código para asegurarse de que todas las rutas posibles en el código hayan sido probadas.

### Pruebas de caja negra (Black-box testing):

Las pruebas de caja negra, por otro lado, se centran en el comportamiento externo de una aplicación y no requieren conocimiento detallado del código interno. Los testers se centran en probar las entradas y observar las salidas sin preocuparse por cómo se logra internamente. El objetivo principal es verificar si la aplicación cumple con los requisitos funcionales y de diseño, y si se comporta como se espera.

En el contexto de Java Spring Boot, las pruebas de caja negra podrían incluir pruebas de integración o pruebas de extremo a extremo, donde se simulan interacciones de usuarios o de sistemas externos con la aplicación y se evalúa si se obtienen los resultados correctos según los requisitos.

En resumen, la principal diferencia entre las pruebas de caja blanca y las de caja negra radica en si se tiene conocimiento sobre la estructura interna del código (caja blanca) o si solo se evalúa el comportamiento externo (caja negra) de una aplicación. Ambos enfoques son importantes para asegurar la calidad de un software, y generalmente se utilizan en conjunto para lograr una cobertura integral de pruebas.

1.  Si hemos estado atentos cada que agregamos dependencias al pom.xml habremos visto que hay una sobre test que es la que ya nos permite hacer uso de cosas como mockito y JUnit para realizar dichos test, en este caso de hara uso de JUnit

2.  empezaremos por hacer pruebas a los metodos de `MedicoRepository` pero los que hacen uso de `@Query`, para ello primero iremos a la carpeta que ya existe de test y dentro de `/api` creamos el paquete `domain` y dentro de este otro paquete `medico` y finalmente dentro de este creamos la clase `MedicoRepositoryTest.java` y agregamos de momento el siguiente codigo:

        package med.voll.api.domain.medico;

        import org.junit.jupiter.api.Test;

        class MedicoRepositoryTest {

            @Test
            void seleccionarMedicoConEspecialidadEnFecha() {

            }

        }

3.  prubamos corriendo el test que obvio pasara automaticamente ya que no contiene nada y de paso borramos el test que ya viene por defecto de nombre `ApiApplicationTest` ya que no lo utilizaremos

4.  ahora agregaremos una nueva notacion sobre el nombre de la clase:

        @DataJpaTest
        class MedicoRepositoryTest

    si corremos de nuevo el test esta vez fallara

### Explicacion del codigo:

@DataJpaTest es una anotación proporcionada por Spring Boot para pruebas de integración con JPA (Java Persistence API). Cuando agregas @DataJpaTest a una clase de prueba, Spring Boot realiza varias configuraciones y acciones automáticas para facilitar las pruebas relacionadas con la capa de acceso a datos (como repositorios JPA).

Las funcionalidades clave de @DataJpaTest incluyen:

-   Configuración automática del contexto de Spring: @DataJpaTest configura automáticamente el contexto de Spring para cargar solo las clases relevantes para JPA, como los repositorios, entidades, etc. Esto ayuda a mantener las pruebas enfocadas y livianas, evitando la carga innecesaria de componentes no relacionados con JPA.

-   Creación de la base de datos en memoria: Spring Boot configurará automáticamente una base de datos en memoria (como H2) para las pruebas. Esto permite simular el comportamiento de la base de datos real sin afectar el entorno de desarrollo o producción.

-   Configuración de transacciones: @DataJpaTest configura transacciones para tus pruebas. Cada prueba se ejecutará en una transacción separada y se revertirá al final, lo que garantiza que los cambios en la base de datos no se propaguen entre las pruebas.

Ahora, en cuanto a tu situación actual: cuando agregamos @DataJpaTest, es posible que el comportamiento de nuestras pruebas haya cambiado debido a la creación de una base de datos en memoria y la configuración de transacciones.

Si tu prueba no pasó después de agregar @DataJpaTest, podría ser debido a factores como:

-   Datos de prueba necesarios: Para probar la consulta seleccionarMedicoConEspecialidadEnFecha, es posible que necesites datos específicos en tu base de datos en memoria para que la consulta funcione como se espera.

-   Transacciones y rollback: Si estás realizando cambios en la base de datos durante la prueba (por ejemplo, insertando datos), esos cambios se revertirán al final de la transacción. Esto puede afectar tus expectativas si esperas que los datos persistan.

-   Configuraciones predeterminadas: Spring Boot puede proporcionar configuraciones predeterminadas para la base de datos en memoria y las transacciones, que pueden no coincidir con tu entorno de desarrollo normal.

## Continuando...

## Ignorar el paso 5

5.  para solucionar el problema tendremos que ir a <a href="https://start.spring.io/">spring initialzr</a> y agregaremos la dependencia para la base de datos h2

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

## Correccion

`Agregar la dependencia de la base de datos de h2 no soluciona nada solo es un ejemplo inutil dado en el curso del video lo recomendado es que si la agregaste la elimines o nos dara problemas`

## Continuando...

6.  para solucionar el problema solo debemos agregar una nueva notacion:

        package med.voll.api.domain.medico;

        import org.junit.jupiter.api.Test;
        import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
        import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

        @DataJpaTest
        @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
        class MedicoRepositoryTest

## Explicacion del codigo:

Cuando realizas pruebas con @DataJpaTest, Spring Boot configura automáticamente una base de datos en memoria para simular las operaciones de la base de datos. Esto es excelente para mantener las pruebas aisladas y rápidas, ya que no afectará tus entornos de desarrollo o producción. Sin embargo, en algunos casos, es posible que necesites que tus pruebas interactúen con una base de datos real, por ejemplo, para probar características específicas de la base de datos o para asegurarte de que tus consultas funcionen en un entorno lo más parecido posible al entorno de producción.

La anotación @AutoConfigureTestDatabase se utiliza para configurar cómo se debe reemplazar la base de datos durante las pruebas. Por defecto, cuando usas @DataJpaTest, Spring Boot reemplaza automáticamente la base de datos con una base de datos en memoria. Sin embargo, si deseas que las pruebas interactúen con una base de datos real, debes cambiar esta configuración.

El uso de @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) en tu prueba de repositorio de médico deshabilita el reemplazo automático de la base de datos. En otras palabras, le estás diciendo a Spring Boot que no reemplace la base de datos configurada y que utilice la base de datos real, que está configurada en tu aplicación principal.

Esto es útil en situaciones en las que necesitas probar consultas específicas, características de la base de datos u otros aspectos que pueden depender de la configuración real de la base de datos. Al deshabilitar el reemplazo automático de la base de datos, aseguras que tu prueba se ejecute en un entorno más parecido al de producción, lo que puede ayudarte a detectar posibles problemas o incompatibilidades.

## Continuando...

7.  Algo que siempre debemos tener presente es el hecho de que tenemos que tener la base de datos de test separada de la de produccion ya que buscamos estar probando, mandando datos erroneos o a veces borrar toda la base de datos

8.  Para hacer algo como lo anterormente mencionado podemos crear un archivo nuevo como el `application.properties` pero para este ejemplo usaremos el formato `.yml` asi que dentro de la carpeta de `resources` crearemos el archivo `application-test.yml` y simplemente agregaremos lo siguiente:

        spring:
            datasource:
                url: jdbc:mysql://localhost:3307/vollmed_api_test?createDatabaseIfNotExist=true&serverTimezone=UTC
                username: root
                password: Pantera09?

    notaremos que en url tenemos agregado `?createDatabaseIfNotExist=true&serverTimezone=UTC` eso es para que en caso de que no encuentre la base de datos que indicamos, cosa que sucedera por que no la hemos creado, la cree de manera automatica y con la zona horaria actual

9.  Volvemos a `MedicoRepositoryTest` y agregaremos un nuevo tag para indicarle que perfil debe usar, osea el que acabamos de crear:

        @DataJpaTest
        @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
        @ActiveProfiles("test")
        class MedicoRepositoryTest

10. Compilamos y despues volvemos a probar el test el cual no deberia fallar y si revisamos la base de datos debe aparecer ahora `vollmed_api_test` en esta

# Testando el repository

Al final el codigo de los tests en `MedicoRespositoryTest` queda de la siguiente manera:

    package med.voll.api.domain.medico;

    import java.time.LocalDateTime;

    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.junit.jupiter.api.Assertions.assertNull;

    import java.time.DayOfWeek;
    import java.time.LocalDate;
    import java.time.temporal.TemporalAdjusters;

    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
    import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
    import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
    import org.springframework.test.context.ActiveProfiles;

    import med.voll.api.domain.consulta.Consulta;
    import med.voll.api.domain.direccion.DatosDireccion;
    import med.voll.api.domain.paciente.DatosRegistroPaciente;
    import med.voll.api.domain.paciente.Paciente;

    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ActiveProfiles("test")
    class MedicoRepositoryTest {

        @Autowired
        private MedicoRepository medicoRepository;

        @Autowired
        private TestEntityManager em;

        @Test
        @DisplayName("Deberia retornar null cuando el medico se encuentre en consulta con otro paciente en ese horario")
        void seleccionarMedicoConEspecialidadEnFechaEscenario1() {

            var proximoLunes10H = LocalDate.now()
                    .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                    .atTime(10, 0);

            var medico = registrarMedico("Jose", "j@mail.com", "123456", Especialidad.CARDIOLOGIA);
            var paciente = registrarPaciente("antonio", "a@mail.com", "123.456.789-09");

            registrarConsulta(medico, paciente, proximoLunes10H);

            var medicoLibre = medicoRepository.seleccionarMedicoConEspecialidadEnFecha(Especialidad.CARDIOLOGIA,
                    proximoLunes10H);

            assertNull(medicoLibre);

        }

        @Test
        @DisplayName("deberia retornar un medico cuando realice la consulta en la base de datos  en ese horario")
        void seleccionarMedicoConEspecialidadEnFechaEscenario2() {

            // given
            var proximoLunes10H = LocalDate.now()
                    .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                    .atTime(10, 0);

            var medico = registrarMedico("Jose", "j@mail.com", "123456", Especialidad.CARDIOLOGIA);

            // when
            var medicoLibre = medicoRepository.seleccionarMedicoConEspecialidadEnFecha(Especialidad.CARDIOLOGIA,
                    proximoLunes10H);

            // then
            assertEquals(medicoLibre, medico);
        }

        private void registrarConsulta(Medico medico, Paciente paciente, LocalDateTime fecha) {
            em.persist(new Consulta(medico, paciente, fecha));
        }

        private Medico registrarMedico(String nombre, String email, String documento, Especialidad especialidad) {
            var medico = new Medico(datosMedico(nombre, email, documento, especialidad));
            em.persist(medico);
            return medico;
        }

        private Paciente registrarPaciente(String nombre, String email, String documento) {
            var paciente = new Paciente(datosPaciente(nombre, email, documento));
            em.persist(paciente);
            return paciente;
        }

        private DatosRegistroMedico datosMedico(String nombre, String email, String documento, Especialidad especialidad) {
            return new DatosRegistroMedico(
                    nombre,
                    email,
                    "61999999999",
                    documento,
                    especialidad,
                    datosDireccion());
        }

        private DatosRegistroPaciente datosPaciente(String nombre, String email, String documento) {
            return new DatosRegistroPaciente(
                    nombre,
                    email,
                    "61999999999",
                    documento,
                    datosDireccion());
        }

        private DatosDireccion datosDireccion() {
            return new DatosDireccion(
                    " loca",
                    "azul",
                    "acapulpo",
                    "321",
                    "12");
        }

    }

### Para explicaciones detalladas usar ChatGPT

# Testeando error 400

1.  Creamos dentro del paquete api de los test un nuevo paquete `controller`

2.  dentro de este creamos una nueva clase test `ConsultaControllerTest` y agregamos el siguiente codigo:

        package med.voll.api.controller;

        import med.voll.api.domain.consulta.AgendaDeConsultaService;
        import med.voll.api.domain.consulta.DatosAgendarConsulta;
        import med.voll.api.domain.consulta.DatosDetalleConsulta;
        import med.voll.api.domain.medico.Especialidad;
        import org.junit.jupiter.api.DisplayName;
        import org.junit.jupiter.api.Test;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
        import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
        import org.springframework.boot.test.context.SpringBootTest;
        import org.springframework.boot.test.json.JacksonTester;
        import org.springframework.boot.test.mock.mockito.MockBean;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.MediaType;
        import org.springframework.security.test.context.support.WithMockUser;
        import org.springframework.test.context.ActiveProfiles;
        import org.springframework.test.web.servlet.MockMvc;

        import java.time.LocalDateTime;

        import static org.assertj.core.api.Assertions.assertThat;
        import static org.junit.jupiter.api.Assertions.assertEquals;
        import static org.mockito.ArgumentMatchers.any;
        import static org.mockito.Mockito.when;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

        import java.time.LocalDateTime;

        import static org.junit.jupiter.api.Assertions.assertEquals;

        @SpringBootTest
        @AutoConfigureMockMvc
        @AutoConfigureJsonTesters
        @ActiveProfiles("test")
        @SuppressWarnings("all")
        class ConsultaControllerTest {

            @Autowired
            private MockMvc mvc;

            @Autowired
            private JacksonTester<DatosAgendarConsulta> agendarConsultaJacksonTester;

            @Autowired
            private JacksonTester<DatosDetalleConsulta> detalleConsultaJacksonTester;

            @MockBean
            private AgendaDeConsultaService agendaDeConsultaService;

            @Test
            @DisplayName("deberia retornar estado http 400 cuando los datos ingresados sean invalidos")
            @WithMockUser
            void agendarEscenario1() throws Exception {
                // given //when
                var response = mvc.perform(post("/consultas")).andReturn().getResponse();

                // then
                assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
            }

            @Test
            @DisplayName("deberia retornar estado http 200 cuando los datos ingresados son validos")
            @WithMockUser
            void agendarEscenario2() throws Exception {
                // given
                var fecha = LocalDateTime.now().plusHours(1);
                var especialidad = Especialidad.CARDIOLOGIA;
                var datos = new DatosDetalleConsulta(null, 2L, 5L, fecha);

                // when
                when(agendaDeConsultaService.agendar(any())).thenReturn(datos);

                var response = mvc.perform(post("/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(agendarConsultaJacksonTester.write(new DatosAgendarConsulta(null, 2L, 5L, fecha, especialidad))
                                .getJson()))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();

                // then
                assertEquals(HttpStatus.OK.value(), response.getStatus());

                var jsonEsperado = detalleConsultaJacksonTester.write(datos).getJson();
                assertEquals(jsonEsperado, response.getContentAsString());
            }

        }

### Para explicaciones preguntar a chatGPT
