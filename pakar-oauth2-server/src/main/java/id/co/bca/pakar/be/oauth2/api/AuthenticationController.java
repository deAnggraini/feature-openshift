package id.co.bca.pakar.be.oauth2.api;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import id.co.bca.pakar.be.oauth2.dto.CredentialDto;
import id.co.bca.pakar.be.oauth2.dto.OAuthCredential;
import id.co.bca.pakar.be.oauth2.dto.OAuthTokenDto;
import id.co.bca.pakar.be.oauth2.util.JSONMapperAdapter;

@RestController
public class AuthenticationController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestTemplate restTemplate;

	@Value("${spring.security.oauth2.clientId}")
	private String clientId;
	@Value("${spring.security.oauth2.clientSecret}")
	private String clientSecret;
	@Value("${spring.security.oauth2.server.url}")
	private String uri;

	@PostMapping(value = "/api/auth/login", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<OAuthTokenDto> loginTest(@RequestBody CredentialDto dto) {
		OAuthCredential cred = new OAuthCredential();
		cred.setUsername(dto.getUsername());
		cred.setPassword(dto.getPassword());
		cred.setGrant_type("password");

		logger.info("received credential --------- " + cred.toString());
		String credentials = clientId + ":" + clientSecret;
		String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Basic " + encodedCredentials);

		HttpEntity<String> request = new HttpEntity<String>(headers);

		String access_token_url = uri;
		access_token_url += "?username=" + cred.getUsername();
		access_token_url += "&password=" + cred.getPassword();
		access_token_url += "&grant_type=" + cred.getGrant_type();

		ResponseEntity<String> response = null;
		response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, String.class);

		logger.info("generated oauth2 token --------- " + response.getBody());

		OAuthTokenDto oAuthToken = (OAuthTokenDto) JSONMapperAdapter.jsonToObject(response.getBody(),  OAuthTokenDto.class);

		logger.info("Token Response Data callback --------- " + JSONMapperAdapter.objectToJson(oAuthToken));
		
		return ResponseEntity.accepted().headers(headers).body(oAuthToken);
	}
}