package st.netb.mc.hsrails;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HsRails extends JavaPlugin {

    private double speed_multiplier = 1.0d;

    @Override
    public void onEnable() {
        getLogger().info("Registering event listener");
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
                sender.sendMessage(ChatColor.RED + "hsrails: multiplier should be a number");
                return false;
            }

            if (speed_multiplier > 0 && speed_multiplier <= 50) {
                sender.sendMessage(ChatColor.AQUA + "hsrails: speed multiplier set to: " + speed_multiplier);
                return true;
            }

            sender.sendMessage(ChatColor.RED + "hsrails: multiplier must be greater than 0 and max 50");
        }

        return false;
    }
}
