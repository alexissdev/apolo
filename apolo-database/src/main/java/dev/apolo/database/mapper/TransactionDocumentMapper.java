package dev.apolo.database.mapper;

import dev.apolo.api.enums.TransactionStatus;
import dev.apolo.api.enums.TransactionType;
import dev.apolo.api.model.TransactionModel;
import org.bson.Document;

public class TransactionDocumentMapper {

    public Document toDocument(TransactionModel transaction) {
        return new Document()
            .append("id", transaction.getId())
            .append("fromUuid", transaction.getFromUuid())
            .append("toUuid", transaction.getToUuid())
            .append("amount", transaction.getAmount())
            .append("type", transaction.getType().name())
            .append("reason", transaction.getReason())
            .append("timestamp", transaction.getTimestamp())
            .append("status", transaction.getStatus().name());
    }

    public TransactionModel fromDocument(Document doc) {
        return TransactionModel.builder()
            .id(doc.getString("id"))
            .fromUuid(doc.getString("fromUuid"))
            .toUuid(doc.getString("toUuid"))
            .amount(doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0)
            .type(TransactionType.valueOf(doc.getString("type")))
            .reason(doc.getString("reason"))
            .timestamp(doc.getLong("timestamp") != null ? doc.getLong("timestamp") : 0L)
            .status(TransactionStatus.valueOf(doc.getString("status")))
            .build();
    }
}
