package sample.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import sample.models.CustomOidcUser;
import sample.services.UserInfoService;

@RestController
public class UserInfoController {

	private final UserInfoService userInfoService;

	public UserInfoController(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	@GetMapping("/oauth2/userinfo")
	public CustomOidcUser getUser(@RequestHeader(name = "Authorization") String token) {
		return userInfoService.getUserInfo(token);
	}
}
