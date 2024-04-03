package com.github.cinnaio.itemprop.handler;

import com.github.cinnaio.itemprop.ItemProp;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.github.cinnaio.itemprop.handler.i18Handler.inferiorColor;
import static com.github.cinnaio.itemprop.handler.i18Handler.success_add;
import static com.github.cinnaio.itemprop.utils.MessageUtils.sendMessage;

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
        new i18Handler(new File(ins.getDataFolder(), "lang/zh_CN.yml"));
    }

    public void generateFile(JavaPlugin ins) {
        if (!new File(ins.getDataFolder(), "tags.yml").exists())
            ins.saveResource("tags.yml", false);

        if (!new File(ins.getDataFolder(), "items/food.yml").exists())
            ins.saveResource("items/food.yml", false);

        if (!new File(ins.getDataFolder(), "lang/zh_CN.yml").exists()) {
            ins.saveResource("lang/zh_CN.yml", false);
        }

        new i18Handler(new File(ins.getDataFolder(), "lang/zh_CN.yml"));

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

    public void addTagToFile(String n1, String n2, Player p) {
        File file = new File(ItemProp.getInstance().getDataFolder(), "tags.yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection tags = config.getConfigurationSection("tags");

        List<String> directoryList = tags.getStringList(n1);

        if (!directoryList.contains(n2)) {
            directoryList.add(n2);

            tags.set(n1, directoryList);

            try {
                config.save(file);
                sendMessage(p, (success_add + inferiorColor).replace("{0}", "&n" + n2 + "&r").replace("{1}", "&n" + n1 + "&r"));
                } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public HashMap<String, Object> getMap() {
        return map;
    }
}
