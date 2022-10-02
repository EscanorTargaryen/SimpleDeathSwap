package it.escanortargaryen.simpledeathswap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    /**
     * The time of a round, before the swap
     */
    private int roundTime;
    /**
     * The time you remain in creative
     */
    private int creativeTime;

    /**
     * Create a new ConfigManager
     */
    public ConfigManager() {
        SimpleDeathSwap.INSTANCE.saveResource("config.yml", false);

        loadConfig();

    }

    private void loadConfig() {
        final YamlConfiguration config = new YamlConfiguration();
        File file = new File(SimpleDeathSwap.INSTANCE.getDataFolder(), "config.yml");

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        roundTime = config.getInt("round-time");

        if (roundTime < 100) {

            throw new RuntimeException("The time of a round must be more than 100 secs");
        }

        creativeTime = config.getInt("creative-time");
        if (creativeTime > (roundTime - 30 - 50)) {

            throw new RuntimeException("The time you are in creative must be less than " + (roundTime - 30 - 50) + " (depends on the total time of the round)");
        }
        if (creativeTime < 1) {

            throw new RuntimeException("The time you are in creative must be greater than 0 (depends on the total time of the round)");
        }

    }

    public int getCreativeTime() {
        return creativeTime;
    }

    public int getRoundTime() {
        return roundTime;
    }
}
