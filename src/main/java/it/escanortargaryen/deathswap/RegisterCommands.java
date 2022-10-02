package it.escanortargaryen.deathswap;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Iterator;

/**
 * Class to manage all the commands
 */
public class RegisterCommands {

    public RegisterCommands() {

        CommandAPICommand start = new CommandAPICommand("start").withArguments(new PlayerArgument("opponent"))
                .executesPlayer((sender, args) -> {

                    Player owner = sender;

                    Player target = (Player) args[0];

                    if (target.equals(owner)) {

                        target.sendMessage(ChatColor.RED + "You can't play with yourself");

                        return;

                    }

                    for (Game g : MainClass.GAMES) {
                        if (g.isInGame()) {

                            if (g.containsPlayer(owner)) {
                                owner.sendMessage(ChatColor.RED + "You are in other game");

                                return;
                            }

                            if (g.containsPlayer(target)) {
                                owner.sendMessage(ChatColor.RED + target.getName() + " is in other game");

                                return;
                            }
                        }

                        MainClass.GAMES.add(new Game(owner, target));

                    }

                }).withPermission("deathswap.gamestart");

        CommandAPICommand disband = new CommandAPICommand("stopgame")
                .executesPlayer((sender, args) -> {

                    Player p = sender;

                    Iterator<Game> game = MainClass.GAMES.iterator();

                    while (game.hasNext()) {

                        Game g = game.next();
                        if (g.containsPlayer(p)) {

                            if (g.isOwner(p)) {

                                g.stop();
                                game.remove();
                                return;
                            } else {

                                p.sendMessage(ChatColor.RED + "You are not the owner");
                                return;

                            }

                        }

                    }
                    p.sendMessage(ChatColor.RED + "You are not in a game");

                }).withPermission("deathswap.stopgame");

        new CommandAPICommand("deathswap").withSubcommands(start, disband)
                .executesPlayer((sender, args) -> {
                    sender.sendMessage("Usage: /deathswap start <player>");
                }).withPermission("deathswap")
                .register();

    }

}
