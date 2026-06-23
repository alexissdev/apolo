package dev.apolo.api.model;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder(toBuilder = true)
public class UserModel {
    String uuid;
    String username;
    String lastKnownName;
    double balance;
    long firstJoin;
    long lastSeen;
    boolean flyEnabled;
    boolean godModeEnabled;
    PlayerRank rank;
    Map<String, String> metadata;
}
