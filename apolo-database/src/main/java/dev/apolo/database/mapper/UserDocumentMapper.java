package dev.apolo.database.mapper;

import dev.apolo.api.model.PlayerRank;
import dev.apolo.api.model.UserModel;
import org.bson.Document;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserDocumentMapper {

    public Document toDocument(UserModel user) {
        Document metadata = new Document();
        if (user.getMetadata() != null) {
            user.getMetadata().forEach(metadata::append);
        }
        return new Document()
            .append("uuid", user.getUuid())
            .append("username", user.getUsername())
            .append("lastKnownName", user.getLastKnownName())
            .append("balance", user.getBalance())
            .append("firstJoin", user.getFirstJoin())
            .append("lastSeen", user.getLastSeen())
            .append("flyEnabled", user.isFlyEnabled())
            .append("godModeEnabled", user.isGodModeEnabled())
            .append("rank", user.getRank().name())
            .append("metadata", metadata);
    }

    public UserModel fromDocument(Document doc) {
        Map<String, String> metadata = new HashMap<>();
        Document metaDoc = doc.get("metadata", Document.class);
        if (metaDoc != null) {
            metaDoc.forEach((k, v) -> metadata.put(k, String.valueOf(v)));
        }

        String rankStr = doc.getString("rank");
        PlayerRank rank;
        try {
            rank = PlayerRank.valueOf(rankStr);
        } catch (Exception e) {
            rank = PlayerRank.DEFAULT;
        }

        return UserModel.builder()
            .uuid(doc.getString("uuid"))
            .username(doc.getString("username"))
            .lastKnownName(doc.getString("lastKnownName"))
            .balance(doc.getDouble("balance") != null ? doc.getDouble("balance") : 0.0)
            .firstJoin(doc.getLong("firstJoin") != null ? doc.getLong("firstJoin") : 0L)
            .lastSeen(doc.getLong("lastSeen") != null ? doc.getLong("lastSeen") : 0L)
            .flyEnabled(doc.getBoolean("flyEnabled", false))
            .godModeEnabled(doc.getBoolean("godModeEnabled", false))
            .rank(rank)
            .metadata(Collections.unmodifiableMap(metadata))
            .build();
    }
}
