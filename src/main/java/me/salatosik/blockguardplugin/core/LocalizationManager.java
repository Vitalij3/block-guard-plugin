package me.salatosik.blockguardplugin.core;

import me.salatosik.blockguardplugin.exceptions.InvalidLocalizationKeyException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class LocalizationManager {
    public LocalizationManager(JavaPlugin javaPlugin) throws InvalidLocalizationKeyException, IOException, InvalidConfigurationException {
        String localizationKey = javaPlugin.getConfig().getString("plugin-language");

        if(verifyLang(localizationKey)) this.key = AllowedLocalizationKey.getByString(localizationKey);
        else if(localizationKey == null) throw new InvalidLocalizationKeyException("No localization parameter found!");
        else throw new InvalidLocalizationKeyException("Unknown key. The language key you entered: [key]".replace("[key]", localizationKey));

        javaPlugin.saveResource("lang.yml", false);
        FileConfiguration configuration = new YamlConfiguration();
        configuration.load(new File(javaPlugin.getDataFolder().getAbsoluteFile(), "lang.yml"));
        this.configurationValues = configuration.getValues(true);
    }

    private static boolean verifyLang(String key) {
        if(key == null) return false;
        else return AllowedLocalizationKey.getByString(key) != AllowedLocalizationKey.UNDEFINED;
    }

    public enum AllowedLocalizationKey {
        ENGLISH("eng"), UKRAINIAN("uk"), RUSSIAN("ru"), UNDEFINED("undefined");

        public final String localeName;

        AllowedLocalizationKey(String localeName) {
            this.localeName = localeName;
        }

        public static AllowedLocalizationKey getByString(String localeName) {
            for(AllowedLocalizationKey key: AllowedLocalizationKey.values()) {
                if(key.localeName.equalsIgnoreCase(localeName)) return key;
            }

            return UNDEFINED;
        }
    }

    private AllowedLocalizationKey key;
    private final Map<String, Object> configurationValues;

    public String getKey(String key) {
        String text = (String) configurationValues.get(key + "." + this.key.localeName);
        if(text == null) text = "none";
        return text;
    }

    public boolean changeLanguage(AllowedLocalizationKey key) {
        if(key != AllowedLocalizationKey.UNDEFINED) {
            this.key = key;
            return true;
        }

        return false;
    }

    public AllowedLocalizationKey getLocalizationKey() {
        return key;
    }
}
