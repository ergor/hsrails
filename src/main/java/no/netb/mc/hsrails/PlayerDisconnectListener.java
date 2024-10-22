package no.netb.mc.hsrails;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public class PlayerDisconnectListener implements Listener {

    private final Logger logger;

    public PlayerDisconnectListener(Logger logger) {
        this.logger = logger;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        boolean cleared = HsRails.debuggers.remove(event.getPlayer()) != null;
        if (cleared) {
            logger.info(String.format("removed %s from debuggers list", event.getPlayer().getDisplayName()));
        }
    }
}
