package me.krzysztofprogramming.resourcesservice.integration;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KeycloakIntegrationTest {

    private final static KeycloakContainer keycloakContainer;

    static {
        keycloakContainer = new KeycloakContainer().withRealmImportFile("./keycloak/realm-export.json");
        keycloakContainer.start();
    }

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    public static void registerKeycloakServer(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("keycloak.auth-server-url", keycloakContainer::getAuthServerUrl);
    }

    @Test
    public void shouldAccessAdminResources() throws URISyntaxException {
        //given
        webTestClient.get().uri("/admin")
                .header("Authorization", getAuthToken("admin", "admin"))
                //when
                .exchange()
                //then
                .expectStatus().isOk();
    }

    @Test
    public void shouldAccessProtectedResources() throws URISyntaxException {
        //given
        webTestClient.get().uri("/protected")
                .header("Authorization", getAuthToken("user", "user"))
                //when
                .exchange()
                //then
                .expectStatus().isOk();
    }

    @Test
    public void shouldThrow403WhenNoRole() throws URISyntaxException {
        //given
        webTestClient.get().uri("/admin")
                .header("Authorization", getAuthToken("user", "user"))
                //when
                .exchange()
                //then
                .expectStatus().isForbidden();
    }

    @Test
    public void shouldThrow401WhenAccessingAdminResources() throws URISyntaxException {
        //given
        webTestClient.get().uri("/admin")
                //when
                .exchange()
                //then
                .expectStatus().isUnauthorized();
    }

    @Test
    public void shouldThrow401WhenAccessingProtectedResources() throws URISyntaxException {
        //given
        webTestClient.get().uri("/protected")
                //when
                .exchange()
                //then
                .expectStatus().isUnauthorized();
    }

    @Test
    public void shouldAccessPublicResources() throws URISyntaxException {
        //given
        webTestClient.get().uri("/public")
                //when
                .exchange()
                //then
                .expectStatus().isOk();
    }

    private String getAuthToken(String username, String password) throws URISyntaxException {
        URI authorizationURI = new URIBuilder(keycloakContainer.getAuthServerUrl()
                + "/realms/Testing/protocol/openid-connect/token").build();
        WebClient webclient = WebClient.builder().build();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.put("grant_type", Collections.singletonList("password"));
        formData.put("client_id", Collections.singletonList("Spring-resources-server"));
        formData.put("scope", Collections.singletonList("openid"));
        formData.put("client_secret", Collections.singletonList("MacHPgDhFl7KwJoqpl8cmIHMgkDGlRIy"));
        formData.put("username", Collections.singletonList(username));
        formData.put("password", Collections.singletonList(password));

        String result = webclient.post()
                .uri(authorizationURI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return "Bearer " + jsonParser.parseMap(result)
                .get("access_token")
                .toString();
    }
}
