package dev.apolo.api.model;

import dev.apolo.api.enums.TransactionStatus;
import dev.apolo.api.enums.TransactionType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TransactionModel {
    String id;
    String fromUuid;
    String toUuid;
    double amount;
    TransactionType type;
    String reason;
    long timestamp;
    TransactionStatus status;
}
