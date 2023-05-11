package com.example.cakes;

import com.example.cakes.repository.CakeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

@SpringBootApplication
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CakesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CakesApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(CakeRepository cakeRepository) {
		return args -> {
			try (InputStream cakesJsonStream = new ClassPathResource("static/initial-cakes.json").getInputStream()) {
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonFromFile = objectMapper.readTree(cakesJsonStream);
				for (final JsonNode cakeJson : jsonFromFile) {
					CakeEntity cakeEntity = objectMapper.treeToValue(cakeJson, CakeEntity.class);
					cakeRepository.save(cakeEntity);
				}
			}
		};
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf()
				.disable()
				.authorizeRequests()
				.anyRequest()
				.authenticated()
				.and()
				.x509()
				.subjectPrincipalRegex("CN=(.*?)(?:,|$)")
				.userDetailsService(userDetailsService());
		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				JsonNode jsonFromFile = null;
				try (InputStream userDetailsJsonStream = new ClassPathResource("static/initial-user-detail.json").getInputStream()) {
					ObjectMapper objectMapper = new ObjectMapper();
					jsonFromFile = objectMapper.readTree(userDetailsJsonStream);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				User user = null;
				for (final JsonNode userJson : jsonFromFile) {
                    if (username.equals(userJson.get("name").asText())) {
						StringBuilder rolesBuilder = new StringBuilder();
						for (final JsonNode roleJson : userJson.get("roles")) {
                            if (rolesBuilder.isEmpty()) {
								rolesBuilder.append(roleJson.asText());
							} else {
								rolesBuilder.append(',');
								rolesBuilder.append(roleJson.asText());
							}
						}
						user = new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList(rolesBuilder.toString()));
						break;
					}
				}
				if (null == user) {
					throw new UsernameNotFoundException("User not found!");
				}
				return user;
			}
		};
	}
}
