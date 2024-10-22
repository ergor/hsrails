package no.netb.mc.hsrails;

import org.bukkit.entity.Player;

public class DebugSubscription {
    private final Player player;
    private final boolean acceptsVerbose;

    public DebugSubscription(Player player, boolean acceptsVerbose) {
        this.player = player;
        this.acceptsVerbose = acceptsVerbose;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean getAcceptsVerbose() {
        return acceptsVerbose;
    }
}
