package st.netb.mc.hsrails;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;


public class MinecartListener implements Listener {

    private static final double BUKKIT_SPEED_MULTIPLIER = 0.4d;

    private final Material boostBlock;

    public MinecartListener(Material boostBlock) {
        this.boostBlock = boostBlock;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleMove(VehicleMoveEvent event) {

        if (event.getVehicle() instanceof Minecart) {
            Minecart cart = (Minecart) event.getVehicle();
            Location cartLocation = cart.getLocation();
            World cartsWorld = cart.getWorld();

            Block rail = cartsWorld.getBlockAt(cartLocation);
            Block blockBelow = cartsWorld.getBlockAt(cartLocation.add(0, -1, 0));

            if (rail.getType() == Material.POWERED_RAIL) {
                if (blockBelow.getType() == boostBlock) {
                    cart.setMaxSpeed(BUKKIT_SPEED_MULTIPLIER * HsRails.getConfiguration().getSpeedMultiplier());
                }
                else {
                    cart.setMaxSpeed(BUKKIT_SPEED_MULTIPLIER);
                }
            }
        }
    }
}
