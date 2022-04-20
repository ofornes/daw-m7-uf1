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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import cat.albirar.daw.cocktails.controllers.MainController;
import cat.albirar.daw.cocktails.service.impl.CocktailsServiceImpl;

/**
 * Configuració de l'aplicaició.
 * 
 * @author Octavi Forn&eacute;s <mailto:ofornes@albirar.cat[]>
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackageClasses = { MainController.class, CocktailsServiceImpl.class })
@EnableWebSecurity
public class CocktailsConfiguration {
	@Bean
	public UserDetailsService users() {
		// The builder will ensure the passwords are encoded before saving in memory
		UserBuilder userBuilder = User.builder();
		UserDetails admin = userBuilder
			.username("admin").password(passwordEncoder().encode("admin"))
			.roles("USER")
			.build();
		UserDetails pepe = userBuilder
				.username("pepe").password(passwordEncoder().encode("1234"))
				.roles("USER")
				.build();
		UserDetails manolo = userBuilder
				.username("manolo").password(passwordEncoder().encode("asdf"))
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(admin, pepe, manolo);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
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
	/*
	 * $phrase = 'SuperSecret.';
	 * 
	 * $users = array( 'admin' =>
	 * '40a12d2b7f546c624461b26bd41573e697e91466dc80ef42acb46685a41b961648422e4529fcbef2fdaf79c7edfbc5e737bed9c224d93ecd26a7f5e028bfa3ed
	 * ', //admin - admin 'pepe' =>
	 * 'c63c4194ea7ab967c7a951a2f784d794318de97710e74bd6d3dcfd680058aecc941973c52e0f74e28aca2840db5a61fb64bfbf974037c34dfb94ebe1b4c860aa
	 * ', // pepe - 1234 'manolo' =>
	 * '3acd3650d01ddf7b2c5fd3488997982684da62c9318a54ee239d5a1f3db72e90b548f10489888ba0da2f0384fa161f319b7d707f2f89cdbe1cc1b6d9ed192fd8
	 * ', //manolo - asdf );
	 */
}
