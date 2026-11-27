# SMS-PROSTO Java Client

Standalone Java SDK for integrating with the SMS-PROSTO API.

## Requirements

- Java 8+
- Maven 3.6+

## Build and test

```bash
mvn clean test
mvn clean package
```

## Usage

```java
import ru.smsprosto.client.SmsProstoClient;
import ru.smsprosto.client.SmsProstoConfig;
import ru.smsprosto.client.SmsRequest;
import ru.smsprosto.client.SmsResponse;

public class Example {
    public static void main(String[] args) {
        SmsProstoConfig config = SmsProstoConfig.builder()
                .apiKey(System.getenv("SMS_PROSTO_API_KEY"))
                .senderName("MyBrand")
                .build();

        SmsProstoClient client = SmsProstoClient.builder()
                .config(config)
                .build();

        SmsRequest request = SmsRequest.builder()
                .phone("79991234567")
                .text("Your verification code: 123456")
                .build();

        SmsResponse response = client.send(request);

        if (response.isSuccessful()) {
            System.out.println("SMS ID: " + response.getSmsId());
        } else {
            System.err.println("SMS-PROSTO error: " + response.getMessage());
        }
    }
}
```

## Authentication

API key:

```java
SmsProstoConfig config = SmsProstoConfig.builder()
        .apiKey("YOUR_API_KEY")
        .senderName("MyBrand")
        .build();
```

Email and password:

```java
SmsProstoConfig config = SmsProstoConfig.builder()
        .credentials("account@example.com", "YOUR_PASSWORD")
        .senderName("MyBrand")
        .build();
```

## Async sending

```java
client.sendAsync(request)
        .thenAccept(response -> {
            if (response.isSuccessful()) {
                System.out.println("SMS ID: " + response.getSmsId());
            } else {
                System.err.println(response.getMessage());
            }
        })
        .exceptionally(error -> {
            error.printStackTrace();
            return null;
        });
```

## Custom OkHttp client

```java
OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();

SmsProstoClient client = SmsProstoClient.builder()
        .config(config)
        .httpClient(httpClient)
        .build();
```

## Important

This is an initial implementation based on the API parameters and response structure
described during development: `push_msg`, `phone`, `text`, `sender_name`,
`priority`, `external_id`, and `response.msg` / `response.data`.

Before using it in production, verify the exact endpoint, authentication parameter
names, response JSON fields, and error codes against the current SMS-PROSTO
documentation and test with a controlled recipient. Do not automatically retry
sending after a network timeout: the provider may already have accepted the SMS,
which could result in duplicate messages.

Never commit API keys or account passwords to source control.
