package ru.smsprosto.client;

import okhttp3.OkHttpClient;
import ru.smsprosto.client.internal.SmsProstoApi;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class SmsProstoClient {

    private final SmsProstoApi api;

    private SmsProstoClient(Builder builder) {
        OkHttpClient client = builder.httpClient;
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
        }
        this.api = new SmsProstoApi(builder.config, client);
    }

    public static Builder builder() {
        return new Builder();
    }

    public SmsResponse send(SmsRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("SMS request is required");
        }
        return api.send(request);
    }

    public CompletableFuture<SmsResponse> sendAsync(SmsRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("SMS request is required");
        }
        return api.sendAsync(request);
    }

    public static final class Builder {
        private SmsProstoConfig config;
        private OkHttpClient httpClient;

        public Builder config(SmsProstoConfig config) {
            this.config = config;
            return this;
        }

        public Builder httpClient(OkHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public SmsProstoClient build() {
            if (config == null) {
                throw new IllegalStateException("SmsProstoConfig is required");
            }
            return new SmsProstoClient(this);
        }
    }
}
