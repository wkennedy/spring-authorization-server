/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author Joe Grandja
 * @since 0.1.0
 */
@EnableWebSecurity
public class DefaultSecurityConfig {

	private final RegisteredUsers registeredUsers;

	public DefaultSecurityConfig(RegisteredUsers registeredUsers) {
		this.registeredUsers = registeredUsers;
	}

	// formatter:off
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeRequests(authorizeRequests ->
						authorizeRequests.antMatchers("/oauth2/userinfo").permitAll().anyRequest().authenticated()
				)
				.formLogin(withDefaults());
		return http.build();
	}
	// formatter:on

	// @formatter:off
	@Bean
	UserDetailsService users() {
//		Map<String, Object> claims = new HashMap<>();
//			claims.put("givenName", "test");
//		claims.put("sn", "test");
//		claims.put("uid", "test");
//		OidcUserAuthority oidcUserAuthority = new OidcUserAuthority(new OidcIdToken(UUID.randomUUID().toString(), null, null, claims));
		Set<UserDetails> userDetailsSet = new HashSet<>();
		List<RegisteredUsers.User> users = registeredUsers.getUsers();
		for (RegisteredUsers.User user : users) {
			UserDetails userDetails = User.withDefaultPasswordEncoder()
					.username(user.getUid())
					.password(user.getPassword())
					.roles("USER") //TODO configurable
					.build();
			userDetailsSet.add(userDetails);
		}

		return new InMemoryUserDetailsManager(userDetailsSet);
	}
	// @formatter:on

}
