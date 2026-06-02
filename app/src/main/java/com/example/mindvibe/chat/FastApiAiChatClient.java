package com.example.mindvibe.chat;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FastApiAiChatClient implements AiChatClient {
    private static final int CONNECT_TIMEOUT_MS = 15000;
    private static final int READ_TIMEOUT_MS = 30000;

    private final String endpointUrl;
    private final Handler mainHandler;
    private final ExecutorService networkExecutor;

    public FastApiAiChatClient(String endpointUrl) {
        this.endpointUrl = endpointUrl;
        mainHandler = new Handler(Looper.getMainLooper());
        networkExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void sendMessage(String message, AiResponseCallback callback) {
        if (isBlank(endpointUrl)) {
            postError(callback, "Chua cau hinh AI_BACKEND_URL trong local.properties.");
            return;
        }

        networkExecutor.execute(() -> {
            try {
                String response = requestAiResponse(message);
                postSuccess(callback, response);
            } catch (IOException | JSONException exception) {
                String errorMessage = exception.getMessage();
                postError(callback, isBlank(errorMessage)
                        ? "Khong the ket noi backend luc nay. Ban thu lai sau nhe."
                        : errorMessage);
            }
        });
    }

    @Override
    public void shutdown() {
        networkExecutor.shutdownNow();
    }

    private String requestAiResponse(String message) throws IOException, JSONException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(endpointUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            JSONObject requestBody = new JSONObject();
            requestBody.put("message", message);
            writeJson(connection, requestBody);

            int statusCode = connection.getResponseCode();
            String responseBody = readBody(statusCode >= 200 && statusCode < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream());

            if (statusCode < 200 || statusCode >= 300) {
                throw new IOException(buildServerErrorMessage(statusCode, responseBody));
            }

            String response = parseAiResponse(responseBody);
            if (isBlank(response)) {
                throw new IOException("Backend chua tra ve noi dung phan hoi.");
            }
            return response.trim();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void writeJson(HttpURLConnection connection, JSONObject requestBody) throws IOException {
        byte[] payload = requestBody.toString().getBytes(StandardCharsets.UTF_8);
        connection.setFixedLengthStreamingMode(payload.length);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(payload);
        }
    }

    private String readBody(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        )) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }

    private String parseAiResponse(String responseBody) throws JSONException {
        if (isBlank(responseBody)) {
            return "";
        }

        Object parsed = new JSONTokener(responseBody.trim()).nextValue();
        if (parsed instanceof String) {
            return (String) parsed;
        }
        if (!(parsed instanceof JSONObject)) {
            return responseBody;
        }

        JSONObject json = (JSONObject) parsed;
        String response = firstString(json, "reply", "response", "message", "content", "text", "answer");
        if (!isBlank(response)) {
            return response;
        }

        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            return firstString(data, "reply", "response", "message", "content", "text", "answer");
        }
        return "";
    }

    private String buildServerErrorMessage(int statusCode, String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            String detail = firstString(json, "detail", "message", "error");
            if (!isBlank(detail)) {
                return "Backend tra ve loi " + statusCode + ": " + detail;
            }
        } catch (JSONException ignored) {
        }
        return "Backend tra ve loi " + statusCode + ". Ban thu lai sau nhe.";
    }

    private String firstString(JSONObject json, String... keys) {
        for (String key : keys) {
            String value = json.optString(key, "");
            if (!isBlank(value)) {
                return value;
            }
        }
        return "";
    }

    private void postSuccess(AiResponseCallback callback, String response) {
        mainHandler.post(() -> callback.onSuccess(response));
    }

    private void postError(AiResponseCallback callback, String message) {
        mainHandler.post(() -> callback.onError(message));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
