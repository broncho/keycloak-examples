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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 建议一个应用程序持有一个KeycloakTokenContext实例，进行Token的获取以及刷新
 * 注意：该示例暂未考虑线程安全性问题
 * <p>
 * Author: secondriver
 * Created: 2020/3/20
 */
public class KeycloakTokenContext {
    
    private final Logger logger = LoggerFactory.getLogger(KeycloakTokenContext.class);
    
    private AuthzClient authzClient;
    
    private Http http;
    
    private AccessTokenResponse clientToken;
    
    private KeycloakTokenContext(Configuration configuration) {
        try {
            
            authzClient = AuthzClient.create(configuration);
            Field httpField = authzClient.getClass().getDeclaredField("http");
            httpField.setAccessible(true);
            http = (Http) httpField.get(authzClient);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Default read classpath keycloak.json installation configuration information
     *
     * @return KeycloakTokenContext
     */
    public static KeycloakTokenContext create() {
        InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("keycloak.json");
        return create(configStream);
    }
    
    /**
     * Create KeycloakTokenContext by configuration stream
     *
     * @param configStream keycloak installation configuration file path
     * @return KeycloakTokenContext
     * @throws RuntimeException
     */
    public static KeycloakTokenContext create(InputStream configStream) throws RuntimeException {
        if (Objects.isNull(configStream)) {
            throw new IllegalArgumentException("Config input stream can not be null");
        }
        try {
            return create(JsonSerialization.readValue(configStream, Configuration.class));
        } catch (IOException e) {
            throw new RuntimeException("Could not parse configuration.", e);
        }
    }
    
    /**
     * Create KeycloakTokenContext by configuration instance
     *
     * @param configuration keycloak configuration instance
     * @return KeycloakTokenContext
     */
    public static KeycloakTokenContext create(Configuration configuration) {
        return new KeycloakTokenContext(configuration);
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
