package me.sebastian420.PandaTNTFusion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static com.mojang.text2speech.Narrator.LOGGER;


public class PandaTNTConfig {
    private static final File CONFIG_FILE = new File("./config/PandaTNTFusion.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static int MaxTNTPrimed;


    public static void loadOrGenerateConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                MaxTNTPrimed = json.get("MaxTNNTPrimed").getAsInt();
            } catch (IOException e) {
                generateConfig();
            }
        } else {
            generateConfig();
        }
    }

    private static void generateConfig() {

        JsonObject json = new JsonObject();
        json.addProperty("MaxTNNTPrimed", 100);
        MaxTNTPrimed = 100;
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(json, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to write the configuration file.", e);
        }
    }
}
