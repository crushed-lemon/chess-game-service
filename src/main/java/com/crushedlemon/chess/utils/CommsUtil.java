package com.crushedlemon.chess.utils;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionResponse;

import java.net.URI;

public class CommsUtil {

    private static final String CONNECTIONS_URI = "https://wec2i3hiw3.execute-api.eu-north-1.amazonaws.com/production";

    public static final void communicateToClient(String clientId, String message) {
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
            System.out.println("Message sent! Status Code: " + response.sdkHttpResponse().statusCode());
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
