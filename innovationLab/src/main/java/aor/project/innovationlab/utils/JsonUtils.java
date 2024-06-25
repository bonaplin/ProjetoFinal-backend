package aor.project.innovationlab.utils;

import aor.project.innovationlab.utils.ws.MessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import aor.project.innovationlab.gson.InstantAdapter;
import com.google.gson.JsonObject;

import java.time.Instant;


public class JsonUtils {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    private static JsonbConfig config = new JsonbConfig().withFormatting(true);
    private static Jsonb jsonb = JsonbBuilder.create(config);
    public static String convertObjectToJson(Object object) {
        return jsonb.toJson(object);
    }

    public static <T> String convertDtoToJson(T dto) {
        return gson.toJson(dto);
    }

    public static JsonObject convertJsonStringToJsonObject(String jsonString) {
        return gson.fromJson(jsonString, JsonObject.class);
    }

    public static MessageType getMessageTypeFromJson(String jsonString) {
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        int typeValue = jsonObject.get("type").getAsInt();
        return MessageType.fromValue(typeValue);
    }
}
