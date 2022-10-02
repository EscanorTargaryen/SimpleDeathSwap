package it.escanortargaryen.deathswap;

import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class Game implements Listener {

    private final Player player1, player2;

    private final BukkitTask timer;

    private final int time = 300;
    private int timeToSwap = 300;
    @Getter
    private boolean inGame = true;
    private final boolean daylight;

    public Game(@NotNull Player p1, @NotNull Player p2) {

        Bukkit.getPluginManager().registerEvents(this, MainClass.INSTANCE);

        daylight = p1.getWorld().getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);

        if (daylight) {
            p1.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "time set day");

        }

        player1 = p1;
        player2 = p2;
        player1.setGameMode(GameMode.SURVIVAL);
        player2.setGameMode(GameMode.SURVIVAL);

        player1.sendMessage(ChatColor.GOLD + "You started a game of DeathSwap with " + player2.getName());
        player2.sendMessage(ChatColor.GOLD + "You started a game of DeathSwap with " + player1.getName());

        player1.getInventory().clear();
        player2.getInventory().clear();
        player1.getInventory().setArmorContents(null);
        player2.getInventory().setArmorContents(null);
        player1.getInventory().setItemInMainHand(null);
        player2.getInventory().setItemInMainHand(null);
        player1.getInventory().setItemInOffHand(null);
        player2.getInventory().setItemInOffHand(null);

        timer = new BukkitRunnable() {

            @Override
            public void run() {

                int p1 = timeToSwap % 60;
                int p3 = (timeToSwap / 60) % 60;

                if (p1 < 10) {

                    player1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((p3 + ":0" + p1)));
                    player2.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((p3 + ":0" + p1)));

                } else {

                    player1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((p3 + ":" + p1)));
                    player2.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((p3 + ":" + p1)));
                }

                switch (timeToSwap) {

                    case 240: {
                        player1.sendMessage(ChatColor.GOLD + "You are now in Creative");
                        player2.sendMessage(ChatColor.GOLD + "You are now in Creative");
                        player1.setGameMode(GameMode.CREATIVE);
                        player2.setGameMode(GameMode.CREATIVE);

                        break;
                    }

                    case 230: {

                        player1.sendMessage(ChatColor.YELLOW + "You are now in Survival");
                        player2.sendMessage(ChatColor.YELLOW + "You are now in Survival");
                        player1.setGameMode(GameMode.SURVIVAL);
                        player2.setGameMode(GameMode.SURVIVAL);

                        break;
                    }
                    case 30: {

                        player1.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "30 seconds remain");
                        player2.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "30 seconds remain");
                        break;

                    }

                    case 0: {
                        timeToSwap = time;
                        Location l = player1.getLocation();
                        player1.teleport(player2);
                        player2.teleport(l);
                        player1.sendMessage(ChatColor.GREEN + "Swap!");
                        player2.sendMessage(ChatColor.GREEN + "Swap!");

                        break;
                    }

                    case 1: {

                        player1.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "1 second remain");
                        player2.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "1 second remain");

                        break;
                    }
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10: {
                        player1.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + timeToSwap + " seconds remain");
                        player2.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + timeToSwap + " seconds remain");

                        break;
                    }

                }

                timeToSwap--;
            }
        }.runTaskTimer(MainClass.INSTANCE, 0, 20);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player player = e.getPlayer();
        died(player);

    }

    public void stop() {

        player1.sendMessage(ChatColor.RED + "The Game has been stopped");
        player2.sendMessage(ChatColor.RED + "The Game has been stopped");

        inGame = false;
        timer.cancel();
        player1.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, daylight);

    }

    private void died(@NotNull Player player) {
        if (!isInGame()) return;
        if (timer != null) {

            timer.cancel();

        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (player.equals(player1)) {

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(ChatColor.GREEN + player2.getName() + " won.");

                    }

                } else {

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(ChatColor.GREEN + player1.getName() + " won.");

                    }
                }
            }
        }.runTaskLater(MainClass.INSTANCE, 10);

        inGame = false;

        player1.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, daylight);
        MainClass.GAMES.remove(this);

        HandlerList.unregisterAll(this);

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player player = e.getEntity();
        died(player);

    }

    public boolean containsPlayer(@NotNull Player p) {

        if (!isInGame()) {

            return false;

        }

        return p.equals(player1) || p.equals(player2);
    }

    public boolean isOwner(@NotNull Player p) {

        if (!isInGame()) {

            return false;

        }
        return p.equals(player1);
    }

}
