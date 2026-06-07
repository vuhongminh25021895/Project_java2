package Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonConfig {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private GsonConfig() {}

    public static Gson getGson() { return gson; }
}
