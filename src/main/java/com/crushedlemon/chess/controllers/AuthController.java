package com.crushedlemon.chess.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
public class AuthController {

    @Value("${cognito.redirectUri}")
    private String redirectUri;

    @Value("${cognito.clientId}")
    private String clientId;

    @Value("${cognito.endpoint}")
    private String endpoint;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/whoami")
    public ResponseEntity<String> whoami(@CookieValue(name = "id_token", required = false) String token) {
        log.atInfo().log("token = {}", token);
        return ResponseEntity.ok("{\"logged_out\": true}");
        // return ResponseEntity.ok("{\"email\": \"lemony@gmail.com\"}");
    }

    @PostMapping("/login-callback")
    public ResponseEntity<String> loginCallback(@RequestBody Map<String, Object> requestBody) {

        String code = (String) requestBody.get("code");

        log.atInfo().log(code);

        if (code.isBlank()) {
            return ResponseEntity.ok("no code given");
        }

        HttpEntity<MultiValueMap<String, String>> tokenRequest = prepareTokenRequest(code);

        try {
            // Send POST request
            Map<String, String> tokenResponseBody = hitTokenEndpoint(tokenRequest);
            String id_token = tokenResponseBody.get("id_token");

            // Set the id_token in a cookie
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Set-Cookie", String.format("id_token=%s; HttpOnly; SameSite=Strict", id_token));
            return new ResponseEntity<>("Login Successful", responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.atError().log("", e);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Credentials not valid");
        }
    }

    private HttpEntity<MultiValueMap<String, String>> prepareTokenRequest(String code) {
        MultiValueMap<String, String> tokenRequestBody = prepareTokenRequestBody(code);

        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(tokenRequestBody, tokenHeaders);
    }

    private MultiValueMap<String, String> prepareTokenRequestBody(String code) {
        MultiValueMap<String, String> tokenRequestBody = new LinkedMultiValueMap<>();
        tokenRequestBody.add("grant_type", "authorization_code");
        tokenRequestBody.add("code", code);
        tokenRequestBody.add("redirect_uri", redirectUri);
        tokenRequestBody.add("client_id", clientId);
        return tokenRequestBody;
    }

    private Map<String, String> hitTokenEndpoint(HttpEntity<MultiValueMap<String, String>> tokenRequest)
            throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
         ResponseEntity<String> tokenResponse = restTemplate.postForEntity(endpoint, tokenRequest, String.class);
         Map<String, String> tokenResponseBody =
                objectMapper.readValue(tokenResponse.getBody(), new TypeReference<Map<String, String>>() {});
         return tokenResponseBody;
        //return Map.of("id_token", "xxx");
    }
}
