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
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;


public class MinecartListener implements Listener {

    /**
     * Default speed, in meters per tick. A tick is 0.05 seconds, thus 0.4 * 1/0.05 = 8 m/s
     */
    private static final double DEFAULT_SPEED_METERS_PER_TICK = 0.4d;
    private final Set<Integer> minecartBoosted = new HashSet<>();
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
            Location cartLocation = cart.getLocation();
            World cartsWorld = cart.getWorld();

            Block rail = cartsWorld.getBlockAt(cartLocation);

            if (rail.getType() == Material.POWERED_RAIL) {
                Block blockBelow = cartsWorld.getBlockAt(cartLocation.add(0, -1, 0));

                if (isCheatMode || blockBelow.getType() == boostBlock) {
                    if (!minecartBoosted.contains(event.getVehicle().getEntityId())) {
                        minecartBoosted.add(event.getVehicle().getEntityId());
                        cart.setMaxSpeed(DEFAULT_SPEED_METERS_PER_TICK * HsRails.getConfiguration().getSpeedMultiplier());
                    }
                } else {
                    if (cart.getMaxSpeed() != DEFAULT_SPEED_METERS_PER_TICK) {
                        minecartBoosted.remove(event.getVehicle().getEntityId());
                        cart.setMaxSpeed(DEFAULT_SPEED_METERS_PER_TICK);
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
        }
    }
}
