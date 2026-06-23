package dev.apolo.economy.sync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EconomyUpdateMessage {
    private String uuid;
    private double newBalance;
}
