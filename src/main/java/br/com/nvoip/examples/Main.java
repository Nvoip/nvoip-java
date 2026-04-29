package br.com.nvoip.examples;

import br.com.nvoip.NvoipClient;

public final class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("Use one of: auth-token, balance, send-sms, create-call, send-otp, check-otp, wa-list, wa-send");
        }

        NvoipClient client = new NvoipClient(
            System.getenv("NVOIP_BASE_URL"),
            System.getenv("NVOIP_OAUTH_CLIENT_ID"),
            System.getenv("NVOIP_OAUTH_CLIENT_SECRET")
        );

        String command = args[0];
        switch (command) {
            case "auth-token":
                System.out.println(client.createAccessToken(env("NVOIP_NUMBERSIP"), env("NVOIP_USER_TOKEN")));
                break;
            case "balance":
                System.out.println(client.getBalance(accessTokenOrCreate(client)));
                break;
            case "send-sms":
                System.out.println(client.sendSms(
                    accessTokenOrCreate(client),
                    firstNonBlank(System.getenv("NVOIP_TARGET_NUMBER"), "11999999999"),
                    firstNonBlank(System.getenv("NVOIP_SMS_MESSAGE"), "Mensagem de teste Nvoip")
                ));
                break;
            case "create-call":
                System.out.println(client.createCall(
                    accessTokenOrCreate(client),
                    env("NVOIP_CALLER"),
                    firstNonBlank(System.getenv("NVOIP_TARGET_NUMBER"), "11999999999")
                ));
                break;
            case "send-otp":
                System.out.println(client.sendOtp(
                    accessTokenOrCreate(client),
                    buildOtpPayload()
                ));
                break;
            case "check-otp":
                System.out.println(client.checkOtp(env("NVOIP_OTP_CODE"), env("NVOIP_OTP_KEY")));
                break;
            case "wa-list":
                System.out.println(client.listWhatsAppTemplates(accessTokenOrCreate(client)));
                break;
            case "wa-send":
                System.out.println(client.sendWhatsAppTemplate(
                    accessTokenOrCreate(client),
                    buildWhatsAppPayload()
                ));
                break;
            default:
                throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    private static String accessTokenOrCreate(NvoipClient client) throws Exception {
        String accessToken = firstNonBlank(System.getenv("NVOIP_ACCESS_TOKEN"), "");
        if (!accessToken.isBlank()) {
            return accessToken;
        }
        String response = client.createAccessToken(env("NVOIP_NUMBERSIP"), env("NVOIP_USER_TOKEN"));
        String token = extractJsonString(response, "access_token");
        if (token.isBlank()) {
            throw new IllegalStateException("access_token not found in OAuth response: " + response);
        }
        return token;
    }

    private static String buildOtpPayload() {
        StringBuilder payload = new StringBuilder("{");
        appendJsonField(payload, "sms", firstNonBlank(System.getenv("NVOIP_OTP_SMS"), System.getenv("NVOIP_TARGET_NUMBER")));
        appendJsonField(payload, "voice", System.getenv("NVOIP_OTP_VOICE"));
        appendJsonField(payload, "email", System.getenv("NVOIP_OTP_EMAIL"));
        payload.append("}");
        return payload.toString();
    }

    private static String buildWhatsAppPayload() {
        String bodyVariables = firstNonBlank(System.getenv("NVOIP_WA_BODY_VARIABLES"), "[]");
        String headerVariables = firstNonBlank(System.getenv("NVOIP_WA_HEADER_VARIABLES"), "[]");
        String toFlow = firstNonBlank(System.getenv("NVOIP_WA_TO_FLOW"), "false");

        return "{"
            + "\"idTemplate\":\"" + escapeJson(env("NVOIP_WA_TEMPLATE_ID")) + "\","
            + "\"destination\":\"" + escapeJson(env("NVOIP_WA_DESTINATION")) + "\","
            + "\"instance\":\"" + escapeJson(env("NVOIP_WA_INSTANCE")) + "\","
            + "\"language\":\"" + escapeJson(firstNonBlank(System.getenv("NVOIP_WA_LANGUAGE"), "pt_BR")) + "\","
            + "\"bodyVariables\":" + bodyVariables + ","
            + "\"headerVariables\":" + headerVariables + ","
            + "\"functions\":{\"to_flow\":" + toFlow + "}"
            + "}";
    }

    private static void appendJsonField(StringBuilder payload, String key, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (payload.length() > 1) {
            payload.append(",");
        }
        payload.append("\"").append(key).append("\":\"").append(escapeJson(value)).append("\"");
    }

    private static String env(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + name);
        }
        return value;
    }

    private static String firstNonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String extractJsonString(String response, String key) {
        String marker = "\"" + key + "\":\"";
        int startIndex = response.indexOf(marker);
        if (startIndex < 0) {
            return "";
        }
        int contentStart = startIndex + marker.length();
        int endIndex = response.indexOf("\"", contentStart);
        if (endIndex < 0) {
            return "";
        }
        return response.substring(contentStart, endIndex);
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
