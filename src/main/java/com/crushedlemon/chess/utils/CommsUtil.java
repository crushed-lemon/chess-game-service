package com.crushedlemon.chess.utils;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionResponse;

import java.net.URI;

@Slf4j
public class CommsUtil {

    private static final String CONNECTIONS_URI = "https://wec2i3hiw3.execute-api.eu-north-1.amazonaws.com/production";

    public static void communicateToClient(String clientId, String message) {
        // Send the message to client
        ApiGatewayManagementApiClient client = ApiGatewayManagementApiClient.builder()
                .endpointOverride(URI.create(CONNECTIONS_URI))
                .build();

        PostToConnectionRequest postRequest = PostToConnectionRequest.builder()
                .connectionId(clientId)
                .data(SdkBytes.fromUtf8String(message))
                .build();

        try {
            PostToConnectionResponse response = client.postToConnection(postRequest);
            log.info("Message sent! Status Code: {}", response.sdkHttpResponse().statusCode());
        } catch (Exception e) {
            log.error("Error sending message", e);
        }
    }
}
