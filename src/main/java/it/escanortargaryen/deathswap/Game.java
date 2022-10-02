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

/**
 * A class representing a game
 */
public class Game implements Listener {

    /**
     * The owner of the game
     */
    private final Player owner;

    /**
     * The second player of the game
     */
    private final Player player2;

    /**
     * The BukkitTask of the game
     */
    private final BukkitTask timer;

    /**
     * The time of a round
     */
    private final int time = 300;

    /**
     * Time remains before the player swap
     */
    private int timeToSwap = 300;

    /**
     * Whether the game is currently active
     */
    @Getter
    private boolean inGame = true;

    /**
     * Preserves the value of DO_DAYLIGHT_CYCLE.
     * During the game it will be set as day and gamerule to false.
     * At the end of the game the value will be reset to.
     */
    private final boolean daylight_cicle;

    /**
     * Create a new Game
     *
     * @param owner The owner of the game
     * @param p2 The second player of the game
     */
    public Game(@NotNull Player owner, @NotNull Player p2) {

        Bukkit.getPluginManager().registerEvents(this, MainClass.INSTANCE);

        daylight_cicle = owner.getWorld().getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);

        if (daylight_cicle) {
            owner.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "time set day");

        }

        this.owner = owner;
        player2 = p2;
        this.owner.setGameMode(GameMode.SURVIVAL);
        player2.setGameMode(GameMode.SURVIVAL);

        this.owner.sendMessage(ChatColor.GOLD + "You started a game of DeathSwap with " + player2.getName());
        player2.sendMessage(ChatColor.GOLD + "You started a game of DeathSwap with " + this.owner.getName());

        this.owner.getInventory().clear();
        player2.getInventory().clear();
        this.owner.getInventory().setArmorContents(null);
        player2.getInventory().setArmorContents(null);
        this.owner.getInventory().setItemInMainHand(null);
        player2.getInventory().setItemInMainHand(null);
        this.owner.getInventory().setItemInOffHand(null);
        player2.getInventory().setItemInOffHand(null);

        timer = new BukkitRunnable() {

            @Override
            public void run() {

                int p1 = timeToSwap % 60;
                int p3 = (timeToSwap / 60) % 60;

                if (p1 < 10) {

                    Game.this.owner.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((p3 + ":0" + p1)));
                    player2.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((p3 + ":0" + p1)));

                } else {

                    Game.this.owner.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((p3 + ":" + p1)));
                    player2.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((p3 + ":" + p1)));
                }

                switch (timeToSwap) {

                    case 240: {
                        Game.this.owner.sendMessage(ChatColor.GOLD + "You are now in Creative");
                        player2.sendMessage(ChatColor.GOLD + "You are now in Creative");
                        Game.this.owner.setGameMode(GameMode.CREATIVE);
                        player2.setGameMode(GameMode.CREATIVE);

                        break;
                    }

                    case 230: {

                        Game.this.owner.sendMessage(ChatColor.YELLOW + "You are now in Survival");
                        player2.sendMessage(ChatColor.YELLOW + "You are now in Survival");
                        Game.this.owner.setGameMode(GameMode.SURVIVAL);
                        player2.setGameMode(GameMode.SURVIVAL);

                        break;
                    }
                    case 30: {

                        Game.this.owner.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "30 seconds remain");
                        player2.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "30 seconds remain");
                        break;

                    }

                    case 0: {
                        timeToSwap = time;
                        Location l = Game.this.owner.getLocation();
                        Game.this.owner.teleport(player2);
                        player2.teleport(l);
                        Game.this.owner.sendMessage(ChatColor.GREEN + "Swap!");
                        player2.sendMessage(ChatColor.GREEN + "Swap!");

                        break;
                    }

                    case 1: {

                        Game.this.owner.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "1 second remain");
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
                        Game.this.owner.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + timeToSwap + " seconds remain");
                        player2.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + timeToSwap + " seconds remain");

                        break;
                    }

                }

                timeToSwap--;
            }
        }.runTaskTimer(MainClass.INSTANCE, 0, 20);

    }

    /**
     * {@inheritDoc}
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player player = e.getPlayer();
        died(player);

    }

    /**
     * Stop this game
     */
    public void stop() {

        owner.sendMessage(ChatColor.RED + "The Game has been stopped");
        player2.sendMessage(ChatColor.RED + "The Game has been stopped");

        inGame = false;
        timer.cancel();
        owner.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, daylight_cicle);

    }

    /**
     * Calls when a player died.
     * The other player will win the game.
     *
     * @param player The player who died
     */
    private void died(@NotNull Player player) {
        if (!isInGame()) return;
        if (timer != null) {

            timer.cancel();

        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (player.equals(owner)) {

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(ChatColor.GREEN + player2.getName() + " won.");

                    }

                } else {

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(ChatColor.GREEN + owner.getName() + " won.");

                    }
                }
            }
        }.runTaskLater(MainClass.INSTANCE, 10);

        inGame = false;

        owner.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, daylight_cicle);
        MainClass.GAMES.remove(this);

        HandlerList.unregisterAll(this);

    }

    /**
     * {@inheritDoc}
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player player = e.getEntity();
        died(player);

    }

    /**
     * Returns if this game contains a player
     *
     * @param p A player
     * @return Returns if this game contains a player
     */
    public boolean containsPlayer(@NotNull Player p) {

        if (!isInGame()) {

            return false;

        }

        return p.equals(owner) || p.equals(player2);
    }

    /**
     * Returns if a player is the owner of this game
     *
     * @param p A player
     * @return Returns if a player is the owner of this game
     */
    public boolean isOwner(@NotNull Player p) {

        if (!isInGame()) {

            return false;

        }
        return p.equals(owner);
    }

}
