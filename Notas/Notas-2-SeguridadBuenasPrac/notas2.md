# Controller de autenticacion

1.  Si vamos a nuestro thunder o insomnia, creamos un nuevo metodo POST con el cual le damos el siguiente body:

        {
        "login": "luis.emmanuel",
        "clave": "123456"
        }

2.  Si lo probamos nos dara un error 404, si por que efectivamente aun no hemos creado/mapeado dicho elemento

3.  Dentro de la carpeta controller crearemos la clase `AutenticacionController.java`

4.  Agregamos el siguiente codigo:

        package med.voll.api.controller;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.ResponseEntity;
        import org.springframework.security.authentication.AuthenticationManager;
        import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
        import org.springframework.security.core.Authentication;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import med.voll.api.domain.usuarios.DatosAutenticacionUsuario;

        @RestController
        @RequestMapping("/login")
        public class AutenticacionController {

                @Autowired
                private AuthenticationManager authenticationManager;

                @PostMapping
                public ResponseEntity<Void> autenticarUsuario(DatosAutenticacionUsuario datosAutenticacionUsuario) {
                        Authentication token = new UsernamePasswordAuthenticationToken(datosAutenticacionUsuario.login(),
                                datosAutenticacionUsuario.clave());
                        authenticationManager.authenticate(token);
                        return ResponseEntity.ok().build();
                }

        }

5.  Pero aun debemos hacer un cambio en nuestro `SecurityConfigurations`:

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

6.  Si compilamos y volvemos a intentar el POST de login nos dara ahora un 403 Forbbiden, lo cual es bueno ya que indica que intento hacer el login pero como no hemos creado ningun usuario, osea no encontro nada no pudo iniciar sesion

### Explicacion del codigo:

#### AutenticaciónController:

-   Esta clase es un controlador REST que maneja las solicitudes de autenticación.

-   Anotamos la clase con @RestController para indicar que es un controlador que manejará solicitudes REST.

-   Con @RequestMapping("/login"), estamos mapeando las solicitudes a la URL /login.

#### DatosAutenticacionUsuario:

-   Este es un record (registro) que define una clase inmutable para contener los datos de autenticación del usuario.

-   Tiene dos campos: login y clave (contraseña).

#### SecurityConfigurations:

-   La clase SecurityConfigurations configura la seguridad de la aplicación.

-   El método authenticationManager anotado con @Bean crea y configura el AuthenticationManager, que se utilizará para autenticar a los usuarios.

#### AuthenticationManager en SecurityConfigurations:

-   authenticationManager es un componente central de Spring Security utilizado para autenticar a los usuarios.

-   Estamos definiendo el authenticationManager utilizando el AuthenticationConfiguration proporcionado por Spring Security.

-   El método getAuthenticationManager() devuelve una instancia del AuthenticationManager configurado.

#### AutenticacionController: autenticarUsuario:

-   El método autenticarUsuario se encarga de autenticar a los usuarios que intentan iniciar sesión.

-   Utiliza el AuthenticationManager para autenticar las credenciales del usuario.

-   Crea un UsernamePasswordAuthenticationToken con las credenciales proporcionadas.

-   Luego llama al método authenticate del AuthenticationManager para autenticar el token.

-   Si la autenticación tiene éxito, devuelve una respuesta HTTP 200 OK.

#### En resumen, el flujo es el siguiente:

-   Un cliente hace una solicitud POST a /login con las credenciales del usuario.

-   El controlador AutenticacionController recibe la solicitud y utiliza el AuthenticationManager para autenticar las credenciales.

-   Si la autenticación es exitosa, se devuelve una respuesta 200 OK.

Esta configuración y código garantizan que las solicitudes de inicio de sesión se manejen adecuadamente y que se utilice el AuthenticationManager para autenticar a los usuarios de manera segura.

### Revisando a fondo los metodos:

        @PostMapping
        public ResponseEntity<Void> autenticarUsuario(@RequestBody @Valid DatosAutenticacionUsuario datosAutenticacionUsuario) {
                Authentication token = new UsernamePasswordAuthenticationToken(datosAutenticacionUsuario.login(),
                        datosAutenticacionUsuario.clave());
                authenticationManager.authenticate(token);
                return ResponseEntity.ok().build();
        }

-   @PostMapping: Esta anotación indica que este método manejará solicitudes HTTP POST a la ruta mapeada en la clase (/login en este caso).

-   public ResponseEntity<Void> autenticarUsuario(@RequestBody @Valid DatosAutenticacionUsuario datosAutenticacionUsuario): Esto define el método que manejará las solicitudes de autenticación. Recibe como parámetro un objeto DatosAutenticacionUsuario que contiene las credenciales de autenticación.

-   Authentication token = new UsernamePasswordAuthenticationToken(datosAutenticacionUsuario.login(), datosAutenticacionUsuario.clave()): Aquí estamos creando un objeto UsernamePasswordAuthenticationToken, que es un tipo de token de autenticación que Spring Security utiliza para procesar las credenciales del usuario. Se construye utilizando el nombre de usuario (login) y la contraseña (clave) proporcionados en el objeto DatosAutenticacionUsuario.

-   authenticationManager.authenticate(token): Aquí estamos utilizando el AuthenticationManager (que se inyecta automáticamente) para autenticar el token que creamos. La llamada a authenticate valida las credenciales proporcionadas con las almacenadas en la base de datos o en el sistema de autenticación.

    -   Si las credenciales son válidas, la autenticación tiene éxito y el método se completa sin lanzar excepciones.

    -   Si las credenciales no son válidas, se lanzará una excepción (por ejemplo, BadCredentialsException).

-   return ResponseEntity.ok().build(): Después de autenticar las credenciales, si la autenticación es exitosa, se devuelve una respuesta HTTP 200 OK. Esto significa que las credenciales eran válidas y el usuario ha sido autenticado correctamente.

En resumen, este método es responsable de recibir las credenciales de autenticación del usuario, construir un token de autenticación, utilizar el AuthenticationManager para validar las credenciales y, finalmente, devolver una respuesta HTTP que indica si la autenticación fue exitosa o no.

### otro más

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

-   @Bean: Esta anotación indica que este método crea y configura un bean que será gestionado por Spring. En este caso, el bean creado será un AuthenticationManager.

-   public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception: Esto define el método que crea y configura el AuthenticationManager. Recibe como parámetro una instancia de AuthenticationConfiguration.

-   return authenticationConfiguration.getAuthenticationManager();: Aquí se está utilizando el AuthenticationConfiguration para obtener y devolver el AuthenticationManager. Esto proporciona una forma de acceder y configurar las políticas de autenticación en Spring Security.

En resumen, este método se encarga de crear y configurar el AuthenticationManager que se utilizará en la autenticación de usuarios. Es un paso fundamental para definir cómo se autenticarán los usuarios en tu aplicación, y proporciona la infraestructura necesaria para manejar las solicitudes de autenticación de manera segura.

# Retornando usarios MySQL

1.  Vamos a agregar manualmente un usario en la base de datos, ya sea con codigo o con las facilidades que nos de las opciones de MySQL, Con ayuda de un generador de Bcrypt hash tenemos lo siguiente:

        login: luis.emmanuel
        clave: 123456 -> $2y$10$bOoGV1PD.t2.L5p8xzZace86bPD19hTDiTcLCWth9bTI5x404bboO

Ahora tenemos un pequeño problema, Spring no sabe realmente cual es el login o la contraseña, ya que de por si desde dentro todo esta en ingles y tenemos cosas como "clave", asi mismo tampoco sabe el algoritmo de hashing que se utilizo para la clave anterior, por lo que aun hay ciertas cosas por hacer, indicar el tipo de hashing, indicar de donde debe sarcar user y password

2.  volvemos a `SecurityConfigurations` y agregamos lo siguiente:

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

3.  volvemos a `Usuario.java` e implementamos una nueva interface de spring que por ende debemos pasar los metodos de la misma y modificaremos sus return quedando de la siguiente manera:

        package med.voll.api.domain.usuarios;

        import java.util.Collection;
        import java.util.List;

        import org.springframework.security.core.GrantedAuthority;
        import org.springframework.security.core.authority.SimpleGrantedAuthority;
        import org.springframework.security.core.userdetails.UserDetails;

        import jakarta.persistence.*;

        import lombok.*;

        @Table(name = "usuarios")
        @Entity(name = "Usuario")
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode(of = "id")
        public class Usuario implements UserDetails {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;
            private String login;
            private String clave;

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority("ROLE_USER"));
            }

            @Override
            public String getPassword() {
                return clave;
            }

            @Override
            public String getUsername() {
                return login;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

        }

4.  Ahora si volvemos a compilar y probamos el metodo POST de login nos dara un estatus 200 indicando que todo salio bien

### Explicacion del codigo:

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

-   @Bean: Esta anotación marca un método como un productor de un bean de Spring. Los beans son objetos gestionados por el contenedor de Spring y se utilizan para definir y configurar diversos componentes en una aplicación.

-   public PasswordEncoder passwordEncoder(): Este método crea y configura un bean de tipo PasswordEncoder, que es una interfaz proporcionada por Spring Security para codificar y verificar contraseñas.

-   return new BCryptPasswordEncoder();: Aquí se crea una instancia de BCryptPasswordEncoder, que es una implementación de PasswordEncoder proporcionada por Spring Security. Esta clase se utiliza para codificar contraseñas de manera segura mediante el algoritmo de hashing bcrypt.

En resumen, al agregar el método passwordEncoder() con la anotación @Bean en tu SecurityConfigurations, estás configurando un bean de tipo BCryptPasswordEncoder que se utilizará para codificar y verificar contraseñas de manera segura en tu aplicación. Esto es fundamental para la autenticación de usuarios, ya que garantiza que las contraseñas almacenadas en la base de datos estén protegidas contra ataques de fuerza bruta y otros tipos de amenazas.

    package med.voll.api.domain.usuarios;

    import java.util.Collection;
    import java.util.List;

    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import jakarta.persistence.*;

    import lombok.*;

    @Table(name = "usuarios")
    @Entity(name = "Usuario")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(of = "id")
    public class Usuario implements UserDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String login;
        private String clave;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        @Override
        public String getPassword() {
            return clave;
        }

        @Override
        public String getUsername() {
            return login;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    }

la implementación de la interfaz UserDetails en la clase Usuario es fundamental para que Spring Security pueda manejar y autenticar usuarios de manera efectiva en tu aplicación. Aquí está una explicación detallada de cada método implementado y su función en el contexto de la autenticación y autorización:

-   getAuthorities(): Este método devuelve una colección de roles y permisos del usuario. Los roles y permisos son utilizados por Spring Security para determinar qué acciones están permitidas para un usuario. En este caso, estás retornando un único rol "ROLE_USER".

-   getPassword(): Este método devuelve la contraseña del usuario. Spring Security utiliza esta contraseña para compararla con la contraseña proporcionada durante el proceso de autenticación.

-   getUsername(): Este método devuelve el nombre de usuario del usuario. Spring Security utiliza este nombre de usuario para identificar al usuario durante el proceso de autenticación.

-   isAccountNonExpired(): Indica si la cuenta del usuario no ha expirado. En este caso, siempre se devuelve true, lo que significa que las cuentas no tienen fecha de expiración.

-   isAccountNonLocked(): Indica si la cuenta del usuario no está bloqueada. En este caso, siempre se devuelve true, lo que significa que las cuentas no están bloqueadas.

-   isCredentialsNonExpired(): Indica si las credenciales del usuario (por ejemplo, la contraseña) no han expirado. En este caso, siempre se devuelve true, lo que significa que las credenciales no tienen fecha de expiración.

-   isEnabled(): Indica si la cuenta del usuario está habilitada. En este caso, siempre se devuelve true, lo que significa que las cuentas están habilitadas.

Al implementar la interfaz UserDetails, la clase Usuario proporciona los detalles necesarios para la autenticación y autorización de los usuarios dentro de tu aplicación. Estos detalles se utilizan para verificar la autenticidad de un usuario y determinar sus roles y permisos. Es importante implementar correctamente los métodos de la interfaz para que Spring Security pueda interactuar de manera adecuada con la clase Usuario durante el proceso de autenticación y autorización.

### profundizando el getAuthorities():

El método getAuthorities() es parte de la implementación de la interfaz UserDetails en la clase Usuario. Su función principal es proporcionar información sobre los roles y permisos asociados a un usuario en el contexto de Spring Security. Estos roles y permisos son utilizados por Spring Security para determinar qué acciones y recursos están permitidos para un usuario autenticado en la aplicación.

En muchos sistemas, es común clasificar a los usuarios en grupos o roles que definen sus niveles de acceso y las funcionalidades a las que pueden acceder. Por ejemplo, podrías tener roles como "ADMIN", "USER", "MODERATOR", etc. Cada rol podría tener permisos específicos para ciertas partes de la aplicación.

En tu implementación específica, estás retornando un único rol `"ROLE_USER" en la colección de autoridades. En el contexto de Spring Security, es una convención añadir el prefijo "ROLE_"` a los nombres de los roles. Esto ayuda a Spring Security a identificar y gestionar mejor los roles.

Cuando un usuario inicia sesión y se autentica en tu aplicación, Spring Security verifica el rol del usuario en función de las autoridades proporcionadas por el método getAuthorities(). Luego, utiliza esta información para determinar si el usuario tiene permiso para acceder a ciertos recursos o realizar ciertas acciones en la aplicación.

En resumen, getAuthorities() es crucial para la configuración de roles y permisos en Spring Security. Proporciona una forma de asignar permisos específicos a los usuarios y controlar su acceso a partes específicas de la aplicación, asegurando que solo tengan acceso a las funcionalidades para las que están autorizados.

### Algo que no notamos:

    package med.voll.api.domain.usuarios;

    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.security.core.userdetails.UserDetails;

    public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

        UserDetails findByLogin(String username);

    }

Antes de implementar la interfaz UserDetails en la clase Usuario, el método findByLogin en UsuarioRepository daba error porque el tipo de retorno esperado era UserDetails, pero la clase Usuario aún no implementaba esa interfaz. En ese momento, la clase Usuario no tenía ninguna relación con UserDetails, por lo que el tipo de retorno no coincidía y generaba un error de incompatibilidad.

El propósito de UsuarioRepository es proporcionar métodos para realizar operaciones relacionadas con la entidad Usuario en la base de datos. En este caso, estás utilizando Spring Data JPA para generar automáticamente consultas CRUD (crear, leer, actualizar, eliminar) para la entidad Usuario. El método findByLogin se utilizaba para buscar un usuario por su nombre de usuario (login en este caso).

Al implementar la interfaz UserDetails en la clase Usuario, se está indicando que la clase Usuario puede funcionar como un objeto de usuario autenticable en Spring Security. Esto incluye métodos como getAuthorities(), getPassword(), getUsername(), etc., que son requeridos por Spring Security para gestionar la autenticación y autorización.

Después de esta implementación, el método findByLogin ahora puede retornar una instancia de UserDetails (que en realidad es la misma instancia de Usuario en este caso). Esto es posible porque Usuario ahora cumple con los requisitos de UserDetails. Con esto, puedes realizar operaciones de autenticación y autorización utilizando directamente la entidad Usuario como parte de la integración con Spring Security.

# Agregando la libreria auth0-jwt

1. Iremos a la pagina de `https://jwt.io/` la cual contiene todas las librerias para Json web token y para una gran cantidad de lenguajes, buscaremos entre los de Java el que fue creado por Auth0, se ve en la esquina inferior izquierda

2. Damos click en `View Repo` y en el readme del repo veremos la seccion de el dependency para maven, lo copiaremos a nuestro pom.xml

# Generando un jwt

1.  Vamos a crear un servicio para comenzar a hacer uso de los jwt, por lo que vamos a el paquete de `security` y dentro de este crearemos la clase `TokenService.java`

2.  En base a al readme agregamos el siguiente codigo:

        package med.voll.api.infra.security;

        import org.springframework.stereotype.Service;

        import com.auth0.jwt.JWT;
        import com.auth0.jwt.algorithms.Algorithm;
        import com.auth0.jwt.exceptions.JWTCreationException;

        @Service
        public class TokenService {

            public String generarToken() {
                try {
                    Algorithm algorithm = Algorithm.HMAC256("123456");
                    return JWT.create()
                            .withIssuer("voll med")
                            .withSubject("luis.emmanuel")
                            .sign(algorithm);
                } catch (JWTCreationException exception) {
                    throw new RuntimeException();
                }
            }

        }

### Explicacion del codigo:

La clase TokenService está diseñada para generar tokens JWT (JSON Web Tokens) utilizados en la autenticación y autorización de aplicaciones. Los tokens JWT son una forma segura de transmitir información entre partes de manera que pueda ser verificada y confiable. En esta clase, estás utilizando la biblioteca Auth0 para generar tokens JWT.

#### Voy a explicar el funcionamiento del método generarToken() paso a paso:

-   Creación del Algoritmo: Primero, se crea un objeto Algorithm utilizando el algoritmo HMAC256 y una clave secreta. Aquí es donde mencionaste que es mala práctica tener la clave secreta (secret) como "123456". Esto es cierto porque las claves secretas deben ser largas, complejas y aleatorias para brindar seguridad. En lugar de incluir la clave secreta en el código fuente, es recomendable almacenarla de manera segura, como en una variable de entorno o un archivo de configuración.

-   Generación del Token: Luego, utilizas el objeto JWT.create() para comenzar a construir el token JWT. Utilizas el método withIssuer() para establecer el emisor del token, que en este caso es "voll med". El método withSubject() se usa para establecer el sujeto del token, que podría ser el identificador único del usuario (por ejemplo, su nombre de usuario).

-   Firma del Token: Finalmente, utilizas el método sign() para firmar el token con el algoritmo y la clave secreta. Esto genera el token JWT final.

Si el proceso de generación del token tiene éxito, se devuelve el token JWT generado. Sin embargo, si ocurre una excepción JWTCreationException, se lanza una RuntimeException para indicar un error en la generación del token.

Para mejorar la seguridad y la práctica de generar tokens JWT, es recomendable seguir estos pasos:

-   Usar una Clave Secreta Fuerte: Genera una clave secreta sólida y almacénala de manera segura, fuera del código fuente.

-   Usar Configuración de Propiedades: Utiliza alguna configuración de propiedades o variables de entorno para almacenar la clave secreta.

-   Usar Herramientas de Gestión de Secretos: En entornos de producción, considera utilizar herramientas de gestión de secretos para almacenar y administrar de manera segura las claves secretas.

-   Generación Dinámica de Tokens: En lugar de simplemente establecer un sujeto fijo, podrías considerar incluir información útil del usuario en el token, como roles, permisos o ID, de manera que el token sea más útil para la autorización.

En resumen, la clase TokenService está destinada a generar tokens JWT para la autenticación y autorización en tu aplicación. Sin embargo, asegúrate de seguir las mejores prácticas de seguridad para garantizar que la generación y el uso de los tokens sean seguros y confiables.

## Continuando...

3. Para hacer uso del metodo anterior vamos a nuestro `AutenticacionController` y modificamos el codigo, quedando de la siguiente manera:

package med.voll.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import med.voll.api.domain.usuarios.DatosAutenticacionUsuario;
import med.voll.api.infra.security.TokenService;

@RestController
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<String> autenticarUsuario(
            @RequestBody @Valid DatosAutenticacionUsuario datosAutenticacionUsuario) {
        Authentication authToken = new UsernamePasswordAuthenticationToken(datosAutenticacionUsuario.login(),
                datosAutenticacionUsuario.clave());
        authenticationManager.authenticate(authToken);

        var JWTtoken = tokenService.generarToken();
        return ResponseEntity.ok(JWTtoken);
    }

}

### Explicacion del codigo:

-   En esta versión modificada, se ha agregado una instancia de TokenService llamada tokenService a la clase AutenticacionController. Esto es esencial para poder llamar al método generarToken() dentro del controlador.

-   Después de autenticar al usuario utilizando authenticationManager.authenticate(authToken), ahora se llama al método generarToken() de tokenService para crear un token JWT válido.

-   En la última línea, se devuelve un ResponseEntity que contiene el token JWT recién generado. Como ahora el token es una cadena de texto (String), ya no necesitas invocar el método .build() en ResponseEntity. La clase ResponseEntity ofrece sobrecargas convenientes para tratar diferentes tipos de respuestas, y cuando el contenido es un String, la sobrecarga .ok(T body) ya se encarga de construir la respuesta adecuada.

#### Eliminación del .build() en ResponseEntity:

En la versión anterior del código, el .build() se usaba para construir explícitamente la respuesta en forma de ResponseEntity. Sin embargo, las últimas versiones de Spring Framework han introducido sobrecargas en los métodos ResponseEntity.ok() para manejar varios tipos de contenido, incluyendo Strings. Estas sobrecargas automáticamente construyen la respuesta adecuada, por lo que ya no necesitas llamar explícitamente al .build() después de agregar el contenido al ResponseEntity.

En resumen, las modificaciones en el método autenticarUsuario() se centran en generar un token JWT utilizando el servicio TokenService y devolverlo como una respuesta en forma de cadena. Además, la eliminación del .build() después de agregar el contenido al ResponseEntity se debe a las mejoras en las sobrecargas de métodos en versiones más recientes de Spring Framework.

## Continuando...

4.  Compilamos y si hacemos el POST de login veremos que ahora nos retorna el token:

        eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ2b2xsIG1lZCIsInN1YiI6Imx1aXMuZW1tYW51ZWwifQ.SbwJ43dV21ONM9mRlvdDgWLGrP644_n42EE9s3icleQ

    Si vamos a la web de <a href="https://jwt.io/">JWT.io</a> y colocamos el token en su Encoded veremos que en la seccion de Decoded nuestros datos correctamente decodificados

5.  Pero aun debemos hacer cambios como NO tener el secret como tal ahi visible en el codigo y que el `withSubject` sea dinamico ya que no todos los usuarios se llaman igual

# Ajustes de generación en jwt

1.  Volvemos a nuestro `TokenService` y comenzaremos a realizar los cambios necesarios:

        @Service
        public class TokenService {

            public String generarToken(Usuario usuario) {
                try {
                    Algorithm algorithm = Algorithm.HMAC256("123456");
                    return JWT.create()
                            .withIssuer("voll med")
                            .withSubject(usuario.getLogin())
                            .withClaim("id", usuario.getId())
                            .withExpiresAt(generarFechaExpiracion())
                            .sign(algorithm);
                } catch (JWTCreationException exception) {
                    throw new RuntimeException();
                }
            }

            private Instant generarFechaExpiracion() {
                return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-05:00"));
            }

        }

2.  Notese que aun no realizamos las modificaciones para secret, eso lo haremos despues

### Explicacion del codigo:

    public String generarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("123456");
            return JWT.create()
                    .withIssuer("voll med")
                    .withSubject(usuario.getLogin())
                    .withClaim("id", usuario.getId())
                    .withExpiresAt(generarFechaExpiracion())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException();
        }
    }

-   En esta versión mejorada del método generarToken(), ahora acepta un objeto Usuario como argumento. Esto es porque el token que se genera contendrá información relacionada con el usuario, como el nombre de usuario (login) y su identificación (id).

-   Se ha utilizado el mismo algoritmo HMAC256 para firmar el token, aunque es recomendable que se utilice un secreto más seguro en lugar de "123456" para mayor seguridad.

-   Se utiliza el método .withSubject(usuario.getLogin()) para establecer el "sujeto" del token, que en este caso es el nombre de usuario del usuario autenticado.

-   Se utiliza .withClaim("id", usuario.getId()) para agregar una reclamación personalizada "id" al token, que contiene el ID del usuario autenticado. Esto puede ser útil para identificar al usuario sin necesidad de decodificar el token.

-   .withExpiresAt(generarFechaExpiracion()) se utiliza para establecer la fecha de expiración del token. En este caso, se utiliza el método generarFechaExpiracion() para calcular una fecha de expiración dos horas después de la hora actual.

-   Finalmente, se firma el token utilizando el algoritmo y se devuelve como una cadena.

### Generando fecha de expiracion del token

    private Instant generarFechaExpiracion() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-05:00"));
    }

-   Este método se introduce para calcular la fecha de expiración del token. En este caso, se está estableciendo una expiración de dos horas después de la hora actual.

-   Se utiliza LocalDateTime.now().plusHours(2) para obtener la fecha y hora actual y agregarle dos horas.

-   Luego, se convierte a Instant utilizando .toInstant(ZoneOffset.of("-05:00")). Aquí, el ZoneOffset se establece en "-05:00", que representa la zona horaria en la que se encuentra el servidor.

En resumen, estos cambios en el método generarToken() y la introducción del método generarFechaExpiracion() permiten generar tokens JWT personalizados que contienen información del usuario y establecer una fecha de expiración adecuada para los tokens. Sin embargo, se debe recordar que usar un secreto seguro y considerar las mejores prácticas de seguridad es crucial al implementar la autenticación con tokens JWT en una aplicación.

## Continuando...

3.  Como ahora le estamos enviando un objeto Usuario a el token debemos modificar el `AutenticacionController`:

        @PostMapping
        public ResponseEntity<String> autenticarUsuario(
                @RequestBody @Valid DatosAutenticacionUsuario datosAutenticacionUsuario) {
            Authentication authToken = new UsernamePasswordAuthenticationToken(datosAutenticacionUsuario.login(),
                    datosAutenticacionUsuario.clave());

            var usuarioAutenticado = authenticationManager.authenticate(authToken);

            var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
            return ResponseEntity.ok(JWTtoken);
        }

### Explicacion del codigo:

#### Obtención del Usuario autenticado:

En esta versión del método, se ha agregado la línea var usuarioAutenticado = authenticationManager.authenticate(authToken);. Una vez que el authToken (token de autenticación) se ha creado con el nombre de usuario y contraseña proporcionados en la solicitud, se procede a autenticar al usuario utilizando el authenticationManager.

El resultado de authenticationManager.authenticate(authToken) es un objeto de tipo Authentication que representa al usuario autenticado. Este objeto contiene información sobre la autenticación exitosa, incluido el objeto Principal, que en este caso es el objeto Usuario que representa al usuario autenticado.

#### Generación del token JWT con el Usuario autenticado:

Después de autenticar con éxito al usuario, se utiliza el objeto usuarioAutenticado.getPrincipal() para obtener el objeto Usuario que representa al usuario autenticado. Esto es posible porque, al implementar la interfaz UserDetails, el objeto Principal es el propio objeto Usuario.

Luego, se llama al método generarToken() del TokenService, pasando el Usuario autenticado como argumento. El método generarToken() utiliza la información del usuario para crear un token JWT personalizado que incluye el nombre de usuario, la identificación y otros detalles.

#### Respuesta con el token JWT:

Una vez que se ha generado el token JWT con éxito, se devuelve una respuesta con el token en el cuerpo utilizando ResponseEntity.ok(JWTtoken). Esto significa que el cliente que hizo la solicitud recibirá el token JWT que puede utilizar para futuras solicitudes autenticadas.

En resumen, estos cambios en el método autenticarUsuario() permiten generar un token JWT basado en la información del usuario autenticado y proporcionar este token como respuesta al cliente. Esto es fundamental para implementar un flujo de autenticación basado en tokens JWT en la aplicación.

## Continuando...

4.  Volvemos a compilar, probar el POST y revisar la decodificacion del token obtenido:

        {
            "iss": "voll med",
            "sub": "luis.emmanuel",
            "id": 1,
            "exp": 1692568229
        }

5.  Como nuestro estandar es consumir DTO y retornar DTO, vamos a agregar un DTO para el token, para ello simplemente crearemos un record dentro del paquete de `security` el cual tendra el nombre de `DatosJWTToken.java`:

        package med.voll.api.infra.security;

        public record DatosJWTToken(String jwtToken) {
        }

6.  Finalmente modificamos levemente el AutenticacionController para hacer uso de dicho DTO:

        @PostMapping
        public ResponseEntity<DatosJWTToken> autenticarUsuario(
                @RequestBody @Valid DatosAutenticacionUsuario datosAutenticacionUsuario) {
            Authentication authToken = new UsernamePasswordAuthenticationToken(datosAutenticacionUsuario.login(),
                    datosAutenticacionUsuario.clave());

            var usuarioAutenticado = authenticationManager.authenticate(authToken);

            var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
            return ResponseEntity.ok(new DatosJWTToken(JWTtoken));
        }

7.  Compilamos de nuevo y al probar el POST veremos que el body ya retornara un json con el token:

        {
            "jwtToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ2b2xsIG1lZCIsInN1YiI6Imx1aXMuZW1tYW51ZWwiLCJpZCI6MSwiZXhwIjoxNjkyNTY4ODk5fQ.wAB7E_n71F1WK52OixhoYakP21diL3tYmXkNm0HPzZw"
        }

8.  Toca resolver el problema de el secret, asi que para ello primero iremos al `application.properties` y agregaremos lo siguiete:

        api.security.secret=${JWT_SECRET:123456}

9.  Lo que le estamos diciendo es que va a acceder a las variables de ambiende de nuestra computadora, por ejemplo podemos abrir la terminal de vscode y usar los siguientes comandos:

    -   export JWT_SECRET=123456
    -   echo $JWT_SECRET

    El primero para crear la variable y darle valor y el segundo para verificar que fue creada correctamente

10. Pero como puede suceder que nuestro IDE no tenga acceso a dichas variables de entorno es que colocamos el :123456, es para darle un valor por defecto

11. Ahora simplemente en el `TokenService` agregamos los siguientes cambios:

        @Service
        public class TokenService {

            @Value("${api.security.secret}")
            private String apiSecret;

            public String generarToken(Usuario usuario) {
                try {
                    Algorithm algorithm = Algorithm.HMAC256(apiSecret);
                    return JWT.create()
                            .withIssuer("voll med")
                            .withSubject(usuario.getLogin())
                            .withClaim("id", usuario.getId())
                            .withExpiresAt(generarFechaExpiracion())
                            .sign(algorithm);
                } catch (JWTCreationException exception) {
                    throw new RuntimeException();
                }
            }

            private Instant generarFechaExpiracion() {
                return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-05:00"));
            }

        }

12. Compilamos y si todo salio en orden al probar el POST login todo funcionara igual de bien previo a los cambios de secret

### Explicacion general de los cambios en secret:

Los cambios que has realizado en relación al manejo del secreto (secret) del token son para mejorar la seguridad y la configuración flexible del token JWT. Vamos a analizar los cambios uno por uno:

#### Configuración del secreto en el archivo application.properties:

La línea api.security.secret=${JWT_SECRET:123456} en el archivo application.properties está configurada para tomar el valor de la variable de entorno JWT_SECRET si está definida, y si no lo está, utilizará el valor por defecto 123456. Esto permite configurar el secreto de manera flexible y segura a través de las variables de entorno.

#### Modificación en el TokenService:

En el servicio TokenService, ahora estás inyectando el valor del secreto desde la variable de entorno api.security.secret mediante la anotación @Value("${api.security.secret}"). Esto garantiza que el secreto se cargue desde la configuración definida en el archivo application.properties o desde las variables de entorno del sistema.

Luego, en el método generarToken(), utilizas este valor de secreto dinámico en lugar del valor fijo "123456". Esto mejora la seguridad, ya que el secreto no está durocodificado en el código y puede ser cambiado sin necesidad de recompilar el código.

En resumen, estos cambios son muy positivos desde el punto de vista de la seguridad y la configuración flexible. La configuración del secreto a través de variables de entorno y su uso en el servicio TokenService permite mantener el secreto seguro y permitir su actualización sin necesidad de modificar el código fuente. Esto es una buena práctica para mantener la seguridad y la mantenibilidad en tu aplicación.

13. Ahora que ya tenemos el token necesitamos modificar los otros request para que solo funcionen si tienen un token valido como el que hemos estado tratando de generar.

# Continuacion en notas3.md
