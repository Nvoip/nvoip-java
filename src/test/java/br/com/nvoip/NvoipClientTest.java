package br.com.nvoip;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NvoipClientTest {
    private HttpServer server;
    private String baseUrl;
    private final AtomicReference<String> tokenAuth = new AtomicReference<>();
    private final AtomicReference<String> tokenForm = new AtomicReference<>();
    private final AtomicReference<String> bearer = new AtomicReference<>();

    @BeforeEach void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/auth/oauth2/token", this::token);
        server.createContext("/v3/balance", this::balance);
        server.createContext("/v3/check/otp", this::otp);
        server.start();
        baseUrl = "http://localhost:" + server.getAddress().getPort() + "/v3";
    }
    @AfterEach void stop() { server.stop(0); }

    @Test void sendsOauthFormBasicAndBearerResources() throws Exception {
        NvoipClient client = new NvoipClient(baseUrl, "id +", "secret:/", baseUrl.replace("/v3", "/auth/oauth2/token"));
        assertEquals("{\"access_token\":\"token\"}", client.createAccessToken());
        assertEquals("{\"balance\":1}", client.getBalance("token"));
        assertEquals("{\"ok\":true}", client.checkOtp("token", "a b", "key/1"));
        assertTrue(tokenForm.get().startsWith("application/x-www-form-urlencoded"));
        assertEquals("grant_type=client_credentials", tokenAuth.get());
        assertEquals("Bearer token", bearer.get());
    }

    @Test void raisesIOExceptionForUnauthorizedResponse() throws Exception {
        NvoipClient client = new NvoipClient(baseUrl, "id", "secret", baseUrl.replace("/v3", "/auth/oauth2/token"));
        assertThrows(IOException.class, () -> client.getBalance("bad"));
    }

    private void token(HttpExchange e) throws IOException {
        tokenForm.set(e.getRequestHeaders().getFirst("Content-Type"));
        tokenAuth.set(new String(e.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        String basic = e.getRequestHeaders().getFirst("Authorization");
        assertNotNull(basic);
        assertEquals("id+%2B:secret%3A%2F", new String(Base64.getDecoder().decode(basic.substring(6)), StandardCharsets.UTF_8));
        reply(e, 200, "{\"access_token\":\"token\"}");
    }
    private void balance(HttpExchange e) throws IOException {
        bearer.set(e.getRequestHeaders().getFirst("Authorization"));
        reply(e, "Bearer bad".equals(bearer.get()) ? 401 : 200, "{\"balance\":1}");
    }
    private void otp(HttpExchange e) throws IOException {
        bearer.set(e.getRequestHeaders().getFirst("Authorization"));
        assertTrue(e.getRequestURI().getRawQuery().contains("code=a+b"));
        assertTrue(e.getRequestURI().getRawQuery().contains("key=key%2F1"));
        reply(e, 200, "{\"ok\":true}");
    }
    private static void reply(HttpExchange e, int status, String body) throws IOException {
        e.sendResponseHeaders(status, body.getBytes(StandardCharsets.UTF_8).length);
        e.getResponseBody().write(body.getBytes(StandardCharsets.UTF_8));
        e.close();
    }
}
