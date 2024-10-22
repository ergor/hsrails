package no.netb.mc.hsrails;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MinecartListener implements Listener {

    static class MinecartState {
        int blocksCoasted = 0;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MinecartState) {
                MinecartState s = (MinecartState) obj;
                return s.blocksCoasted == this.blocksCoasted;
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return Objects.hash(blocksCoasted);
        }
    }

    /**
     * Default speed, in meters per tick. A tick is 0.05 seconds, thus 0.4 * 1/0.05 = 8 m/s
     */
    private static final double DEFAULT_SPEED_METERS_PER_TICK = 0.4d;
    private final Map<Integer, MinecartState> boostedMinecarts = new HashMap<>();
    private final Material boostBlock;
    private final Material hardBrakeBlock;
    private final boolean isCheatMode;

    public MinecartListener(Material boostBlock, Material hardBrakeBlock, boolean isCheatMode) {
        this.boostBlock = boostBlock;
        this.hardBrakeBlock = hardBrakeBlock;
        this.isCheatMode = isCheatMode;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleMove(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Minecart) {
            Minecart cart = (Minecart) event.getVehicle();
            final boolean isEnteredNewBlock = !event.getTo().getBlock().equals(event.getFrom().getBlock());
            if (!HsRails.debuggers.isEmpty() && isEnteredNewBlock) {
                Vector v = cart.getVelocity();
                log(true, "velocity: [%f %f] |%f|", v.getX(), v.getZ(), v.length());
            }

            final Integer entityId = event.getVehicle().getEntityId();
            final Location cartLocation = cart.getLocation();
            final World cartsWorld = cart.getWorld();

            final Block rail = cartsWorld.getBlockAt(cartLocation);
            final double speedMultiplier = HsRails.getConfiguration().getSpeedMultiplier();
            final double boostedMaxSpeed = DEFAULT_SPEED_METERS_PER_TICK * speedMultiplier;

            if (rail.getType() == Material.POWERED_RAIL) {
                Block blockBelow = cartsWorld.getBlockAt(cartLocation.add(0, -1, 0));

                if (isCheatMode || blockBelow.getType() == boostBlock) {
                    if (!boostedMinecarts.containsKey(entityId)) { // if cart is not in high speed state then make it high speed
                        boostedMinecarts.put(entityId, new MinecartState());
                        cart.setMaxSpeed(boostedMaxSpeed);
                        log(false, "minecart [%d] added", entityId);
                    } else { // the cart is already in high speed state; refresh the values since we are on a boost block.
                        boostedMinecarts.get(entityId).blocksCoasted = 0;
                        if (cart.getMaxSpeed() != boostedMaxSpeed) {
                            cart.setMaxSpeed(boostedMaxSpeed);
                            log(false, "minecart [%d] max speed refreshed", entityId);
                        }
                    }
                } else {
                    // carts should NOT be in high speed state when passing over regular power rails; clear it.
                    if (cart.getMaxSpeed() != DEFAULT_SPEED_METERS_PER_TICK) {
                        boostedMinecarts.remove(entityId);
                        cart.setMaxSpeed(DEFAULT_SPEED_METERS_PER_TICK);
                        log(false, "minecart [%d] evicted", entityId);
                    }
                }

                RedstoneRail railBlockData = (RedstoneRail) rail.getBlockData();
                if (!railBlockData.isPowered()
                        && blockBelow.getType() == hardBrakeBlock) {
                    Vector cartVelocity = cart.getVelocity();
                    cartVelocity.multiply(HsRails.getConfiguration().getHardBrakeMultiplier());
                    cart.setVelocity(cartVelocity);
                }
            }
            // This handles the infinite momentum bug as reported by GitHub issue #6.
            else if (isEnteredNewBlock) {
                MinecartState state = boostedMinecarts.get(entityId);
                switch (rail.getType()) {
                    case RAIL:
                    case ACTIVATOR_RAIL:
                    case DETECTOR_RAIL:
                        if (state != null) { // state != null means the cart is in the high speed state.
                            if (cart.getVelocity().length() < DEFAULT_SPEED_METERS_PER_TICK) {
                                cart.setMaxSpeed(DEFAULT_SPEED_METERS_PER_TICK);
                                boostedMinecarts.remove(entityId);
                                log(false, "momentum: too slow, clearing boost state");
                                break;
                            }
                            state.blocksCoasted++;
                            if (state.blocksCoasted > HsRails.coastFactor) {
                                double factor = Math.max(1d, speedMultiplier - ((state.blocksCoasted - HsRails.coastFactor) / HsRails.coastFactor));
                                cart.setMaxSpeed(DEFAULT_SPEED_METERS_PER_TICK * factor);
                                if (factor == 1) {
                                    boostedMinecarts.remove(entityId);
                                    log(false, "momentum: factor reached 1, clearing boost state");
                                    break;
                                }
                                log(false, "momentum: adjusting speed factor: %f", factor);
                            }
                        }
                        break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleDestroyed(VehicleDestroyEvent event) {
        Integer vehicleId = event.getVehicle().getEntityId();
        boolean wasPresent = boostedMinecarts.remove(vehicleId) != null;
        if (wasPresent) {
            log(false, "minecart [%d] evicted", vehicleId);
        } else {
            log(false, "minecart [%d] was already cleared", vehicleId);
        }
    }

    private void log(boolean verbose, String template, Object... args) {
        for (DebugSubscription subscriber : HsRails.debuggers.values()) {
            if (!verbose || subscriber.getAcceptsVerbose()) {
                subscriber.getPlayer().sendMessage(String.format(template, args));
            }
        }
    }
}
