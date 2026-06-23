package dev.apolo.api.result;

import dev.apolo.api.messaging.MessageKey;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class ServiceResult<T> {

    public enum Status {
        SUCCESS,
        FAILURE,
        NO_PERMISSION,
        NOT_FOUND,
        COOLDOWN,
        ALREADY_EXISTS
    }

    private final Status status;
    private final T data;
    private final MessageKey messageKey;
    private final Map<String, String> placeholders;

    private ServiceResult(Status status, T data, MessageKey messageKey, Map<String, String> placeholders) {
        this.status = status;
        this.data = data;
        this.messageKey = messageKey;
        this.placeholders = placeholders != null ? placeholders : Collections.emptyMap();
    }

    public static <T> ServiceResult<T> success(T data) {
        return new ServiceResult<>(Status.SUCCESS, data, null, Collections.emptyMap());
    }

    public static <T> ServiceResult<T> success() {
        return new ServiceResult<>(Status.SUCCESS, null, null, Collections.emptyMap());
    }

    public static <T> ServiceResult<T> failure(MessageKey key) {
        return new ServiceResult<>(Status.FAILURE, null, key, Collections.emptyMap());
    }

    public static <T> ServiceResult<T> failure(MessageKey key, Map<String, String> placeholders) {
        return new ServiceResult<>(Status.FAILURE, null, key, placeholders);
    }

    public static <T> ServiceResult<T> noPermission(MessageKey key) {
        return new ServiceResult<>(Status.NO_PERMISSION, null, key, Collections.emptyMap());
    }

    public static <T> ServiceResult<T> notFound(MessageKey key) {
        return new ServiceResult<>(Status.NOT_FOUND, null, key, Collections.emptyMap());
    }

    public static <T> ServiceResult<T> notFound(MessageKey key, Map<String, String> placeholders) {
        return new ServiceResult<>(Status.NOT_FOUND, null, key, placeholders);
    }

    public static <T> ServiceResult<T> cooldown(MessageKey key, Map<String, String> placeholders) {
        return new ServiceResult<>(Status.COOLDOWN, null, key, placeholders);
    }

    public static <T> ServiceResult<T> alreadyExists(MessageKey key) {
        return new ServiceResult<>(Status.ALREADY_EXISTS, null, key, Collections.emptyMap());
    }

    public static <T> ServiceResult<T> alreadyExists(MessageKey key, Map<String, String> placeholders) {
        return new ServiceResult<>(Status.ALREADY_EXISTS, null, key, placeholders);
    }

    public Status getStatus() {
        return status;
    }

    public Optional<T> getData() {
        return Optional.ofNullable(data);
    }

    public Optional<MessageKey> getMessageKey() {
        return Optional.ofNullable(messageKey);
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}
