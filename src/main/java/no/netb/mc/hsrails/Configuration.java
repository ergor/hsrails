package no.netb.mc.hsrails;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class Configuration {

    private Material boostBlock;
    private Material hardBrakeBlock;
    private double speedMultiplier;
    private double hardBrakeMultiplier;
    private boolean isCheatMode;


    public Material getBoostBlock() {
        return boostBlock;
    }

    public Material getHardBrakeBlock() {
        return hardBrakeBlock;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double getHardBrakeMultiplier() {
        return hardBrakeMultiplier;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public void setHardBrakeMultiplier(double hardBrakeMultiplier) {
        this.hardBrakeMultiplier = hardBrakeMultiplier;
    }

    public boolean isCheatMode() {
        return isCheatMode;
    }

    public void readConfig(FileConfiguration fileConfig, Logger logger) {
        readBoostBlock(fileConfig, logger);
        readHardBrakeBlock(fileConfig, logger);
        readSpeedMultiplier(fileConfig, logger);
        readHardBrakeMultiplier(fileConfig, logger);
    }

    private void readBoostBlock(FileConfiguration fileConfig, Logger logger) {
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

    private void readHardBrakeBlock(FileConfiguration fileConfig, Logger logger) {
        String hardBrakeBlockKey = fileConfig.getString("hardBrakeBlock");
        if (hardBrakeBlockKey != null) {
            hardBrakeBlock = Material.matchMaterial(hardBrakeBlockKey);
        }
        if (hardBrakeBlock == null) {
            logger.warning("Warning: option 'hardBrakeBlock' was not specified or invalid value was given. Hard braking disabled.");
        }
    }

    private void readSpeedMultiplier(FileConfiguration fileConfig, Logger logger) {
        double speedMultiplier = fileConfig.getDouble("speedMultiplier");
        if (speedMultiplier < 1d) {
            logger.warning("Warning: speed multiplier set below 1 in config. Using value of 1 as fallback.");
            speedMultiplier = 1d;
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

    private void readHardBrakeMultiplier(FileConfiguration fileConfig, Logger logger) {
        double hardBrakeMultiplier = fileConfig.getDouble("hardBrakeMultiplier");
        if (hardBrakeMultiplier < 1.0) {
            logger.warning("Warning: brake multiplier not set or set to below 1 in config. Using value of 8 as fallback.");
            hardBrakeMultiplier = 8.0;
        }
        else {
            logger.info("Setting brake multiplier to " + hardBrakeMultiplier);
        }

        this.hardBrakeMultiplier = 1.0 / hardBrakeMultiplier;
    }
}
