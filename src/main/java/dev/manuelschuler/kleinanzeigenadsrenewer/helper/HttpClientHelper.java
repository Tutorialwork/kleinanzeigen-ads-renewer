package dev.manuelschuler.kleinanzeigenadsrenewer.helper;

import generator.RandomUserAgentGenerator;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClientHelper {

    private HttpClientHelper() {
    }

    @SneakyThrows
    public static String doGetRequest(String url) {
        String randomUserAgent = RandomUserAgentGenerator.getNext();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request.Builder requestBuilder =
                new Request.Builder()
                        .url(url)
                        .method("GET", null)
                        .addHeader("User-Agent", randomUserAgent);

        Request request = requestBuilder.build();

        Response response = client.newCall(request).execute();

        int statusCode = response.code();
        if (statusCode != 200) {
            throw new RuntimeException("Failed to renew ad with status code: " + statusCode);
        }

        return response.body().string();
    }

}
