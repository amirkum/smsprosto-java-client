package ru.smsprosto.client;

import java.math.BigDecimal;

public final class SmsResponse {

    private final int errorCode;
    private final String message;
    private final String type;
    private final Long smsId;
    private final BigDecimal credits;
    private final Integer smsParts;
    private final String senderName;

    public SmsResponse(int errorCode, String message, String type,
                       Long smsId, BigDecimal credits, Integer smsParts,
                       String senderName) {
        this.errorCode = errorCode;
        this.message = message;
        this.type = type;
        this.smsId = smsId;
        this.credits = credits;
        this.smsParts = smsParts;
        this.senderName = senderName;
    }

    public boolean isSuccessful() { return errorCode == 0; }
    public int getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public Long getSmsId() { return smsId; }
    public BigDecimal getCredits() { return credits; }
    public Integer getSmsParts() { return smsParts; }
    public String getSenderName() { return senderName; }
}
