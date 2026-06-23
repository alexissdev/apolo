package dev.apolo.redis.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonModelSerializer<T> implements ModelSerializer<T> {
    private final Gson gson;

    public GsonModelSerializer() {
        this.gson = new GsonBuilder().create();
    }

    @Override
    public String serialize(T model) {
        return gson.toJson(model);
    }

    @Override
    public T deserialize(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
