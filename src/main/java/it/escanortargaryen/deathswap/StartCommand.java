package it.escanortargaryen.deathswap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class StartCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@Nullable CommandSender commandSender, @Nullable Command command, @Nullable String s, @Nullable String[] strings) {

        if (!(commandSender instanceof Player)) {

            System.out.println("Only Players\nUsage: /deathswap <opponent>");

            return false;
        }
        Player pl = (Player) commandSender;

        if (strings.length == 1) {
            assert strings[0] != null;
            Player p = Bukkit.getPlayer(strings[0]);

            if (p != null) {

                if (p.equals(pl)) {

                    p.sendMessage(ChatColor.RED + "You can't play with yourself");

                    return false;

                }

                for (Game g : MainClass.GAMES) {
                    if (g.isInGame()) {

                        if (g.containsPlayer(pl)) {
                            pl.sendMessage(ChatColor.RED + "You are in other game");

                            return false;
                        }

                        if (g.containsPlayer(p)) {
                            pl.sendMessage(ChatColor.RED + p.getName() + " is in other game");

                            return false;
                        }
                    }

                }

                MainClass.GAMES.add(new Game(pl, p));

            } else {

                pl.sendMessage(ChatColor.RED + "Player must be online\nUsage: /deathswap <opponent>");

            }

            return true;
        } else {

            pl.sendMessage(ChatColor.RED + "Usage: /deathswap <opponent>");

            return false;
        }

    }

    @Override
    public @Nullable List<String> onTabComplete(@Nullable CommandSender commandSender, @Nullable Command command, @Nullable String s, @Nullable String[] strings) {

        if (strings.length == 1) {
            List<String> list = new LinkedList<>();
            for (Player i : Bukkit.getOnlinePlayers())

                list.add(i.getName());
            return list;
        }

        return null;
    }
}
