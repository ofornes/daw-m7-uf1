# DAW - M7

*Desenvolupament web en entorn servidor*

**Continguts**

1. [Introducció](#Introducci.C3.B3)
2. [Prova UF1](#Prova_UF1)
   1. [Dades](#Dades)
   2. [Autenticació i autorització](#Autenticaci.C3.B3_i_autoritzaci.C3.B3)
   3. [Controlador](#Controlador)
   4. [Vistes](#Vistes)
3. [Prova UF3](#Prova_UF3)
   1. [Dades SQL](#Dades_SQL)
   2. [Autenticació i autorització](#Autenticaci.C3.B3_i_autoritzaci.C3.B3)
   3. [Servei de Cocktails](#Servei_de_Cocktails)

------

## Introducció

Per a resoldre les proves UF1 i UF3, s’ha creat un projecte Java amb maven.

S’ha utilitzat Spring com a framework de desenvolupament per tal d’aprofitar els recursos i facilitats que proporciona (IoC, Dependency Injection, etc.)

Per a crear els *JavaBeans* s'ha utilitzat la llibreria [Lombok](https://projectlombok.org/) que simplifica la implementació i redueix els errors típics de la farragosa i mecànica feina de implementar *getters* i *setters*.

Aquesta utilitat disposa d'un _plugin_ per a diferents IDEs de manera que ofereix els mètodes d'accés a les propietats _on the fly_.

S’ha utilitzat el submòdul Spring MVC per a la part Web.

També s’utilitza el submòdul Spring de seguretat per tal de fer el login.

## Prova UF1

> Fer un sistema de login i un CRUD sense base de dades, totes les variables i maquetació està adjunta.

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

## Prova UF3

> Fer el mateix que la UF1 amb una base de dades.

Per a transformar el sistema de la UF1 a un amb base de dades, s’han fet els següents canvis:

1. Afegir una base de dades encastada, en concret [H2](https://h2database.com/html/main.html)
2. Afegir el mòdul JDBC de l’Spring-Security
3. S’ha creat arxius SQL per a inicialitzar la base de dades:
   1. `src/main/resources/db/schema.sql` per a crear les taules
   2. `src/main/resources/db/user_data.sql` per a inserir els usuaris, tal i com es feia a la UF1, però en aquest cas a la taula directament
   3. `src/main/resources/db/cocktails_data.sql` per a inserir la informació dels cocktails. SQL obtingut de transformar el json en SQL
4. S’ha modificat [cat.albirar.daw.cocktails.CocktailsConfiguration](xref/cat/albirar/daw/cocktails/CocktailsConfiguration.html) per a configurar:
   1. `UserDetails` basat en JDBC
   2. Afegir els components `DataSource`, `JdbcTemplate` i `NamedParameterJdbcTemplate` per accedir a la base de dades
5. S’ha modificat [cat.albirar.daw.cocktails.service.impl.CocktailsServiceImpl](xref/cat/albirar/daw/cocktails/service/impl/CocktailsServiceImpl.html) per a utilitzar la base de dades com a font i no pas llistes o mapes en memòria.
6. S’han creat un parell de classes per a l’operació amb la base de dades: 
   1. [cat.albirar.daw.cocktails.service.db.ConstantsCocktailsDb](xref/cat/albirar/daw/cocktails/service/db/ConstantsCocktailsDb.html#ConstantsCocktailsDb)
   2. [cat.albirar.daw.cocktails.service.mappers.CocktailDetailBeanMapper](xref/cat/albirar/daw/cocktails/service/mappers/CocktailDetailBeanMapper.html#CocktailDetailBeanMapper)

### Dades SQL

El canvi de dades en memòria a SQL, implica, a més d’afegir un parell de dependències, crear uns quants elements per a aquesta nova infraestructura.

La base de dades utilitzada és _encastada_. Es crea en moment d’execució i es destrueix en moment d’aturada.

S’han creat tres elements per a disposar de la BD:

Primer el `DataSource`, que és la classe central del sistema JDBC d’accés a BD de Java. En el nostre cas, a més, serveix per a _aixecar_ la base de dades i inicialitzar esquemes i dades. Tot plegat es fa al mètode [CocktailsConfiguration.dataSource()](xref/cat/albirar/daw/cocktails/CocktailsConfiguration.html#L87):

```java
	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("db/schema.sql")
                .addScripts("db/user_data.sql", "db/cocktails_data.sql")
                .build();
	}

```

Amb el DataSource (connexió a la BD), s’han creat dos elements més, per a accedir a l’execució de sentències a la base de dades:

* [JdbcTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html) que permet accés bàsic
* [NamedParameterJdbcTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/namedparam/NamedParameterJdbcTemplate.html) que permet accés amb noms parametritzats a les sentències SQL

Aquests dos elements, igual que el `DataSource` són utilitzats a tot arreu _per convenció_, de manera que qualsevol element que tingui accés a base de dades pot indicar la dependència (amb `@Autowired`).

A més, s’han creat tres scripts SQL:

* `src/main/resources/db/schema.sql`:

  ```SQL
  -- Usuaris
  CREATE TABLE USERS
  (
  	USERNAME VARCHAR(50) NOT NULL PRIMARY KEY
  	,PASSWORD VARCHAR(500) NOT NULL
  	,ENABLED BOOLEAN NOT NULL
  );
  CREATE TABLE AUTHORITIES
  (
  	USERNAME VARCHAR(50) NOT NULL
  	,AUTHORITY VARCHAR(50) NOT NULL
  	,CONSTRAINT FK_AUTHORITIES_USERS FOREIGN KEY(USERNAME) REFERENCES USERS(USERNAME)
  );
  CREATE UNIQUE INDEX IX_AUTH_USERNAME ON AUTHORITIES (USERNAME,AUTHORITY);
  -- Cocktails
  CREATE TABLE COCKTAILS
  (
  	ID_DRINK INTEGER NOT NULL PRIMARY KEY
  	,NAME NVARCHAR(200) NOT NULL
  	,THUMB VARCHAR(500) NOT NULL
  	,INGREDIENTS NVARCHAR(1000) NOT NULL
  	,INSTRUCTIONS NVARCHAR(1000) NOT NULL
  );
  CREATE INDEX IX_NAME ON COCKTAILS (NAME);
  ```

  L’esquema d’usuaris s’ha obtingut de l’arxiu `/org/springframework/security/core/userdetails/jdbc/users.ddl` del jar `spring-security-core-5.6.2.jar`

* `src/main/resources/db/user_data.sql` amb les dades d’usuaris:

  ```SQL
  -- Usuaris
  INSERT INTO USERS (USERNAME,PASSWORD,ENABLED) VALUES ('admin', '40a12d2b7f546c624461b26bd41573e697e91466dc80ef42acb46685a41b961648422e4529fcbef2fdaf79c7edfbc5e737bed9c224d93ecd26a7f5e028bfa3ed', TRUE);
  INSERT INTO USERS (USERNAME,PASSWORD,ENABLED) VALUES ('pepe', 'c63c4194ea7ab967c7a951a2f784d794318de97710e74bd6d3dcfd680058aecc941973c52e0f74e28aca2840db5a61fb64bfbf974037c34dfb94ebe1b4c860aa', TRUE);
  INSERT INTO USERS (USERNAME,PASSWORD,ENABLED) VALUES ('manolo', '3acd3650d01ddf7b2c5fd3488997982684da62c9318a54ee239d5a1f3db72e90b548f10489888ba0da2f0384fa161f319b7d707f2f89cdbe1cc1b6d9ed192fd8', TRUE);
  -- ROLS
  INSERT INTO AUTHORITIES (USERNAME,AUTHORITY) VALUES ('admin', 'ROLE_USER');
  INSERT INTO AUTHORITIES (USERNAME,AUTHORITY) VALUES ('pepe', 'ROLE_USER');
  INSERT INTO AUTHORITIES (USERNAME,AUTHORITY) VALUES ('manolo', 'ROLE_USER');
  
  ```

  La qual cosa és suficient per a l’autenticació i l’autorització

* `src/main/resources/db/cocktails_data.sql` que conté les sentències d’inserció dels cocktails (obtinguts del json):

  ```SQL
  -- Cocktails
  INSERT INTO COCKTAILS(ID_DRINK,NAME,THUMB,INGREDIENTS,INSTRUCTIONS) VALUES (12560,'Afterglow','https://www.thecocktaildb.com/images/media/drink/vuquyv1468876052.jpg','1 part Grenadine, 4 parts Orange juice, 4 parts Pineapple juice','Mix. Serve over ice.');
  INSERT INTO COCKTAILS(ID_DRINK,NAME,THUMB,INGREDIENTS,INSTRUCTIONS) VALUES (12562,'Alice Cocktail','https://www.thecocktaildb.com/images/media/drink/qyqtpv1468876144.jpg','1 cl Grenadine, 1 cl Orange juice, 2 cl Pineapple juice, 4 cl Cream','Shake well, strain into a large cocktail glass.');
  INSERT INTO COCKTAILS(ID_DRINK,NAME,THUMB,INGREDIENTS,INSTRUCTIONS) VALUES (12862,'Aloha Fruit punch','https://www.thecocktaildb.com/images/media/drink/wsyvrt1468876267.jpg','3/4 cup Water, 2 tsp Ginger, 2 cups Guava juice, 1 1/2 tblsp Lemon juice, 1 1/2 cup Pineapple, 1 cup Sugar, 3-4 cups Pineapple juice','Add 1/4 cup water to ginger root. Boil 3 minutes. Strain. Add the liquid to the guava, lemon and pineapple juices. Make a syrup of sugar and remaining water. Cool. Combine with juices and pineapple. Chill throroughly.');
  INSERT INTO COCKTAILS(ID_DRINK,NAME,THUMB,INGREDIENTS,INSTRUCTIONS) VALUES (15106,'Apello','https://www.thecocktaildb.com/images/media/drink/uptxtv1468876415.jpg','4 cl Orange juice, 3 cl Grapefruit juice, 1 cl Apple juice, 1 Maraschino cherry','Stirr. Grnish with maraschino cherry.');
  INSERT INTO COCKTAILS(ID_DRINK,NAME,THUMB,INGREDIENTS,INSTRUCTIONS) VALUES (12710,'Apple Berry Smoothie','https://www.thecocktaildb.com/images/media/drink/xwqvur1468876473.jpg','1 cup Berries, 2 Apple','Throw everything into a blender and liquify.');
  INSERT INTO COCKTAILS(ID_DRINK,NAME,THUMB,INGREDIENTS,INSTRUCTIONS) VALUES (12564,'Apple Karate','https://www.thecocktaildb.com/images/media/drink/syusvw1468876634.jpg','2 cups Apple juice, 1 large Carrot','Place all ingredients in the blender jar - cover and whiz on medium speed until well blended. Pour in one tall, 2 medium or 3 small glasses and drink up.');
  INSERT INTO COCKTAILS(ID_DRINK,NAME,THUMB,INGREDIENTS,INSTRUCTIONS) VALUES (12708,'Banana Cantaloupe Smoothie','https://www.thecocktaildb.com/images/media/drink/uqxqsy1468876703.jpg','Juice of 1/2 Cantaloupe, 1 Banana','Juice cantaloupe, pour juice into blender, add banana, and liquify.');
  INSERT INTO COCKTAILS(ID_DRINK,NAME,THUMB,INGREDIENTS,INSTRUCTIONS) VALUES (12654,'Banana Milk Shake','https://www.thecocktaildb.com/images/media/drink/rtwwsx1472720307.jpg','10 cl cold Milk, 4 cl Orange juice, 2 tsp Sugar syrup, 1/2 Banana','Blend very well, preferably in a household mixer. Serve in a wine glass, garnish with whipped cream and a piece of banana.');
  INSERT INTO COCKTAILS(ID_DRINK,NAME,THUMB,INGREDIENTS,INSTRUCTIONS) VALUES (12656,'Banana Strawberry Shake','https://www.thecocktaildb.com/images/media/drink/vqquwx1472720634.jpg','1/2 lb frozen Strawberries, 1 frozen Banana, 1 cup plain Yoghurt, 1 cup Milk, to taste\n Honey','Blend all together in a blender until smooth.');
  -- ... Tallat per a visualització més còmoda
  ```

  

#### Autenticació i autorització

El mòdul `spring-security-jdbc` proporciona una implementació d’`UserDetails` que opera amb base de dades.

Requereix dues taules:

* `USERS` per a enregistrar els usuaris amb les seves passwords codificades i un indicador de si estan actius o no
* `AUTHORITIES` per a establir els rols de cada usuari

La classe que se n’encarrega de llegir, crear, modificar, etc els usuaris és [JdbcUserDetailsManager](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/provisioning/JdbcUserDetailsManager.html). Al jar hi ha un arxiu DDL amb la definició SQL de les taules.

La inicialització és ben senzilla, com es pot veure al mètode [CockatilsConfiguration.users](xref/cat/albirar/daw/cocktails/CocktailsConfiguration.html#L55):

```java
	@Bean
	public UserDetailsService users(@Autowired DataSource dataSource) {
		return new JdbcUserDetailsManager(dataSource);
	}

```

El mòdul de seguretat que gestiona l’accés a les URLs, té dependència amb `UserDetailsService`, de manera que el canvi de memòria a SQL no l’afecta en absoluta i continua operant perfectament.

#### Servei de Cocktails

El servei de cocktails: [cat.albirar.daw.cocktails.service.impl.CocktailsServiceImpl](xref/cat/albirar/daw/cocktails/service/impl/CocktailsServiceImpl.html#L41) a estat transformat per tal de que utilitzi la base de dades i no pas la memòria:

```java
@Service
public class CocktailsServiceImpl implements ICocktailsService {
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private RowMapper<CocktailDetailBean> mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CocktailDetailBean> findAll() {
		return namedParameterJdbcTemplate.query(ConstantsCocktailsDb.SQL_FIND_ALL, mapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<CocktailDetailBean> findById(String id) {
		List<CocktailDetailBean> l;
		
		l = namedParameterJdbcTemplate.query(ConstantsCocktailsDb.SQL_FIND_BY_ID
				, new MapSqlParameterSource(ConstantsCocktailsDb.ID, Integer.valueOf(id))
				, mapper);
		if(!l.isEmpty()) {
			return Optional.of(l.get(0));
		}
		return Optional.empty();
	}
}
```

S’ha suprimit qualsevol referència als elements de memòria (arxiu json, llista i mapa), així com el mètode d’inicialització que carregava les dades de l’arxiu json a memòria.

Ara s’han afegit dues dependències d’injecció:

* `private NamedParameterJdbcTemplate namedParameterJdbcTemplate` per a executar sentències a la base de dades
* `private RowMapper<CocktailDetailBean> mapper` per a delegar la materialització de files de la taula `COCKTAILS` en instàncies de la classe `CocktailDetailBean`

Els dos mètodes del servei s’han canviat per tal d’obtenir les dades de la taula.

La classe [cat.albirar.daw.cocktails.service.mappers.CocktailDetailBeanMapper](xref/cat/albirar/daw/cocktails/service/mappers/CocktailDetailBeanMapper.html#CocktailDetailBeanMapper) és la responsable de convertir les dades dels registres SQL en classes `CocktailDetailBean`. Aquest sistema forma part del patró JDBC implementat pel framework. La transformació es fa al mètode `mapRow`:

```java
	public CocktailDetailBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		return CocktailDetailBean.builder()
				.id(Integer.toString(rs.getInt(ConstantsCocktailsDb.ID)))
				.name(rs.getString(ConstantsCocktailsDb.NAME))
				.urlThumb(rs.getString(ConstantsCocktailsDb.THUMB))
				.ingredients(rs.getString(ConstantsCocktailsDb.INGREDIENTS))
				.instruccions(rs.getString(ConstantsCocktailsDb.INSTRUCTIONS))
				.build()
				;
	}

```

Aquest mètode és cridat pel sistema JDBC del framework en obtenir `ResultSet` d’una sentència _SELECT_. El retorn de cada crida s’acumula a una llista o bé es retorna directament.



