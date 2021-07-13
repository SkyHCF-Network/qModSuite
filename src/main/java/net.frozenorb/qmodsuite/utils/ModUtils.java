package net.frozenorb.qmodsuite.utils;

import com.cheatbreaker.api.CheatBreakerAPI;
import com.lunarclient.bukkitapi.LunarClientAPI;

import java.util.*;
import java.util.stream.Collectors;

import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.visibility.FrozenVisibilityHandler;
import net.frozenorb.qmodsuite.qModSuite;
import net.frozenorb.qmodsuite.event.ModModeEnterEvent;
import net.frozenorb.qmodsuite.event.ModModeExitEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class ModUtils {
   private static final Map<String, ItemStack[]> playerInventories = new HashMap();
   private static final Map<String, ItemStack[]> playerArmor = new HashMap();
   private static final Map<String, GameMode> playerGameModes = new HashMap();
   public static final Set<UUID> hideStaff = new HashSet();

   public static boolean isModMode(Player player) {
      return player.hasMetadata("modmode");
   }

   public static boolean isInvis(Player player) {
      return player.hasMetadata("invisible");
   }

   public static void enableModMode(Player player) {
      enableModMode(player, false);
   }

   public static void setModMode(boolean modMode, Player player) {
      if (modMode) {
         enableModMode(player);
      } else {
         disableModMode(player);
      }

   }

   public static void enableModMode(Player player, boolean silent) {
      if (Bukkit.getPluginManager().getPlugin("Practice") == null || !Bukkit.getPluginManager().getPlugin("Practice").isEnabled()) {
         if (!silent) {
            player.sendMessage(ChatColor.GOLD + "Mod Mode: " + ChatColor.GREEN + "Enabled");
         }

         player.setMetadata("modmode", new FixedMetadataValue(qModSuite.getInstance(), true));
         playerInventories.put(player.getName(), player.getInventory().getContents());
         playerArmor.put(player.getName(), player.getInventory().getArmorContents());
         playerGameModes.put(player.getName(), player.getGameMode());
         enableInvis(player);
         player.getInventory().clear();
         player.getInventory().setArmorContents((ItemStack[])null);
         if (player.hasPermission("basic.gamemode")) {
            player.setGameMode(GameMode.CREATIVE);
         } else {
            qModSuite.getInstance().getLogger().info("Setting " + player.getName() + " to fly mode!");
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.setFlying(true);
         }

         player.getInventory().setItem(0, StaffItems.COMPASS);
         player.getInventory().setItem(1, StaffItems.INSPECT_BOOK);
         if (player.hasPermission("worldedit.wand")) {
            player.getInventory().setItem(2, StaffItems.WAND);
            player.getInventory().setItem(3, StaffItems.CARPET);
         } else {
            player.getInventory().setItem(2, StaffItems.CARPET);
         }

         ItemStack onlineStaff = StaffItems.ONLINE_STAFF.clone();
         player.getInventory().setItem(6, StaffItems.LAST_PVP);
         player.getInventory().setItem(5, StaffItems.RANDOM_TELEPORT);
         player.getInventory().setItem(7, onlineStaff);
         player.getInventory().setItem(8, StaffItems.GO_VIS);
         player.updateInventory();
         Bukkit.getPluginManager().callEvent(new ModModeEnterEvent(player));
         CheatBreakerAPI.getInstance().giveAllStaffModules(player);
         LunarClientAPI.getInstance().giveAllStaffModules(player);
      }
   }

   public static void disableModMode(Player player) {
      if (Bukkit.getPluginManager().getPlugin("Practice") == null || !Bukkit.getPluginManager().getPlugin("Practice").isEnabled()) {
         player.sendMessage(ChatColor.GOLD + "Mod Mode: " + ChatColor.RED + "Disabled");
         player.removeMetadata("modmode", qModSuite.getInstance());
         disableInvis(player);
         player.getInventory().setContents((ItemStack[])playerInventories.remove(player.getName()));
         player.getInventory().setArmorContents((ItemStack[])playerArmor.remove(player.getName()));
         player.setGameMode((GameMode)playerGameModes.remove(player.getName()));
         if (player.getGameMode() != GameMode.CREATIVE) {
            player.setAllowFlight(false);
         }

         player.updateInventory();
         Bukkit.getPluginManager().callEvent(new ModModeExitEvent(player));
         CheatBreakerAPI.getInstance().disableAllStaffModules(player);
         LunarClientAPI.getInstance().disableAllStaffModules(player);
      }
   }

   public static void enableInvis(Player player) {
      if (Bukkit.getPluginManager().getPlugin("Practice") == null || !Bukkit.getPluginManager().getPlugin("Practice").isEnabled()) {
         player.setMetadata("invisible", new FixedMetadataValue(qModSuite.getInstance(), true));
         Iterator var1 = qModSuite.getInstance().getServer().getOnlinePlayers().iterator();

         while(true) {
            Player otherPlayer;
            do {
               if (!var1.hasNext()) {
                  FrozenNametagHandler.reloadPlayer(player);
                  player.spigot().setCollidesWithEntities(false);
                  player.getInventory().setItem(8, StaffItems.GO_VIS);
                  player.updateInventory();
                  player.spigot().setCollidesWithEntities(false);
                  return;
               }

               otherPlayer = (Player)var1.next();
            } while(qModSuite.getInstance().isShowOtherStaff() && otherPlayer.hasPermission("qmodsuite.Use") && !hideStaff.contains(otherPlayer.getUniqueId()));

            otherPlayer.hidePlayer(player);
         }
      }
   }

   public static void disableInvis(Player player) {
      if (Bukkit.getPluginManager().getPlugin("Practice") == null || !Bukkit.getPluginManager().getPlugin("Practice").isEnabled()) {
         player.removeMetadata("invisible", qModSuite.getInstance());
         FrozenVisibilityHandler.update(player);
         FrozenNametagHandler.reloadPlayer(player);
         player.spigot().setCollidesWithEntities(!isModMode(player));
         player.getInventory().setItem(8, StaffItems.GO_INVIS);
         player.updateInventory();
         player.spigot().setCollidesWithEntities(true);
      }
   }

   public static void showStaff(Player player) {
      if (Bukkit.getPluginManager().getPlugin("Practice") == null || !Bukkit.getPluginManager().getPlugin("Practice").isEnabled()) {
         hideStaff.remove(player.getUniqueId());
         FrozenVisibilityHandler.updateAllTo(player);
      }
   }

   public static void hideStaff(Player player) {
      if (Bukkit.getPluginManager().getPlugin("Practice") == null || !Bukkit.getPluginManager().getPlugin("Practice").isEnabled()) {
         hideStaff.add(player.getUniqueId());
         FrozenVisibilityHandler.updateAllTo(player);
      }
   }

   public static void randomTeleport(Player player) {
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
