package no.netb.mc.hsrails;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class Configuration {

    private double speedMultiplier;
    private Material boostBlock;
    private boolean isCheatMode;

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public Material getBoostBlock() {
        return boostBlock;
    }

    public boolean isCheatMode() {
        return isCheatMode;
    }

    public void readConfig(FileConfiguration fileConfig, Logger logger) {
        { // Speed multiplier
            double speedMultiplier = fileConfig.getDouble("speedMultiplier");
            if (speedMultiplier <= 0) {
                logger.warning("Warning: speed multiplier set to 0 or below in config. Using value of 0.1 as fallback.");
                speedMultiplier = 0.1;
            } else if (speedMultiplier > 8) {
                logger.warning("Warning: speed multiplier set above 8 in config. Using value of 8 as fallback.");
                speedMultiplier = 8d;
            } else {
                logger.info("Setting speed multiplier to " + speedMultiplier);
            }

            if (speedMultiplier > 4) {
                logger.info("Note: speed multiplier is set above 4. Typically, due to server limitations you may not see an increase in speed greater than 4x,"
                        + " however the carts will have more momentum. This means they will coast for longer even though the max speed is seemingly 4x.");
            }
            this.speedMultiplier = speedMultiplier;
        }

        { // Boost block
            String boostBlockKey = fileConfig.getString("boostBlock");
            if (boostBlockKey != null) {
                if (boostBlockKey.equalsIgnoreCase("any")) {
                    isCheatMode = true;
                } else {
                    boostBlock = Material.matchMaterial(boostBlockKey);
                }
            }
            if (boostBlock == null && !isCheatMode) {
                Material fallbackMat = Material.REDSTONE_BLOCK;
                logger.warning(String.format("Warning: option 'boostBlock' was '%s' in config which is an illegal value. Falling back to using '%s'",
                        boostBlockKey == null ? "(undefined)" : boostBlockKey,
                        fallbackMat.getKey()));
                boostBlock = fallbackMat;
            }
            if (isCheatMode) {
                logger.info("Boost block was set to 'any'. Every powered rail is now a high speed rail.");
            } else {
                logger.info(String.format("Setting boost block to '%s'", boostBlock.getKey()));
            }
        }
    }
}
