package com.crushedlemon.chess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
public class ChessGameController {

    private static final Logger logger = LoggerFactory.getLogger(ChessGameController.class);

    @GetMapping("/")
    public String hello() {
        return "Hello";
    }

    @PostMapping("/")
    public ResponseEntity<String> handleWebSocketMessage(@RequestBody Map<String, Object> payload) {

        logger.atInfo().log("The API endpoint was hit");

        // Extract the actual WebSocket message
        String bodyJson = (String) payload.get("body");
        logger.atInfo().log(String.format("Body was %s", bodyJson));

        // Parse the body string into a Map or POJO
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> innerBody;
        try {
            innerBody = mapper.readValue(bodyJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid JSON body");
        }

        String action = (String) innerBody.get("action");
        // Handle your logic here based on the action

        return ResponseEntity.ok("Action received: " + action);
    }
}
