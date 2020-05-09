package com.example.marcel.DevDojo.error;

import java.time.LocalDateTime;

public class ResourceNotFoundDetails extends MasterErrorDetails {
    private ResourceNotFoundDetails() {
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

        public ResourceNotFoundDetails build() {
            ResourceNotFoundDetails resourceNotFoundDetails = new ResourceNotFoundDetails();
            resourceNotFoundDetails.setTimestamp(this.timestamp);
            resourceNotFoundDetails.setTitle(this.title);
            resourceNotFoundDetails.setDetail(this.detail);
            resourceNotFoundDetails.setDeveloperMessage(this.developerMessage);
            return resourceNotFoundDetails;
        }
    }
}
