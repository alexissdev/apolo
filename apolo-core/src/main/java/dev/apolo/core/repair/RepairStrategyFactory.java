package dev.apolo.core.repair;

import dev.apolo.core.repair.impl.AllRepairStrategy;
import dev.apolo.core.repair.impl.ArmorRepairStrategy;
import dev.apolo.core.repair.impl.HandRepairStrategy;

public class RepairStrategyFactory {
    private final HandRepairStrategy handStrategy = new HandRepairStrategy();
    private final ArmorRepairStrategy armorStrategy = new ArmorRepairStrategy();
    private final AllRepairStrategy allStrategy = new AllRepairStrategy();

    public RepairStrategy getStrategy(String input) {
        if (input == null || input.isEmpty() || input.equalsIgnoreCase("hand")) {
            return handStrategy;
        }
        switch (input.toLowerCase()) {
            case "armor": return armorStrategy;
            case "all": return allStrategy;
            default: return handStrategy;
        }
    }
}
