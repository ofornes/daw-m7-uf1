/*
 * This file is part of "cocktails".
 * 
 * "cocktails" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "cocktails" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with calendar.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2022 Octavi Fornés
 */
package cat.albirar.daw.cocktails;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import cat.albirar.daw.cocktails.controllers.MainController;
import cat.albirar.daw.cocktails.crypt.CocktailsPasswordEncoder;
import cat.albirar.daw.cocktails.service.impl.CocktailsServiceImpl;

/**
 * Configuració de l'aplicació.
 * 
 * @author Octavi Forn&eacute;s <mailto:ofornes@albirar.cat[]>
 * @since 0.0.1
 * @since 0.1.4
 */
@Configuration
@ComponentScan(basePackageClasses = { MainController.class, CocktailsServiceImpl.class })
@EnableWebSecurity
public class CocktailsConfiguration {
	@Bean
	public UserDetailsService users(@Autowired DataSource dataSource) {
		return new JdbcUserDetailsManager(dataSource);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new CocktailsPasswordEncoder();
	}
	
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
	
	@Bean
	public JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	@Bean
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Autowired JdbcTemplate jdbcTemplate) {
		return new NamedParameterJdbcTemplate(jdbcTemplate);
	}
}
