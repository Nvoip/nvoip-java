package br.com.nvoip;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

public final class NvoipClient {
    private final String baseUrl;
    private final String oauthClientId;
    private final String oauthClientSecret;
    private final HttpClient httpClient;

    public NvoipClient(String baseUrl, String oauthClientId, String oauthClientSecret) {
        this.baseUrl = trimTrailingSlash(baseUrl == null || baseUrl.isBlank() ? "https://api.nvoip.com.br/v2" : baseUrl);
        this.oauthClientId = oauthClientId;
        this.oauthClientSecret = oauthClientSecret;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }

    public static String encodeBasicAuth(String clientId, String clientSecret) {
        return Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String numbersip, String userToken) throws IOException, InterruptedException {
        String formBody = "username=" + encode(numbersip)
            + "&password=" + encode(userToken)
            + "&grant_type=password";

        return request(
            HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/oauth/token"))
                .header("Authorization", "Basic " + resolveBasicAuth())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
        );
    }

    public String refreshAccessToken(String refreshToken) throws IOException, InterruptedException {
        String formBody = "grant_type=refresh_token&refresh_token=" + encode(refreshToken);

        return request(
            HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/oauth/token"))
                .header("Authorization", "Basic " + resolveBasicAuth())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
        );
    }

    public String getBalance(String accessToken) throws IOException, InterruptedException {
        return request(
            HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/balance"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
        );
    }

    public String sendSms(String accessToken, String numberPhone, String message) throws IOException, InterruptedException {
        String payload = "{"
            + "\"numberPhone\":\"" + escapeJson(numberPhone) + "\","
            + "\"message\":\"" + escapeJson(message) + "\","
            + "\"flashSms\":false"
            + "}";

        return jsonRequest("/sms", accessToken, payload);
    }

    public String createCall(String accessToken, String caller, String called) throws IOException, InterruptedException {
        String payload = "{"
            + "\"caller\":\"" + escapeJson(caller) + "\","
            + "\"called\":\"" + escapeJson(called) + "\""
            + "}";

        return jsonRequest("/calls/", accessToken, payload);
    }

    public String sendOtp(String accessToken, String payloadJson) throws IOException, InterruptedException {
        return jsonRequest("/otp", accessToken, payloadJson);
    }

    public String checkOtp(String code, String key) throws IOException, InterruptedException {
        return request(
            HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/check/otp?code=" + encode(code) + "&key=" + encode(key)))
                .GET()
        );
    }

    public String listWhatsAppTemplates(String accessToken) throws IOException, InterruptedException {
        return request(
            HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/wa/listTemplates"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
        );
    }

    public String sendWhatsAppTemplate(String accessToken, String payloadJson) throws IOException, InterruptedException {
        return jsonRequest("/wa/sendTemplates", accessToken, payloadJson);
    }

    private String jsonRequest(String path, String accessToken, String payloadJson) throws IOException, InterruptedException {
        return request(
            HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payloadJson))
        );
    }

    private String request(HttpRequest.Builder builder) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(
            builder.timeout(Duration.ofSeconds(30)).build(),
            HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() >= 400) {
            throw new IOException("Nvoip request failed with status " + response.statusCode() + ": " + response.body());
        }

        return response.body();
    }

    private String resolveBasicAuth() {
        if (oauthClientId != null && !oauthClientId.isBlank() && oauthClientSecret != null && !oauthClientSecret.isBlank()) {
            return encodeBasicAuth(oauthClientId, oauthClientSecret);
        }

        throw new IllegalStateException("Missing OAuth client credentials. Configure oauthClientId + oauthClientSecret.");
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String escapeJson(String value) {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
    }

    private static String trimTrailingSlash(String value) {
        return value.replaceAll("/+$", "");
    }
}
