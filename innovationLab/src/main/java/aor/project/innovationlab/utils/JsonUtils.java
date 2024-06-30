package aor.project.innovationlab.utils;

import aor.project.innovationlab.enums.NotificationType;
import aor.project.innovationlab.utils.ws.MessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import aor.project.innovationlab.gson.InstantAdapter;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;


public class JsonUtils {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
                    jsonWriter.value(localDate.toString());
                }

                @Override
                public LocalDate read(JsonReader jsonReader) throws IOException {
                    return LocalDate.parse(jsonReader.nextString());
                }
            })
            .registerTypeAdapter(Duration.class, new TypeAdapter<Duration>() {
                @Override
                public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
                    jsonWriter.value(duration.toString());
                }

                @Override
                public Duration read(JsonReader jsonReader) throws IOException {
                    return Duration.parse(jsonReader.nextString());
                }
            })
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
    public static <T> T convertJsonStringToObject(String jsonString, Class<T> classOfT) {
        return gson.fromJson(jsonString, classOfT);
    }

    public static NotificationType getMessageTypeFromJson(String jsonString) {
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        int typeValue = jsonObject.get("type").getAsInt();
        return NotificationType.fromValue(typeValue);
    }
}
