package dev.apolo.api.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WarpModel {
    String name;
    String worldName;
    double x;
    double y;
    double z;
    float yaw;
    float pitch;
    String createdBy;
    long createdAt;
}
