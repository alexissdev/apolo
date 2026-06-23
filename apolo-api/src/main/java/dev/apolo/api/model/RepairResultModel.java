package dev.apolo.api.model;

import dev.apolo.api.enums.RepairType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RepairResultModel {
    int itemsRepaired;
    RepairType type;
}
