package dev.apolo.api.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TpaRequestModel {
    String fromUuid;
    String fromName;
    String targetUuid;
    String targetName;
    boolean hereRequest;
    long createdAt;
    long expiresAt;
}
