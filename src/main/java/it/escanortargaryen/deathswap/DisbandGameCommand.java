package it.escanortargaryen.deathswap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class DisbandGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@Nullable CommandSender commandSender, @Nullable Command command, @Nullable String s, @Nullable String[] strings) {

        if (!(commandSender instanceof Player)) {

            System.out.println("Only Players\nUsage: /deathswap <opponent>");

            return false;
        }

        Player p = (Player) commandSender;

        Iterator<Game> game = MainClass.GAMES.iterator();

        while (game.hasNext()) {

            Game g = game.next();
            if (g.containsPlayer(p)) {

                if (g.isOwner(p)) {

                    g.stop();
                    game.remove();
                    return true;
                } else {

                    p.sendMessage(ChatColor.RED + "You are not the owner");
                    return false;

                }

            }

        }
        p.sendMessage(ChatColor.RED + "You are not in a game");
        return false;

    }

}
