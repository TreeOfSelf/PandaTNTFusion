package me.TreeOfSelf.PandaTNTFusion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PandaTNTConfig {
    private static final File CONFIG_FILE = new File("./config/PandaTNTFusion.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static int MaxTNTPrimed;


    public static void loadOrGenerateConfig() {
        File configDir = CONFIG_FILE.getParentFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                if (json == null) {
                    generateConfig();
                } else if (json.has("MaxTNTPrimed")) {
                    MaxTNTPrimed = json.get("MaxTNTPrimed").getAsInt();
                } else if (json.has("MaxTNNTPrimed")) {
                    MaxTNTPrimed = json.get("MaxTNNTPrimed").getAsInt();
                    generateConfig();
                } else {
                    generateConfig();
                }
            } catch (IOException | JsonSyntaxException | NumberFormatException e) {
                generateConfig();
            }
        } else {
            generateConfig();
        }
    }

    private static void generateConfig() {
        JsonObject json = new JsonObject();
        json.addProperty("MaxTNTPrimed", MaxTNTPrimed > 0 ? MaxTNTPrimed : 100);
        if (MaxTNTPrimed <= 0) {
            MaxTNTPrimed = 100;
        }

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(json, writer);
        } catch (IOException ignored) {
        }
    }
}