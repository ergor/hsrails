package no.netb.mc.hsrails;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class HsRails extends JavaPlugin {

    private static final Configuration CONFIGURATION = new Configuration();

    public static Configuration getConfiguration() {
        return CONFIGURATION;
    }

    private static final Set<CommandSender> receivedHeadsUp = new HashSet<>();

    public static Map<Player, DebugSubscription> debuggers = new HashMap<>();
    public static double coastFactor = 30d;

    @Override
    public void onEnable() {
        saveDefaultConfig(); // copies default file to data folder, will not override existing file
        Logger logger = getLogger();

        logger.info("Reading config");
        CONFIGURATION.readConfig(getConfig(), logger);

        logger.info("Registering event listener");
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new MinecartListener(
                CONFIGURATION.getBoostBlock(),
                CONFIGURATION.getHardBrakeBlock(),
                CONFIGURATION.isCheatMode()
        ), this);
        pm.registerEvents(new PlayerDisconnectListener(logger), this);
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

                if (args.length > 0 && (args[0].equalsIgnoreCase("d") || args[0].equalsIgnoreCase("dd"))) {
                    Boolean currentMode = Optional.ofNullable(debuggers.get(player))
                            .map(DebugSubscription::getAcceptsVerbose)
                            .orElse(null);

                    boolean verbose = args[0].equalsIgnoreCase("dd");
                    if (currentMode == null || currentMode != verbose) {
                        debuggers.put(player, new DebugSubscription(player, verbose));
                        player.sendMessage("debug mode: " + (verbose ? "ON [VERBOSE]" : "ON [NORMAL]"));
                        return true;
                    }

                    debuggers.remove(player);
                    player.sendMessage("debug mode: OFF");
                    return true;
                }
            }

            try {
                CONFIGURATION.setSpeedMultiplier(Double.parseDouble(args[0]));
            }
            catch (Exception ignore) {
                sender.sendMessage(ChatColor.RED + "multiplier should be a number");
                return false;
            }

            // TODO: in the future, if the new minecart physics gets accepted, then the speed multiplier glitch will be fixed. then keep this only for compatibility
            double speedMultiplier = CONFIGURATION.getSpeedMultiplier();
            if (speedMultiplier >= 1d && speedMultiplier <= 8d) {
                String message = ChatColor.AQUA + "Speed multiplier set to: " + speedMultiplier;
                String headsUp =
                        ChatColor.YELLOW + "\nNote: multiplier set to more than 4x. Servers often struggle to provide max speeds above 4x,"
                        + " and the carts may appear to be capped at 4x. However, carts will still have their momentum increased,"
                        + " meaning they will coast for longer.";

                boolean sendHeadsUp = !receivedHeadsUp.contains(sender) && speedMultiplier > 4;
                sender.sendMessage(String.format("%s%s", message, sendHeadsUp ? headsUp : ""));
                if (sendHeadsUp) {
                    receivedHeadsUp.add(sender);
                }
                return true;
            }

            sender.sendMessage(ChatColor.RED + "multiplier must be at least 1 and at most 8");
            return true;
        }

        return false;
    }
}
