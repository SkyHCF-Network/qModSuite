package net.frozenorb.qmodsuite.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qmodsuite.qModSuite;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleStaffCommand {
   @Command(
      names = {"togglestaff"},
      permission = "qmodsuite.ToggleStaff"
   )
   public static void toggleStaff(Player player) {
      if (!qModSuite.getInstance().getSilencedStaffMembers().contains(player.getUniqueId())) {
         qModSuite.getInstance().getSilencedStaffMembers().add(player.getUniqueId());
         player.sendMessage(ChatColor.RED + "You can no longer see reports and requests.");
      } else {
         qModSuite.getInstance().getSilencedStaffMembers().remove(player.getUniqueId());
         player.sendMessage(ChatColor.GREEN + "You can now see reports and requests.");
      }

   }
}
