package it.escanortargaryen.deathswap;

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
    public void onEnable() {

        INSTANCE = this;
        @Nullable PluginCommand i = Bukkit.getPluginCommand("deathswap");
        if (i != null) {

            i.setExecutor(new StartCommand());

        }

        i = Bukkit.getPluginCommand("stopgame");
        if (i != null) {

            i.setExecutor(new DisbandGameCommand());

        }

    }
}
