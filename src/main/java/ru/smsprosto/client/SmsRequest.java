package ru.smsprosto.client;

public final class SmsRequest {

    private final String phone;
    private final String text;
    private final Integer priority;
    private final String externalId;

    private SmsRequest(Builder builder) {
        this.phone = builder.phone;
        this.text = builder.text;
        this.priority = builder.priority;
        this.externalId = builder.externalId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPhone() { return phone; }
    public String getText() { return text; }
    public Integer getPriority() { return priority; }
    public String getExternalId() { return externalId; }

    public static final class Builder {
        private String phone;
        private String text;
        private Integer priority;
        private String externalId;

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder priority(Integer priority) {
            this.priority = priority;
            return this;
        }

        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public SmsRequest build() {
            if (phone == null || phone.trim().isEmpty()) {
                throw new IllegalArgumentException("Phone is required");
            }
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("Message text is required");
            }
            if (priority != null && (priority < 1 || priority > 4)) {
                throw new IllegalArgumentException("Priority must be between 1 and 4");
            }
            return new SmsRequest(this);
        }
    }
}
