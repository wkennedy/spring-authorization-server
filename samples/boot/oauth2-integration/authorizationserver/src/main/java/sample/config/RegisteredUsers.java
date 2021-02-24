package sample.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@ConfigurationProperties("registered-users")
public class RegisteredUsers {
	private List<User> users = new ArrayList<>();
	private Map<String, List<User>> heroesMapping;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Map<String, List<User>> getHeroesMapping() {
		return heroesMapping;
	}

	public void setHeroesMapping(Map<String, List<User>> heroesMapping) {
		this.heroesMapping = heroesMapping;
	}

	public static class User {
		private String givenName;
		private String familyName;
		private String uid;
		private String password;

		public User() {
		}

		public User(String givenName, String familyName, String uid, String password) {
			this.givenName = givenName;
			this.familyName = familyName;
			this.uid = uid;
			this.password = password;
		}

		public String getGivenName() {
			return givenName;
		}

		public void setGivenName(String givenName) {
			this.givenName = givenName;
		}

		public String getFamilyName() {
			return familyName;
		}

		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			User user = (User) o;
			return Objects.equals(givenName, user.givenName) && Objects.equals(familyName, user.familyName) && Objects.equals(uid, user.uid) && Objects.equals(password, user.password);
		}

		@Override
		public int hashCode() {
			return Objects.hash(givenName, familyName, uid, password);
		}

	}
}
