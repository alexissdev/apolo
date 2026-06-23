package dev.apolo.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlayerRank {
    DEFAULT("&7"),
    VIP("&a"),
    MVP("&b"),
    ADMIN("&c"),
    OWNER("&4");

    private final String colorCode;
}
