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
    // Over MaxTNTPrimed only big blasts are cut down: a blast counts as big when its TNT was already fused,
    // or when BigBlastTNT or more primed TNT are within BigBlastRadius blocks of it
    public static int BigBlastTNT = 16;
    public static int BigBlastRadius = 16;
    // Past this every chained TNT is cut down, so many small blasts spread out can't add up without limit
    public static int HardMaxTNTPrimed;


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
                    if (json.has("BigBlastTNT")) BigBlastTNT = json.get("BigBlastTNT").getAsInt();
                    if (json.has("BigBlastRadius")) BigBlastRadius = json.get("BigBlastRadius").getAsInt();
                    if (json.has("HardMaxTNTPrimed")) HardMaxTNTPrimed = json.get("HardMaxTNTPrimed").getAsInt();
                    if (!json.has("BigBlastTNT") || !json.has("BigBlastRadius") || !json.has("HardMaxTNTPrimed")) {
                        generateConfig();
                    }
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
        if (HardMaxTNTPrimed <= 0) {
            HardMaxTNTPrimed = MaxTNTPrimed * 2;
        }
        json.addProperty("BigBlastTNT", BigBlastTNT);
        json.addProperty("BigBlastRadius", BigBlastRadius);
        json.addProperty("HardMaxTNTPrimed", HardMaxTNTPrimed);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(json, writer);
        } catch (IOException ignored) {
        }
    }
}