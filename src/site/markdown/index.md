# DAW - M7

*Desenvolupament web en entorn servidor*

**Continguts**

1. [Introducció](#Introducci.C3.B3)
2. [Prova UF1](#Prova_UF1)
   1. [Dades](#Dades)
   2. [Autenticació i autorització](#Autenticaci.C3.B3_i_autoritzaci.C3.B3)
   3. [Controlador](#Controlador)
   4. [Vistes](#Vistes)
3. Prova UF3

------

## Introducció

Per a resoldre la prova UF1, s’ha creat un projecte Java amb maven.

S’ha utilitzat Spring com a framework de desenvolupament per tal d’aprofitar els recursos i facilitats que proporciona (IoC, Dependency Injection, etc.)

Per a crear els *JavaBeans* s'ha utilitzat la llibreria [Lombok](https://projectlombok.org/) que simplifica la implementació i redueix els errors típics de la farragosa i mecànica feina de implementar *getters* i *setters*.

Aquesta utilitat disposa d'un _plugin_ per a diferents IDEs de manera que ofereix els mètodes d'accés a les propietats _on the fly_.

S’ha utilitzat el submòdul Spring MVC per a la part Web.

També s’utilitza el submòdul Spring de seguretat per tal de fer el login.

## Prova UF1

Fer un sistema de login i un CRUD sense base de dades, totes les variables i maquetació està adjunta.

S'ha optat, com ja s'ha esmentat, pel framework Spring MVC.

S'han traslladat els models PHP a models HTML amb el llenguatge de plantilles Thymeleaf.

La classe de configuració [cat.albirar.daw.cocktails.CocktailsConfiguration](xref/cat/albirar/daw/cocktails/CocktailsConfiguration.html) és l’encarregada de crear els elements _injectables_ de l’aplicació.

### Dades

Les dades s’han desplaçat de la vista (vars.php) al codi, a un arxiu json (`/cocktails/src/main/resources/cocktails.json`). Aquest arxiu es carrega a un servei específic format per una interfície (contracte) i la implementació.

* Interfície: [cat.albirar.daw.cocktails.service.ICocktailsService](xref/cat/albirar/daw/cocktails/service/ICocktailsService.html)
* Implementació: [cat.albirar.daw.cocktails.service.impl.CocktailsServiceImpl](xref/cat/albirar/daw/cocktails/service/impl/CocktailsServiceImpl.html)

### Autenticació i autorització

L’autenticació i l’autorització es fa amb el mòdul spring-security. Els usuaris amb les passwords encriptades es creen inicialment per codi i s’emmagatzemem en memòria.

A la classe de configuració, el mètode [CocktailsConfiguration.users()](xref/cat/albirar/daw/cocktails/CocktailsConfiguration.html#L54) s’encarrega de crear la infraestructura d’autenticació i assignació de _roles_.

```java
	@Bean
	public UserDetailsService users() {
		// The builder will ensure the passwords are encoded before saving in memory
		UserBuilder userBuilder = User.builder();
		
		UserDetails admin = userBuilder
				.username("admin")
				.password(ADMIN_ENC_PASS)
				.roles("USER")
				.build();
		UserDetails pepe = userBuilder
				.username("pepe")
				.password(PEPE_ENC_PASS)
				.roles("USER")
				.build();
		UserDetails manolo = userBuilder
				.username("manolo")
				.password(MANOLO_ENC_PASS)
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(admin, pepe, manolo);
	}
```

Les constants [ADMIN_ENC_PASS](xref/cat/albirar/daw/cocktails/CocktailsConfiguration.html#L49), [PEPE_ENC_PASS](xref/cat/albirar/daw/cocktails/CocktailsConfiguration.html#L50) i [MANOLO_ENC_PASS](xref/cat/albirar/daw/cocktails/CocktailsConfiguration.html#L51) contenen les passwords encriptades de `vars.php`.

La classe [cat.albirar.daw.cocktails.crypt.CocktailsPasswordEncoder](xref/cat/albirar/daw/cocktails/crypt/CocktailsPasswordEncoder.html) és l’encarregada de codificar les contrasenyes introduïdes pels usuaris per tal de validar-les amb les dels usuaris registrats.

Implementa el digest **SHA-512** i concatena el sufix `SuperSecret.` a les passwords abans de calcular el digest.

S’assigna el _rol_ `USER` a tots els usuaris per tal de definir la política d’autorització.

Aquesta política (paths HTTP i _rols_ que hi poden accedir) es defineix, també, a la classe de configuració, al mètode [CocktailsConfiguration.web()](xref/cat/albirar/daw/cocktails/CocktailsConfiguration.html#L82):

```java
	@Bean
	public SecurityFilterChain web(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
					.mvcMatchers("/").permitAll()
					.mvcMatchers("/list", "/show/*", "/logout").hasRole("USER")
					.anyRequest().denyAll())
			.formLogin(form -> form
					.loginPage("/")
					.defaultSuccessUrl("/", false)
					.failureUrl("/")
					)
			.logout(logout -> logout
		            .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
		            .clearAuthentication(true)
		            .invalidateHttpSession(true)
		            .logoutSuccessUrl("/")
		            )
			;
		return http.build();
	}
```

L’accés a l’arrel (index.html) és permès a tothom, ja que és la _pàgina de login_. Els paths de _list_ i _show_ estan restringits a usuaris autenticats amb el rol _USER_, que és el que hem definit com a rol d’autorització a la gestió d’usuaris.

Es defineix, també, la pàgina de _login_ i la de _logout_.

Per últim, es restringeix l’accés a qualsevol altre _path_.

### Controlador

En el patró MVC de l’aplicació, el controlador està implementat per la classe [cat.albirar.daw.cocktails.controllers.MainController](xref/cat/albirar/daw/cocktails/controllers/MainController.html).

Els mètodes estan anotats per tal d’establir la relació entre el path i mètode http i el mètode de la classe que ho gestiona, per exemple:

```java
@GetMapping("/list")
public String getList(Model model) {
	model.addAttribute("cocktails", cocktailService.findAll());
	return "list";
}
```

Les dades provenen del servei que hem esmentat abans i s’injecta al controlador amb la interfície:

```java
@Controller
public class MainController {
	@Autowired
	private ICocktailsService cocktailService;
//...
}
```

Spring troba la implementació corresponent per l’anotació de la classe (`@Service`) i pel fet que implementa la mateixa interfície desitjada en la injecció (veure [CocktailsServiceImpl](xref/cat/albirar/daw/cocktails/service/impl/CocktailsServiceImpl.html)).

### Vistes

Les vistes estan implementades amb el sistema de plantilles [Thymeleaf (https://www.thymeleaf.org/)](https://www.thymeleaf.org/) que té la virtut d‘integrar-se amb l’html de manera que es pot veure pràcticament renderitzat sense necessitat d’execució.

Els models de dades passat a les vistes permet que les plantilles mostrin el contingut.

En el cas de la pàgina _show_, s’ha afegit un paràmetre de path, que consisteix en afegir el codi del cocktail com a segment final del path. Per exemple, per a veure el detall del cocktail amb _id_ 12562, el path és `/show/12562`. D’aquesta manera, les URLs resulten molt més netes i fàcils d’indexar pels cercadors.

