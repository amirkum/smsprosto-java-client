package ru.smsprosto.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SmsProstoClientTest {

    private MockWebServer server;
    private SmsProstoClient client;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();

        SmsProstoConfig config = SmsProstoConfig.builder()
                .apiKey("test-api-key")
                .senderName("TestSender")
                .baseUrl(server.url("/").toString())
                .build();

        client = SmsProstoClient.builder()
                .config(config)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void shouldSendSms() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"response\":{\"msg\":{\"err_code\":0,"
                        + "\"text\":\"OK\",\"type\":\"success\"},"
                        + "\"data\":{\"id\":12345,\"credits\":1.5,"
                        + "\"n_raw_sms\":1,\"sender_name\":\"TestSender\"}}}"));

        SmsRequest request = SmsRequest.builder()
                .phone("79991234567")
                .text("Test message")
                .build();

        SmsResponse response = client.send(request);

        assertTrue(response.isSuccessful());
        assertEquals(Long.valueOf(12345), response.getSmsId());

        RecordedRequest recorded = server.takeRequest(1, TimeUnit.SECONDS);
        assertNotNull(recorded);
        assertEquals("POST", recorded.getMethod());

        String body = recorded.getBody().readUtf8();
        assertTrue(body.contains("method=push_msg"));
        assertTrue(body.contains("phone=79991234567"));
        assertTrue(body.contains("text=Test+message"));
        assertTrue(body.contains("key=test-api-key"));
    }

    @Test
    void shouldReturnApiErrorInResponse() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"response\":{\"msg\":{\"err_code\":10,"
                        + "\"text\":\"Invalid phone\",\"type\":\"error\"}}}"));

        SmsResponse response = client.send(SmsRequest.builder()
                .phone("123")
                .text("Test")
                .build());

        assertFalse(response.isSuccessful());
        assertEquals(10, response.getErrorCode());
    }

    @Test
    void shouldRejectEmptyPhone() {
        assertThrows(IllegalArgumentException.class,
                () -> SmsRequest.builder().phone("").text("Test").build());
    }
}
