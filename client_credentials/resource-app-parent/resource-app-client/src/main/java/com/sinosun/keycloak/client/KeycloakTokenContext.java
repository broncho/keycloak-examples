package com.sinosun.keycloak.client;

import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.util.Http;
import org.keycloak.common.util.Time;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.RefreshToken;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Author: secondriver
 * Created: 2020/3/20
 */
public class KeycloakTokenContext {
    
    private final Logger logger = LoggerFactory.getLogger(KeycloakTokenContext.class);
    
    private AuthzClient authzClient;
    
    private Http http;
    
    private AccessTokenResponse clientToken;
    
    public KeycloakTokenContext() {
        this(null);
    }
    
    public KeycloakTokenContext(Configuration configuration) {
        try {
            if (Objects.isNull(configuration)) {
                authzClient = AuthzClient.create();
            } else {
                authzClient = AuthzClient.create(configuration);
            }
            Field httpField = authzClient.getClass().getDeclaredField("http");
            httpField.setAccessible(true);
            http = (Http) httpField.get(authzClient);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    
    
    private boolean isTokenTimeToLiveSufficient(AccessToken token) {
        return token != null && (token.getExpiration() - authzClient.getConfiguration().getTokenMinimumTimeToLive()) > Time.currentTime();
    }
    
    public String getToken() {
        if (clientToken == null) {
            clientToken = authzClient.obtainAccessToken();
            logger.info("Client token is null , so obtain token: {}", clientToken.getToken());
        } else {
            try {
                String token = clientToken.getToken();
                AccessToken accessToken = JsonSerialization.readValue(new JWSInput(token).getContent(), AccessToken.class);
                if (accessToken.isActive() && this.isTokenTimeToLiveSufficient(accessToken)) {
                    return token;
                } else {
                    logger.debug("Client Access token is expired.");
                }
                String refreshTokenValue = clientToken.getRefreshToken();
                RefreshToken refreshToken = JsonSerialization.readValue(new JWSInput(refreshTokenValue).getContent(), RefreshToken.class);
                if (!refreshToken.isActive() || !isTokenTimeToLiveSufficient(refreshToken)) {
                    logger.debug("Client Refresh token is expired.");
                    clientToken = authzClient.obtainAccessToken();
                } else {
                    clientToken = http.<AccessTokenResponse>post(authzClient.getServerConfiguration().getTokenEndpoint())
                            .authentication().client()
                            .form()
                            .param("grant_type", "refresh_token")
                            .param("refresh_token", clientToken.getRefreshToken())
                            .response()
                            .json(AccessTokenResponse.class)
                            .execute();
                }
            } catch (Exception e) {
                clientToken = null;
                throw new RuntimeException(e);
            }
        }
        return clientToken.getToken();
    }
}
