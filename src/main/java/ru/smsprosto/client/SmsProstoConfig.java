package ru.smsprosto.client;

import java.util.Objects;

public final class SmsProstoConfig {

    private final String apiKey;
    private final String email;
    private final String password;
    private final String senderName;
    private final String baseUrl;

    private SmsProstoConfig(Builder builder) {
        this.apiKey = builder.apiKey;
        this.email = builder.email;
        this.password = builder.password;
        this.senderName = builder.senderName;
        this.baseUrl = builder.baseUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getApiKey() { return apiKey; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getSenderName() { return senderName; }
    public String getBaseUrl() { return baseUrl; }

    public static final class Builder {
        private String apiKey;
        private String email;
        private String password;
        private String senderName;
        private String baseUrl = "https://ssl.bs00.ru/";

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder credentials(String email, String password) {
            this.email = email;
            this.password = password;
            return this;
        }

        public Builder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public SmsProstoConfig build() {
            boolean hasApiKey = apiKey != null && !apiKey.trim().isEmpty();
            boolean hasCredentials = email != null && !email.trim().isEmpty()
                    && password != null && !password.isEmpty();

            if (hasApiKey == hasCredentials) {
                throw new IllegalArgumentException(
                        "Specify either API Key or Email/Password");
            }

            Objects.requireNonNull(senderName, "Sender name is required");
            if (senderName.trim().isEmpty()) {
                throw new IllegalArgumentException("Sender name cannot be empty");
            }

            Objects.requireNonNull(baseUrl, "Base URL is required");
            if (baseUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("Base URL cannot be empty");
            }

            return new SmsProstoConfig(this);
        }
    }
}
