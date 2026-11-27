package ru.smsprosto.client.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import ru.smsprosto.client.SmsProstoConfig;
import ru.smsprosto.client.SmsProstoException;
import ru.smsprosto.client.SmsRequest;
import ru.smsprosto.client.SmsResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public final class SmsProstoApi {

    private final SmsProstoConfig config;
    private final OkHttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public SmsProstoApi(SmsProstoConfig config, OkHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }

    public SmsResponse send(SmsRequest sms) {
        Request request = createRequest(sms);
        try (Response response = httpClient.newCall(request).execute()) {
            return handleResponse(response);
        } catch (IOException e) {
            throw new SmsProstoException(
                    "Failed to communicate with SMS-PROSTO",
                    -1, null, e);
        }
    }

    public CompletableFuture<SmsResponse> sendAsync(SmsRequest sms) {
        final CompletableFuture<SmsResponse> future =
                new CompletableFuture<SmsResponse>();

        httpClient.newCall(createRequest(sms)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(new SmsProstoException(
                        "Failed to communicate with SMS-PROSTO",
                        -1, null, e));
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (Response closeableResponse = response) {
                    future.complete(handleResponse(closeableResponse));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });

        return future;
    }

    private Request createRequest(SmsRequest sms) {
        FormBody.Builder form = new FormBody.Builder()
                .add("method", "push_msg")
                .add("format", "json")
                .add("phone", sms.getPhone())
                .add("text", sms.getText())
                .add("sender_name", config.getSenderName());

        if (config.getApiKey() != null) {
            form.add("key", config.getApiKey());
        } else {
            form.add("email", config.getEmail())
                    .add("password", config.getPassword());
        }

        if (sms.getPriority() != null) {
            form.add("priority", sms.getPriority().toString());
        }
        if (sms.getExternalId() != null) {
            form.add("external_id", sms.getExternalId());
        }

        return new Request.Builder()
                .url(config.getBaseUrl())
                .post(form.build())
                .build();
    }

    private SmsResponse handleResponse(Response response) throws IOException {
        int status = response.code();

        if (!response.isSuccessful()) {
            throw new SmsProstoException(
                    "SMS-PROSTO HTTP error: " + status, -1, status);
        }

        ResponseBody body = response.body();
        if (body == null) {
            throw new SmsProstoException("Empty response from SMS-PROSTO");
        }

        return parseResponse(body.string());
    }

    private SmsResponse parseResponse(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode response = root.path("response");
            JsonNode msg = response.path("msg");
            JsonNode data = response.path("data");

            if (response.isMissingNode() || msg.isMissingNode()) {
                throw new SmsProstoException("Unexpected SMS-PROSTO response");
            }

            return new SmsResponse(
                    msg.path("err_code").asInt(-1),
                    text(msg, "text"),
                    text(msg, "type"),
                    longValue(data, "id"),
                    decimalValue(data, "credits"),
                    integerValue(data, "n_raw_sms"),
                    text(data, "sender_name")
            );
        } catch (SmsProstoException e) {
            throw e;
        } catch (Exception e) {
            throw new SmsProstoException(
                    "Cannot parse SMS-PROSTO response", -1, null, e);
        }
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private Long longValue(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asLong();
    }

    private Integer integerValue(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asInt();
    }

    private BigDecimal decimalValue(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull()
                ? null : value.decimalValue();
    }
}
