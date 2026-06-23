package dev.apolo.api.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CommandSpyModel {
    String playerUuid;
    String playerName;
    String command;
    String serverId;
    long timestamp;
}
