package com.github.cinnaio.itemprop.handler;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FileHandler {
    private final HashMap<String, Object> map;

    public FileHandler(JavaPlugin ins) {
        map = new HashMap<>();

        generateFile(ins);

        initItemsMap(ins);
        initTagsMap(ins);
    }

    public void reload(JavaPlugin ins) {
        ins.reloadConfig();

        map.clear();

        initItemsMap(ins);
        initTagsMap(ins);
    }

    public void generateFile(JavaPlugin ins) {
        if (!new File(ins.getDataFolder(), "tags.yml").exists())
            ins.saveResource("tags.yml", false);

        if (!new File(ins.getDataFolder(), "items/food.yml").exists())
            ins.saveResource("items/food.yml", false);

        ins.saveDefaultConfig();
    }

    public void initItem(String name) {

    }

    public void initItemsMap(JavaPlugin ins) {
        File itemsFolder = new File(ins.getDataFolder(), "items");

        if (!itemsFolder.exists() || !itemsFolder.isDirectory()) {
            return;
        }

        File[] tempFiles = itemsFolder.listFiles();

        if (tempFiles == null)
            return;

        for (File file : tempFiles) {
            if (!file.getName().endsWith(".yml"))
                continue;

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection itsSec = config.getConfigurationSection("items");

            if (itsSec == null) {
                continue;
            }

            for (String itemName : itsSec.getKeys(false)) {
                ConfigurationSection itSec = itsSec.getConfigurationSection(itemName);

                if (itSec == null) {
                    continue;
                }

                for (String object : itSec.getKeys(false)) {
                    String objectStr = file.getName().replace(".yml", "") + "." + itsSec.getName() + "." +  itSec.getName() + "." + object;

                    map.put(objectStr, itSec.get(object));
                }
            }
        }
    }

    public void initTagsMap(JavaPlugin ins) {
        File file = new File(ins.getDataFolder(), "tags.yml");

        YamlConfiguration tags = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection tagsSec = tags.getConfigurationSection("tags");

        if (tagsSec == null)
            return;

        for (String tagSec : tagsSec.getKeys(false)) {
            String tagSeccStr = tagsSec.getName() + "." + tagSec;

            map.put(tagSeccStr, tagsSec.get(tagSec));
        }
    }

    public HashMap<String, Object> getMap() {
        return map;
    }

    public ArrayList<String> getTags(HashMap<String, Object> map) {
        for (String tags : map.keySet()) {
            if (!tags.startsWith("tags.")) {
                continue;
            }

            Object value = map.get(tags);

            if (value instanceof ArrayList) {
                return (ArrayList<String>) value;
            }
        }
        return null;
    }
}
