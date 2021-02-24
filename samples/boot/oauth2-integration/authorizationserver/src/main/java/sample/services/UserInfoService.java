package sample.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import sample.config.RegisteredUsers;
import sample.models.CustomOidcUser;

import java.util.*;

@Service
public class UserInfoService {
	private final RegisteredUsers registeredUsers;
	private final RSAKey rsaKey;

	public UserInfoService(RegisteredUsers registeredUsers, RSAKey rsaKey) {
		this.registeredUsers = registeredUsers;
		this.rsaKey = rsaKey;
	}

	public CustomOidcUser getUserInfo(String jwtToken) {
		NimbusJwtDecoder jwtDecoder;
		try {
			jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
		} catch (JOSEException e) {
			throw new JwtException("JWT not valid");
		}

		final Jwt decode = jwtDecoder.decode(jwtToken.substring(7));
		String sub = (String) decode.getClaims().get("sub");
		RegisteredUsers.User user = getUser(sub);
		if (user == null) {
			throw new UsernameNotFoundException("No user found for: " + sub);
		}

		Map<String, Object> claims = new HashMap<>(decode.getClaims());
		claims.put("givenName", user.getGivenName());
		claims.put("sn", user.getFamilyName());
		claims.put("uid", sub);
		OidcUserInfo oidcUserInfo = OidcUserInfo
				.builder()
				.subject(sub)
				.familyName(user.getFamilyName())
				.givenName(user.getGivenName()).build();

		OidcIdToken oidcIdToken = new OidcIdToken(decode.getTokenValue(), null, null, claims);
		OidcUserAuthority oidcUserAuthority = new OidcUserAuthority(oidcIdToken);

		CustomOidcUser oidcUser = new CustomOidcUser(Collections.singleton(oidcUserAuthority), oidcIdToken, oidcUserInfo);
		oidcUser.setSub(sub);
		oidcUser.setUid(sub);

		return oidcUser;
	}

	private RegisteredUsers.User getUser(String uid) {
		List<RegisteredUsers.User> users = registeredUsers.getUsers();
		for (RegisteredUsers.User user : users) {
			if (user.getUid().equals(uid)) {
				return user;
			}
		}

		return null;
	}
}
