package br.com.util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Objects;

public class HttpClient {
    public static void main(String[] args) throws IOException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/pessoas")).GET().build();

        assert client != null;
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        System.out.println("Status Code:" + Objects.<HttpResponse<String>>requireNonNull(response).statusCode());
        System.out.println("Response Body:" + response.body());
    }

    @Contract(pure = true)
    private static @Nullable HttpClient newHttpClient() {
        return null;
    }

    @org.jetbrains.annotations.Contract(pure = true)

    private @Nullable HttpResponse<String> send(HttpRequest request, HttpResponse.BodyHandler<String> stringBodyHandler) {

        return null;
    }
}
