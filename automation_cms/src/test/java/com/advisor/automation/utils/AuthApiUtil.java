package com.advisor.automation.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Backend API helper used by UI tests to guarantee advisor accounts exist.
 *
 * This avoids hard-coding existing LoginEmail/LoginPassword values in Excel.
 */
public final class AuthApiUtil {
    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private AuthApiUtil() {}

    public static String uniquifyEmail(String baseEmail, String testCaseId) {
        if (baseEmail == null || baseEmail.isBlank()) return baseEmail;

        int at = baseEmail.indexOf('@');
        if (at <= 0) return baseEmail;

        String local = baseEmail.substring(0, at);
        String domain = baseEmail.substring(at); // includes '@'
        long salt = Math.abs(System.nanoTime() % 1000000);
        return local + "+" + testCaseId + "_" + salt + domain;
    }

    public static void signupAdvisorIfNeeded(String agentName, String email, String password) {
        // We treat signup as "ensure exists": unique emails should create a fresh user (200),
        // but if something already exists (400), we ignore and rely on UI login.
        String uri = BASE_URL + "/api/auth/signup";

        String json = "{"
                + "\"name\":\"" + escapeJson(agentName) + "\","
                + "\"email\":\"" + escapeJson(email) + "\","
                + "\"password\":\"" + escapeJson(password) + "\""
                + "}";

        HttpRequest req = HttpRequest.newBuilder(URI.create(uri))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) return;
            if (res.statusCode() == 400) return; // already exists / validation

            throw new RuntimeException("Signup failed (" + res.statusCode() + "): " + res.body());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Signup request failed: " + e.getMessage(), e);
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "");
    }
}

