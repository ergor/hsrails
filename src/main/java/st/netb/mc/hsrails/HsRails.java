package st.netb.mc.hsrails;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class HsRails extends JavaPlugin {

    private static double speed_multiplier;

    public static double getMultiplier() {
        return speed_multiplier;
    }

    private static Set<CommandSender> receivedHeadsUp = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig(); // copies default file to data folder, will not override existing file
        Logger logger = getLogger();
        logger.info("Reading config");

        speed_multiplier = getConfig().getDouble("speedMultiplier");
        if (speed_multiplier <= 0) {
            logger.warning("Warning: speed multiplier set to 0 or below in config. Using value of 0.1 as fallback.");
            speed_multiplier = 0.1;
        } else if (speed_multiplier > 8) {
            logger.warning("Warning: speed multiplier set above 8 in config. Using value of 8 as fallback.");
            speed_multiplier = 8d;
        }

        if (speed_multiplier > 4) {
            logger.info("Note: speed multiplier is set above 4. Typically, due server limitations you may not see an increase in speed greater than 4x,"
                    + " however the carts will have more momentum. This means they will coast for longer even though the max speed is seemingly 4x.");
        }

        logger.info("Registering event listener");
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new MinecartListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("unloading...");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("hsrails")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("hsrails.cmd")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                    return true;
                }
            }

            try {
                speed_multiplier = Double.parseDouble(args[0]);
            }
            catch (Exception ignore) {
                sender.sendMessage(ChatColor.RED + "multiplier should be a number");
                return false;
            }

            if (speed_multiplier > 0 && speed_multiplier <= 8) {
                String message = ChatColor.AQUA + "Speed multiplier set to: " + speed_multiplier;
                String headsUp =
                        ChatColor.YELLOW + "\nNote: multiplier set to more than 4x. Servers often struggle to provide max speeds above 4x,"
                        + " and the carts may appear to be capped at 4x. However, carts will still have their momentum increased,"
                        + " meaning they will coast for longer.";

                boolean sendHeadsUp = !receivedHeadsUp.contains(sender) && speed_multiplier > 4;
                sender.sendMessage(String.format("%s%s", message, sendHeadsUp ? headsUp : ""));
                if (sendHeadsUp) {
                    receivedHeadsUp.add(sender);
                }
                return true;
            }

            sender.sendMessage(ChatColor.RED + "multiplier must be greater than 0 and max 8");
            return true;
        }

        return false;
    }
}
