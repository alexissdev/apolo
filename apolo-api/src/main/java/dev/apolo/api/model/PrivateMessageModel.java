package dev.apolo.api.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PrivateMessageModel {
    String fromUuid;
    String fromName;
    String toUuid;
    String toName;
    String message;
    long timestamp;
}
