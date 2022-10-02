package it.escanortargaryen.simpledeathswap;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public class SimpleDeathSwap extends JavaPlugin {

    public static SimpleDeathSwap INSTANCE;
    /**
     * List of the games
     */
    public static List<Game> GAMES = new LinkedList<>();

    /**
     * The config manager
     */
    public static ConfigManager CONFIGMANAGER;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoad() {
        INSTANCE = this;
        CONFIGMANAGER = new ConfigManager();

        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(true));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        CommandAPI.onEnable(this);


        new RegisterCommands();

    }

}
