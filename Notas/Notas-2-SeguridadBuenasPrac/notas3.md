# Interceptando el Requests

Para el poder implementar los tokens de manera que se necesiten para poder hacer cualquiera de nuestras peticiones involucra el hacer algo similar a lo de errores, osea un solo metodo global para aplicar a todos los request, para no tener que aplicarlos de uno en uno.

Bajo esa idea entra el concepto de filters, los cuales si bien spring tiene los suyos internamente, nosotros podemos incorporar los nuestros antes de que entren las peticiones a los de spring, algo asi como un doble filtrado donde nosotros creamos el primero a nuestras necesidades y conveniencias.

## Notas Extras:

### Para saber más: filters

Filter es una de las características que componen la especificación Servlets, que estandariza el manejo de solicitudes y respuestas en aplicaciones web en Java. Es decir, dicha función no es específica de Spring y, por lo tanto, puede usarse en cualquier aplicación Java.

Es una característica muy útil para aislar códigos de infraestructura de la aplicación, como por ejemplo, seguridad, logs y auditoría, para que dichos códigos no se dupliquen y se mezclen con códigos relacionados con las reglas comerciales de la aplicación.

Para crear un Filter, simplemente cree una clase e implemente la interfaz Filter en ella (paquete jakarta.servlet). Por ejemplo:

    @WebFilter(urlPatterns = "/api/**")
    public class LogFilter implements Filter {

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            System.out.println("Requisição recebida em: " + LocalDateTime.now());
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

El método doFilter es llamado por el servidor automáticamente, cada vez que este filter tiene que ser ejecutado, y la llamada al método filterChain.doFilter indica que los siguientes filters, si hay otros, pueden ser ejecutados. La anotación @WebFilter, agregada a la clase, indica al servidor en qué solicitudes se debe llamar a este filter, según la URL de la solicitud.

En el curso, usaremos otra forma de implementar un filter, utilizando los recursos de Spring que facilitan su implementación.

# Creando el Security Filter

1.  Para comenzar la creacion de nuestros filtros para interceptar las request iremos al paquete `infra` y dentro de este al de `security` y crearemos la nueva clase de nombre `SecurityFilter.java`

2.  Vamos a agregar el siguiente codigo:

        package med.voll.api.infra.security;

        import java.io.IOException;

        import org.springframework.stereotype.Component;
        import org.springframework.web.filter.OncePerRequestFilter;

        import jakarta.servlet.FilterChain;
        import jakarta.servlet.ServletException;
        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;

        @Component
        public class SecurityFilter extends OncePerRequestFilter {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                System.out.println("El filtro esta siendo llamado");
            }

        }

3.  Compilamos y si todo salio bien al momento de hacer algun request deberiamos ver el mensaje por consola, pero también notaremos que por ejemplo si usamos el GET de listado medicos no nos retornara nada en el body, solo se nos indica un estatus de 200, lo cual indica que vamos bien

### Explicacion del codigo:

    package med.voll.api.infra.security;

    import java.io.IOException;

    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;

    @Component
    public class SecurityFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            System.out.println("El filtro está siendo llamado");
        }

    }

Este código define una clase llamada SecurityFilter que extiende la clase OncePerRequestFilter. OncePerRequestFilter es una clase de Spring que se encarga de ejecutar el filtro de seguridad una vez por cada solicitud entrante. Los filtros de seguridad son componentes esenciales en aplicaciones web que permiten implementar medidas de seguridad personalizadas.

Aquí está la explicación detallada de las partes clave del código:

#### Importaciones:

-   Importamos las clases necesarias para implementar el filtro, como OncePerRequestFilter, HttpServletRequest y HttpServletResponse. Estas clases son proporcionadas por el paquete jakarta.servlet.

#### Anotación @Component:

-   La anotación @Component es una anotación de Spring que marca la clase SecurityFilter como un componente administrado por Spring. Esto permite que Spring lo maneje y lo inyecte donde sea necesario.

#### Clase SecurityFilter:

-   Esta clase define un filtro personalizado de seguridad.

-   Al extender OncePerRequestFilter, garantizamos que el filtro se ejecute una vez por cada solicitud entrante, independientemente de cuántos otros filtros estén presentes en la cadena de filtros.

#### Método doFilterInternal:

-   Este método es donde implementamos la lógica del filtro de seguridad.

-   La anotación @Override indica que estamos sobrescribiendo el método doFilterInternal de la clase base OncePerRequestFilter.

-   Los parámetros HttpServletRequest request y HttpServletResponse response representan la solicitud entrante y la respuesta saliente respectivamente.

-   El parámetro FilterChain filterChain es un objeto que permite pasar la solicitud y la respuesta a través de la cadena de filtros.

-   Dentro de este método, hemos colocado una simple instrucción System.out.println("El filtro está siendo llamado"); para indicar que el filtro se está ejecutando.

En resumen, este código define un filtro de seguridad básico que imprimirá un mensaje en la consola cada vez que se invoque. En una aplicación real, en lugar de imprimir un mensaje, podrías implementar lógica de seguridad personalizada, como verificar tokens JWT, autorización de acceso, prevención de ataques y más.

## Continuando...

4.  Ahora vamos a modificar el metodo agregando una nueva linea de codigo:

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            filterChain.doFilter(request, response);

        }

### Explicacion del codigo:

En el método doFilterInternal, se invoca filterChain.doFilter(request, response). Esta llamada es crucial porque permite que la solicitud actual y la respuesta pasen a través de la cadena de filtros y lleguen a su destino final, ya sea una parte de la aplicación o la respuesta enviada al cliente. En resumen, esta línea permite que el flujo de la solicitud continúe su curso normal a través de los demás filtros y finalmente alcance su destino.

Aquí hay un desglose de lo que hace esta línea:

-   filterChain: Es un objeto FilterChain que se pasa como parámetro al método doFilterInternal. Este objeto representa la cadena de filtros que se aplican a una solicitud.

-   doFilter(request, response): Este método es llamado en el objeto filterChain para permitir que la solicitud y la respuesta sigan avanzando a través de la cadena de filtros. Básicamente, lo que hace es pasar la solicitud y la respuesta al siguiente filtro en la cadena o, si no hay más filtros, a la parte de la aplicación que manejará la solicitud.

En resumen, esta línea de código asegura que la solicitud continúe su recorrido a través de los filtros y alcance su destino final, asegurando que todos los filtros necesarios se apliquen adecuadamente antes de que la solicitud sea manejada y se genere una respuesta. Sin esta línea, la solicitud se detendría en este filtro y no avanzaría más en la cadena de filtros ni alcanzaría su destino final.

## Continuando...

5. Compilamos y si volvemos a probar los request veremos que ya una vez mas podemos ver los body como el caso del GET listado medicos

# Obteniendo el token

1.  Para comenzar a probar el asunto de los token con los request primero vayamos al thunder o insomnia, en estos iremos a la peticion GET listado medicos por ejemplo (previamente deberemos generar un token nuevo en caso de ser necesario con el POST login y copiarlo)

2.  entre las opciones del GET listado veremos una de nombre `Auth` el cual tiene distinas opciones de autenticaciones, nosotros nos concierne la de `Bearer` o `Bearer Token`

3.  ya en dicha opcion pegaremos nuestro token y por el momento en la opcion de `Token Prefix` o simplemente `Prefix` la dejaremos vacia, al menos por ahora

4.  ahora necesitamos hacer uso de los header para obtener el token, para ello y por estandar uremos al `SecurityFilter` y modificaremos el metodo de la siguiente manera:

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            // Obtener el token del header
            var token = request.getHeader("Authorization");
            System.out.println(token);

            filterChain.doFilter(request, response);

        }

5.  Por el momento solo nos interesa ver si se esta obteniendo el token del header asi que compilamos y probamos el GET de listado medicos y en consola deberiamos ver el token que agregamos en `Auth`

### Explicacion del codigo:

-   request.getHeader("Authorization"): En esta línea, se está extrayendo el encabezado de la solicitud llamado "Authorization". Este encabezado es comúnmente utilizado para enviar información de autenticación y autorización en las solicitudes HTTP.

-   .replace("Bearer ", ""): Una vez que se obtiene el valor del encabezado "Authorization", este puede estar en formato "Bearer token", donde "Bearer" es un tipo de autenticación y "token" es el token real. El método replace se utiliza para eliminar la palabra "Bearer" y dejar solo el token real.

-   System.out.println(token): Aquí se imprime el token en la consola. Esto es solo para fines de depuración y te permite ver el token que se está utilizando.

-   filterChain.doFilter(request, response): Finalmente, se llama al método doFilter del objeto filterChain para permitir que la solicitud y la respuesta continúen su flujo normal a través de la cadena de filtros. En este punto, el filtro de seguridad ya ha extraído y procesado el token del encabezado de la solicitud.

Ahora, en cuanto a la autenticación con el token Bearer:

La autenticación con el token Bearer es un mecanismo común para asegurar las API web. En este enfoque, el cliente (por ejemplo, una aplicación móvil o un navegador web) envía un token en el encabezado "Authorization" de sus solicitudes HTTP. Este token es emitido por el servidor de autenticación durante el proceso de inicio de sesión y se utiliza para identificar y autenticar al usuario en cada solicitud subsiguiente.

El token Bearer es una cadena alfanumérica larga que actúa como una credencial y se incluye en el encabezado de la solicitud de esta manera:

    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

En el filtro de seguridad, al extraer y procesar el token Bearer, puedes verificar su validez, contenido y, en función de ello, permitir o denegar el acceso a la solicitud. En tu caso, estás extrayendo el token Bearer del encabezado "Authorization" y lo estás imprimiendo en la consola con fines de depuración. Luego, la línea filterChain.doFilter(request, response) permite que la solicitud continúe su flujo normal.

## Continuando...

6.  Modificamos un poco más el metodo para casos en que el token falle o algo por el estilo con un clasico if:

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            // Obtener el token del header
            var token = request.getHeader("Authorization");

            if (token == "" || token == null) {
                throw new RuntimeException("El token enviado no es valido");
            }

            token = token.replace("Bearer ", "");

            System.out.println(token);

            filterChain.doFilter(request, response);

        }

7.  Ahora nos queda validar si el token para saber si permitiremos el request o bloquearlo

# Validando el token

1.  Para poder validar el token primero deberemos ir al `TokenService.java` y agregar un metodo nuevo, para ello nos vamos a inspirar en el metodo que nos da el readme del repositorio de la libreria de <a href="https://github.com/auth0/java-jwt">JWT auth0</a> en la seccion de `Verify a JWT`:

        public String getSubject(String token) {
                DecodedJWT verifier = null;
                try {
                    Algorithm algorithm = Algorithm.HMAC256(apiSecret);
                    verifier = JWT.require(algorithm)
                            .withIssuer("voll med")
                            .build()
                            .verify(token);

                    verifier.getSubject();
                } catch (JWTVerificationException exception) {
                    // Invalid signature/claims
                }

                if (verifier == null) {
                    throw new RuntimeException("Verifier invalido");
                }
                return verifier.getSubject();
        }

### Explicacion del codigo:

-   DecodedJWT verifier = null;: Se declara una variable verifier de tipo DecodedJWT, que será utilizada para almacenar el resultado de la verificación del token.

-   try { ... } catch (JWTVerificationException exception) { ... }: Se realiza un bloque try en el que se intenta verificar el token. Dentro del bloque, se crea una instancia de Algorithm utilizando el mismo secreto (apiSecret) que se usó para generar el token. Luego se utiliza el método JWT.require(algorithm) para definir el algoritmo requerido para la verificación. Se especifica el emisor (issuer) y se construye el verificador del token con .build().verify(token).

-   verifier.getSubject();: Dentro del bloque try, esta línea intenta obtener el sujeto (subject) del token. Sin embargo, esta línea no tiene un efecto útil ya que el resultado de esta llamada no se asigna ni se utiliza más adelante. Deberías remover esta línea ya que no es necesaria.

-   if (verifier.getSubject() == null) { ... }: Después del bloque try, se verifica si el sujeto del token (verifier.getSubject()) es nulo. Si el sujeto es nulo, se lanza una excepción con el mensaje "Verifier invalido". Esto indica que el token no pudo ser verificado correctamente.

## Continuando...

2.  Volvemos a nuestro `SecurityFilter` y modificamos un poco el codigo:

        @Component
        public class SecurityFilter extends OncePerRequestFilter {

            @Autowired
            private TokenService tokenService;

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {

                // Obtener el token del header
                var token = request.getHeader("Authorization");// .replace("Bearer ", "");

                if (token == "" || token == null) {
                    throw new RuntimeException("El token enviado no es valido");
                }

                token = token.replace("Bearer ", "");

                System.out.println(token);
                System.out.println(tokenService.getSubject(token)); // este usuario tiene sesion?

                filterChain.doFilter(request, response);

            }

        }

### Explicacion del codigo:

-   @Autowired private TokenService tokenService;: Inyectas el servicio TokenService en el filtro. Esto te permite utilizar las funcionalidades proporcionadas por TokenService en este filtro.

-   var token = request.getHeader("Authorization");// .replace("Bearer ", "");: Obtiene el valor del encabezado "Authorization" de la solicitud HTTP. Esto debería contener el token de autenticación.

-   if (token == "" || token == null) { ... }: Verificas si el token es nulo o está vacío. Si es así, lanzas una excepción indicando que el token no es válido.

-   token = token.replace("Bearer ", "");: Si el token contiene el prefijo "Bearer ", lo eliminas para obtener solo el token en sí.

-   System.out.println(token);: Imprimes el token en la consola para verificar su contenido.

-   System.out.println(tokenService.getSubject(token)); // este usuario tiene sesion?: Utilizas el servicio TokenService para verificar el token y obtener el sujeto (subject) del token. Esta verificación valida la firma del token y verifica su validez. Si la verificación es exitosa, se imprime el sujeto del token en la consola.

-   filterChain.doFilter(request, response);: Finalmente, pasas la solicitud y la respuesta al siguiente filtro en la cadena (o a la aplicación real si este es el último filtro). Esto permite que la solicitud continúe su procesamiento normal.

-   En resumen, este filtro se encarga de verificar el token de autenticación en cada solicitud entrante. Si el token es válido, permite que la solicitud continúe su procesamiento. Si el token no es válido o no está presente, lanza una excepción. Además, utiliza el servicio TokenService para verificar y obtener información del token.

### Nota extra:

Hasta este punto, hemos estado construyendo y configurando las bases de autenticación y seguridad en tu aplicación utilizando tokens JWT. Sin embargo, todavía no hemos implementado la lógica para verificar si el usuario tiene una sesión activa. Esto se debe a que hemos estado enfocándonos en establecer la infraestructura básica de autenticación y verificación de tokens.

El proceso hasta ahora ha incluido la generación de tokens JWT basados en la autenticación del usuario y la verificación de su validez. Estos tokens han sido utilizados para representar la autenticidad del usuario en cada solicitud. Sin embargo, en este punto, no hemos incluido la lógica adicional para validar si un usuario específico tiene una sesión activa o no.

En futuros pasos, podríamos considerar la incorporación de lógica adicional para verificar si el usuario posee una sesión válida o no. Esto podría implicar la consulta de una base de datos o algún otro mecanismo para verificar si el usuario tiene permiso para acceder a ciertos recursos o realizar ciertas acciones. En resumen, hasta ahora hemos estado estableciendo la base para la autenticación, y la validación de la sesión es una parte crucial de la seguridad general que podríamos abordar en futuras etapas de desarrollo.

## Continuando...

3. Compilamos y volvemos a probar la peticion GET y ya no solo aparece nuestro token si no que tambien nuestro login `luis.emmanuel`, indicando que todo va bien

# Liberando el acceso login

1.  Hay un error importante que surge de haber hecho lo anterior y es que el token tambien se nos es solicitado al momento de hacer el POST login, cuando eso siempre debe estar libre para todo el mundo

2.  para arreglar lo anterior iremos al `SecurityConfigurations.java` y modificaremos en gran medida el metodo se `securityFilterChain`:

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
            return httpSecurity
                .csrf(csrf -> csrf.disable())  // Desactiva la protección CSRF
                .sessionManagement(sessionManagement -> sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Establece la política de creación de sesiones como stateless
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                    .requestMatchers(HttpMethod.POST, "/login").permitAll()  // Permite el acceso a /login sin autenticación
                    .anyRequest().authenticated())  // Requiere autenticación para todas las demás solicitudes
                .build();  // Construye la configuración de seguridad
        }

### Explicacion del codigo:

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .csrf(csrf -> csrf.disable())  // Desactiva la protección CSRF
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Establece la política de creación de sesiones como stateless
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers(HttpMethod.POST, "/login").permitAll()  // Permite el acceso a /login sin autenticación
                .anyRequest().authenticated())  // Requiere autenticación para todas las demás solicitudes
            .build();  // Construye la configuración de seguridad
    }

-   csrf(csrf -> csrf.disable()): Desactiva la protección CSRF (Cross-Site Request Forgery) que ayuda a prevenir ataques de falsificación de solicitudes en sitios cruzados. En este caso, se desactiva porque estás utilizando autenticación basada en tokens JWT, que no requiere CSRF.

-   sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)): Configura la gestión de sesiones. Establece la política de creación de sesiones como "stateless", lo que significa que Spring Security no creará ni mantendrá sesiones de usuario en el servidor. Esta es una característica importante para aplicaciones RESTful y basadas en JWT.

-   authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(HttpMethod.POST, "/login").permitAll().anyRequest().authenticated()): Configura las autorizaciones para diferentes tipos de solicitudes. En este caso:

    -   requestMatchers(HttpMethod.POST, "/login").permitAll(): Permite que cualquiera pueda acceder a la ruta de inicio de sesión (/login) sin requerir autenticación.

    -   anyRequest().authenticated(): Requiere autenticación para todas las demás solicitudes. Esto asegura que solo los usuarios autenticados puedan acceder a las demás rutas de tu aplicación.

-   build(): Finaliza la construcción de la configuración de seguridad y devuelve un objeto SecurityFilterChain que representa la configuración completa.

Este método configura las reglas de seguridad para tu aplicación Spring Boot, definiendo cómo se debe manejar la autenticación y autorización de las solicitudes entrantes. La combinación de la desactivación de CSRF, la creación de sesiones stateless y las autorizaciones adecuadas es esencial para asegurar una autenticación basada en tokens JWT efectiva y segura.

## Continuando...

3.  Si compilamos y volvemos a intentar el POST login nos daremos cuenta que ahora nos da un error 403 Forbbiden y eso es por como tenemos configurado el `SecurityFilter`

4.  Como el problema esta justo en este if:

        if (token == "" || token == null) {
            throw new RuntimeException("El token enviado no es valido");
        }

    Vamos a tener que hacer unos cambios en nuestro metodo quedando de la siguiente manera:

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            // Obtener el token del header
            var token = request.getHeader("Authorization");// .replace("Bearer ", "");

            if (token != null) {
                token = token.replace("Bearer ", "");

                System.out.println(token);
                System.out.println(tokenService.getSubject(token)); // este usuario tiene sesion?
            }

            filterChain.doFilter(request, response);

        }

5.  Compilamos, probamos el POST login y veremos que ya de nuevo podemos generar y ver el nuevo token, claro podemos revisar de nuevo otro request y veremos que fallan aun si colocamos el nuevo token nos dan error 403 Forbbiden

6.  Para resolver el problema anterior si bien esto no lo soluciona es bueno tenerlo asi que en el `TokenService` el metodo de getSubject() agregaremos una pequeña validacion quedando de la siguiente manera:

7.  Luego de eso revisando el codigo (en el video por medio de impresion de mensajes a consola) vemos que nuestro metodo de `doFilterInternal` de nuestro `SecurityFilter.java` ya NO esta siendo llamado

8.  Lo que esta sucediendo es que el filter de Spring esta siendo llamado antes que nestro filtro, lo cual no debe ser ya que por eso nuestros request son 403 y nuestro `doFilterInternal` no esta siendo aplicado, por si las dudas este es el filtro de Spring:

        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .anyRequest().authenticated())
                .build();

9.  asi que en ese filtro de spring debemos agregar que queremos aplicar nuestro filtro antes del suyo, por lo que el metodo de `securityFilterChain` de `SecurityConfigurations.java` queda de la siguiente manera:

        @Autowired
        private SecurityFilter securityFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
            return httpSecurity
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(sessionManagement -> sessionManagement
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                            .requestMatchers(HttpMethod.POST, "/login").permitAll()
                            .anyRequest().authenticated())
                    .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        }

10. Mientras que en el `SecurityFilter.java` modificamos el metodo de nuestro filtro:

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            // Obtener el token del header
            var authHeader = request.getHeader("Authorization");// .replace("Bearer ", "");

            if (authHeader != null) {
                var token = authHeader.replace("Bearer ", "");
                var subject = tokenService.getSubject(token);
                if (subject != null) {
                    var usuario = usuarioRepository.findByLogin(subject);

                    // Forzamos inicio de sesion
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);

        }

### Explicacion del codigo:

#### SecurityConfigurations.java:

En este método, se ha agregado .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) para indicarle a Spring Security que deseas aplicar tu propio filtro de seguridad (securityFilter) antes del filtro estándar UsernamePasswordAuthenticationFilter. Esto es crucial para que tu filtro personalizado se ejecute antes de que se intente autenticar a través del filtro estándar.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // ... otras configuraciones ...
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

#### SecurityFilter.java:

En este filtro personalizado, se realiza lo siguiente:

-   Se obtiene el token del encabezado "Authorization" de la solicitud entrante.

-   Si se encuentra un token en el encabezado, se extrae el "subject" del token (que suele ser el nombre de usuario o un identificador único).

-   Si se encuentra un "subject", se busca al usuario correspondiente en la base de datos utilizando el método usuarioRepository.findByLogin(subject).

-   Luego, se crea una instancia de UsernamePasswordAuthenticationToken y se establece en el contexto de seguridad (SecurityContextHolder.getContext().setAuthentication(authentication)) para forzar el inicio de sesión. Esto es esencial para que el usuario esté autenticado correctamente en la aplicación.

-   Después de establecer la autenticación en el contexto de seguridad, se llama a filterChain.doFilter(request, response) para continuar con la cadena de filtros.

En resumen, este filtro personalizado verifica si hay un token válido en el encabezado de la solicitud y, si es así, autentica al usuario correspondiente y establece la autenticación en el contexto de seguridad. Esto asegura que el usuario pueda acceder a las rutas protegidas correctamente después de iniciar sesión.

Ambos métodos se relacionan al asegurarse de que el filtro personalizado se ejecute antes del filtro estándar de autenticación (UsernamePasswordAuthenticationFilter), lo que permite interceptar y manipular la autenticación según tus necesidades.

## Continuando...

11. Compilamos y probamos de nuevo, veremos que ya otra vez funcionan los http request siempre y cuando tengan el token correcto

## Notas Extras

### Para saber más: control de acceso por url

En la aplicación utilizada en el curso, no tendremos diferentes perfiles de acceso para los usuarios. Sin embargo, esta característica se usa en algunas aplicaciones y podemos indicarle a Spring Security que solo los usuarios que tienen un perfil específico pueden acceder a ciertas URL.

Por ejemplo, supongamos que en nuestra aplicación tenemos un perfil de acceso llamado ADMIN, y solo los usuarios con ese perfil pueden eliminar médicos y pacientes. Podemos indicar dicha configuración a Spring Security cambiando el método securityFilterChain, en la clase SecurityConfigurations, de la siguiente manera:

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().authorizeRequests()
            .antMatchers(HttpMethod.POST, "/login").permitAll()
            .antMatchers(HttpMethod.DELETE, "/medicos").hasRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, "/pacientes").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and().addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

Tenga en cuenta que se agregaron dos líneas al código anterior, indicando a Spring Security que las solicitudes de tipo DELETE de las URL /médicos y /pacientes solo pueden ser ejecutadas por usuarios autenticados y cuyo perfil de acceso es ADMIN.

### Para saber más: control de acceso a anotaciones

Otra forma de restringir el acceso a ciertas funciones, según el perfil del usuario, es usar una función de Spring Security conocida como Method Security, que funciona con el uso de anotaciones en los métodos:

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity detallar(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        return ResponseEntity.ok(new DatosDetalladoMedico(medico));
    }

En el ejemplo de código anterior, el método se anotó con @Secured("ROLE_ADMIN"), de modo que sólo los usuarios con el rol ADMIN pueden activar solicitudes para detallar a un médico. La anotación @Secured se puede agregar en métodos individuales o incluso en la clase, lo que sería el equivalente a agregarla en todos los métodos.

¡Atención! Por defecto esta característica está deshabilitada en Spring Security, y para usarla debemos agregar la siguiente anotación en la clase Securityconfigurations del proyecto:

    @EnableMethodSecurity(securedEnabled = true)

Puede obtener más detalles sobre la función de seguridad del método en la documentación de Spring Security, disponible en:

<a href="https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html">Method Security</a>

# Fin
