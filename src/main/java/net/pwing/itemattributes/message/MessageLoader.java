package net.pwing.itemattributes.message;

import net.pwing.itemattributes.ItemAttributes;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public class MessageLoader {
    private static final Map<String, Message> MESSAGES = new LinkedHashMap<>();

    public static void load(ItemAttributes plugin) {
        Messages.init(); // Load all default messages

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try {
                messagesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not create messages.yml file!", e);
                return;
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(messagesFile);

        // Add any missing default messages to the config
        for (Map.Entry<String, Message> entry : MESSAGES.entrySet()) {
            config.addDefault(entry.getKey(), Messages.MINI_MESSAGE.serialize(entry.getValue().getText()));
        }

        config.options().copyDefaults(true);
        config.options().width(256);

        try {
            config.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not save messages.yml file!", e);
        }

        // Now, load messages from the config and override the defaults set here
        for (String key : config.getKeys(false)) {
            String rawMessage = config.getString(key);
            if (rawMessage == null) {
                plugin.getLogger().log(Level.WARNING, "Message for key " + key + " is missing in messages.yml!");
                continue;
            }

            Message message = MESSAGES.get(key);
            if (message == null) {
                plugin.getLogger().log(Level.WARNING, "Message for key " + key + " is not registered in the plugin!");
                continue;
            }

            try {
                message.setText(Messages.MINI_MESSAGE.deserialize(rawMessage));
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Could not parse message for key " + key + " from messages.yml!", e);
            }
        }
    }

    @Nullable
    public static Message get(String translationKey) {
        return MESSAGES.get(translationKey);
    }

    public static Message register(Message message) {
        String translationKey = message.getTranslationKey();
        if (MESSAGES.containsKey(translationKey)) {
            throw new IllegalArgumentException("Message with translation key " + translationKey + " is already registered!");
        }

        MESSAGES.put(translationKey, message);
        return message;
    }
}
