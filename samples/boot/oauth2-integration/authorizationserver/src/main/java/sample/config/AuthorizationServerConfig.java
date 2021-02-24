/*
 * Copyright 2020-2021 the original author or authors.
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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer;
import sample.jose.Jwks;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;

/**
 * @author Joe Grandja
 * @since 0.0.1
 */
@Configuration(proxyBeanMethods = false)
@Import(OAuth2AuthorizationServerConfiguration.class)
public class AuthorizationServerConfig {

	private final ProviderSettingsProperties providerSettingsProperties;
	private final RegisteredClientProperties registeredClientProperties;

	public AuthorizationServerConfig(ProviderSettingsProperties providerSettingsProperties, RegisteredClientProperties registeredClientProperties) {
		this.providerSettingsProperties = providerSettingsProperties;
		this.registeredClientProperties = registeredClientProperties;
	}

	// @formatter:off
	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient.Builder registeredClientBuilder = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId(registeredClientProperties.getClientId())
				.clientSecret(registeredClientProperties.getClientSecret())
				.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope(OidcScopes.OPENID)
				.clientSettings(clientSettings -> clientSettings.requireUserConsent(false));

		List<String> redirectUris = registeredClientProperties.getRedirectUris();
		for (String redirectUri : redirectUris) {
			registeredClientBuilder.redirectUri(redirectUri);
		}

		List<String> scopes = registeredClientProperties.getScopes();
		for (String scope : scopes) {
			registeredClientBuilder.scope(scope);
		}

		RegisteredClient registeredClient = registeredClientBuilder.build();
		return new InMemoryRegisteredClientRepository(registeredClient);
	}
	// @formatter:on

	@Bean
	public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
		JWKSet jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
	}

	@Bean
	public RSAKey rsaKey() {
		return Jwks.generateRsa();
	}

	@Bean
	public ProviderSettings providerSettings() {
		ProviderSettings providerSettings = new ProviderSettings();
		return providerSettings.issuer(providerSettingsProperties.getIssuer());
	}

//	@Bean
//	OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
//		return context -> {
//			context.getClaims().claim("givenName", "test");
//		};
//	}
}
