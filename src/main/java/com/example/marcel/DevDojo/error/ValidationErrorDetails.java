package com.example.marcel.DevDojo.error;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorDetails extends MasterErrorDetails {

    private Map<String, String> fields;

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    private ValidationErrorDetails() {
    }

    public static final class Builder {
        private String title;
        private String detail;
        private LocalDateTime timestamp;
        private String developerMessage;
        private Map<String, String> fields;

        private Builder() {
        }

        public static ValidationErrorDetails.Builder newBuilder() {
            return new ValidationErrorDetails.Builder();
        }

        public ValidationErrorDetails.Builder title(String title) {
            this.title = title;
            return this;
        }

        public ValidationErrorDetails.Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public ValidationErrorDetails.Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ValidationErrorDetails.Builder developerMessage(String developerMessage) {
            this.developerMessage = developerMessage;
            return this;
        }

        public ValidationErrorDetails.Builder fields(Map<String, String> fields) {
            this.fields = fields;
            return this;
        }


        public ValidationErrorDetails build() {
            ValidationErrorDetails validationErrorDetails = new ValidationErrorDetails();
            validationErrorDetails.setTimestamp(this.timestamp);
            validationErrorDetails.setTitle(this.title);
            validationErrorDetails.setDetail(this.detail);
            validationErrorDetails.setDeveloperMessage(this.developerMessage);
            validationErrorDetails.setFields(this.fields);
            return validationErrorDetails;
        }
    }
}
