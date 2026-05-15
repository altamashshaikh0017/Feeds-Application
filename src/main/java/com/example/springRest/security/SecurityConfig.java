package com.example.springRest.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/register", "/signUp", "/login", "/forgot-password", "/reset-password").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/home", true)
            .failureUrl("/login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/login?logout=true")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        );
    return http.build();
}
	
	@Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService,
                                                             BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	
//		@Bean
//		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//			http
//			.csrf((csrf)->csrf.disable())
//			.authorizeHttpRequests((requests)->requests
//					.requestMatchers("/register" , "/login"  , "/createPost")
//					.permitAll()
//					.requestMatchers( "/viewPosts").hasRole("USER")   //hasAuthority("USER")
//					.requestMatchers("/admin/**").hasRole("ADMIN")
//					.anyRequest().authenticated()
//					)
//			.formLogin((form)->form
//					.loginPage("/login")
//					.defaultSuccessUrl("/createPost")
//					.failureUrl("/error")
//					.permitAll()
//					)
//			.logout((logout)->logout
//					.permitAll()
//					)
//			.sessionManagement((session)->session
//					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//					);
//			return http.build();
//		}
//
//	@Bean
//	public UserDetailsService userDetailsService() {
//		UserDetails user = 
//				User.withUsername("user")
//				.password(passwordEncoder().encode("userpass"))
//				.roles("USER")
//				.build();
//
//		UserDetails admin =
//				User.withUsername("admin")
//				.password(passwordEncoder().encode("adminpass"))
//				.roles("ADMIN")
//				.build();
//
//		return new InMemoryUserDetailsManager(user,admin);
//	}
//
//	@Bean
//	PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
}
