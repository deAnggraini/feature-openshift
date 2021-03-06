package id.co.bca.pakar.be.oauth2.api;

import java.security.Principal;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import id.co.bca.pakar.be.oauth2.dto.LoggedinDto;
import id.co.bca.pakar.be.oauth2.service.UserService;

@RestController
public class UserController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	
	/**
	 * decode token object to authorization object
	 * @param principal
	 * @return
	 */
	@GetMapping("/api/auth/me")
    public ResponseEntity<RestResponse<Principal>> get(final Principal principal) {
        return createResponse(principal, "00",  "SUCCESS");
    }
	
	/**
	 * 
	 * @param get user profile by token
	 * @return
	 */
	@PostMapping(value = "/api/auth/getUser", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponse<LoggedinDto>> getUser(@RequestHeader (name="Authorization") String authorization, @RequestHeader (name="X-USERNAME") String username) {
		try {
			logger.info("received token bearer --- " + authorization);
			String tokenValue = "";
			if (authorization != null && authorization.contains("Bearer")) {
	            tokenValue = authorization.replace("Bearer", "").trim();

	            logger.info("token value request header --- "+tokenValue);
	            logger.info("username request header --- "+username);
	        }
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			logger.info("get user by token ");
			LoggedinDto loggedInDto = userService.findUserByToken(tokenValue, username);
			return createResponse(loggedInDto, "00",  "SUCCESS");
		} catch (Exception e) {
			logger.error("exception", e);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			RestResponse<LoggedinDto> tResponse = new RestResponse(new LoggedinDto(), "01", "FAILED LOGIN");
			return new ResponseEntity<RestResponse<LoggedinDto>>(tResponse, HttpStatus.OK);
		}
	}
}
