package net.frozenorb.qmodsuite.listeners;

import net.frozenorb.qmodsuite.utils.StaffItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author UberDevo
 * Copyright @ UberDevo | All rights reserved
 * 5:55 pm on the 13.07.2021
 */
public class RandomTeleportListener implements Listener {

    @EventHandler
    public void onJackInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
            if (StaffItems.RANDOM_TELEPORT.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "Random Teleport")) {
                List<Player> players = Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(user -> !user.hasPermission("basic.staff"))
                        .collect(Collectors.toList());
                Random random = new Random();
                try {
                    int rand = random.nextInt(players.size());
                    Player playerTP = players.get(rand);
                    player.teleport(playerTP);
                    player.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.YELLOW + playerTP.getName());
                } catch (IllegalArgumentException exception) {
                    player.sendMessage(ChatColor.RED + "No avaliable players to teleport to.");
                }
            }
       }
   }
