package it.escanortargaryen.deathswap;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class MainClass extends JavaPlugin {

    public static MainClass INSTANCE;
    public static List<Game> GAMES = new LinkedList<>();

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(true));


    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable(this);
        INSTANCE = this;

        new RegisterCommands();

    }

}
