package com.gabriaum.devroom.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    public static Object convertToBson(JsonElement value) {
        if (value.isJsonPrimitive()) {
            JsonPrimitive primitive = value.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isNumber()) {
                return primitive.getAsNumber();
            } else if (primitive.isString()) {
                return primitive.getAsString();
            }
        } else if (value.isJsonArray()) {
            JsonArray jsonArray = value.getAsJsonArray();
            List<Object> bsonArray = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                bsonArray.add(convertToBson(element));
            }
            return bsonArray;
        } else if (value.isJsonObject()) {
            JsonObject jsonObject = value.getAsJsonObject();
            Document bsonDocument = new Document();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                bsonDocument.append(entry.getKey(), convertToBson(entry.getValue()));
            }
            return bsonDocument;
        }
        return null;
    }
}
