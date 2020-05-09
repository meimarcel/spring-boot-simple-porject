package com.example.marcel.DevDojo.error;

import java.time.LocalDateTime;

public class MasterErrorDetails {
    private String title;
    private String detail;
    private LocalDateTime timestamp;
    private String developerMessage;

    public MasterErrorDetails() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public static final class Builder {
        private String title;
        private String detail;
        private LocalDateTime timestamp;
        private String developerMessage;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder developerMessage(String developerMessage) {
            this.developerMessage = developerMessage;
            return this;
        }

        public MasterErrorDetails build() {
            MasterErrorDetails masterErrorDetails = new MasterErrorDetails();
            masterErrorDetails.setTitle(title);
            masterErrorDetails.setDetail(detail);
            masterErrorDetails.setTimestamp(timestamp);
            masterErrorDetails.setDeveloperMessage(developerMessage);
            return masterErrorDetails;
        }
    }
}
