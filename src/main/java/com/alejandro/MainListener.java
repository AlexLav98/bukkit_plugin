package com.alejandro;

import com.google.common.collect.BiMap;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class MainListener implements Listener {

    MainListener(BiMap<Long, OfflinePlayer> linkedAccountsMap, JDA jda, TheBestPlugin plugin) {
        this.linkedAccountsMap = linkedAccountsMap;
        this.jda = jda;
        this.plugin = plugin;

        mainListenerWrapper = new MainListenerWrapper(jda, plugin);
        inGameChannelIdLong = plugin.inGameChannelIdLong();
    }

    private BiMap<Long, OfflinePlayer> linkedAccountsMap;
    private JDA jda;
    private MainListenerWrapper mainListenerWrapper;
    private TheBestPlugin plugin;

    private long inGameChannelIdLong;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        Long   authorUserIdLong       = linkedAccountsMap.inverse().get( event.getPlayer() );
        String authorDiscordUsername  = jda.getUserById( authorUserIdLong ).getName();
        String messageContent         = event.getMessage();
        TextChannel inGameTextChannel = jda.getTextChannelById(plugin.inGameChannelIdLong());

        inGameTextChannel.sendMessage(String.format("**%s**: %s", authorDiscordUsername, messageContent)).queue();
    }

    /**
     * When a player logs on, print their join message on Discord
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // getJoinMessage() can return null. Also, make sure to filter out
        // weird minecraft unicode
        String formattedJoinMessage = event.getJoinMessage() == null ?
                "[JOIN MESSAGE ERROR]" : event.getJoinMessage().replace("\u00A7e", "");

        jda.getTextChannelById(inGameChannelIdLong)
                .sendMessage("**" + formattedJoinMessage + "**").queue();
    }

    /**
     * When a player quits, print their leave message on Discord
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

        /*
         * Remove unicode. Explained above
         */
        String formattedQuitMessage = event.getQuitMessage().replace("\u00A7e", "");

        jda.getTextChannelById(inGameChannelIdLong)
                .sendMessage("**" + formattedQuitMessage + "**").queue();
    }

    /**
     * When a player dies, print their death in Discord
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        jda.getTextChannelById(inGameChannelIdLong)
                .sendMessage("**" + event.getDeathMessage() + "**").queue();
    }
}