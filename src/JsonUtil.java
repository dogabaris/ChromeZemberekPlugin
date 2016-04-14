import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

/**
 * Created by Master on 21.03.2016.
 */
public class JsonUtil {
    public static Map<String, String> parse(String object) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(object, Map.class);
    }

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }
}
