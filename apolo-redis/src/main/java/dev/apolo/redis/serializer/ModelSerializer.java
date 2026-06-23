package dev.apolo.redis.serializer;

public interface ModelSerializer<T> {
    String serialize(T model);
    T deserialize(String json, Class<T> clazz);
}
