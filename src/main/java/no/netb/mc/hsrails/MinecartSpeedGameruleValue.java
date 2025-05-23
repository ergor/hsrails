package no.netb.mc.hsrails;

import org.bukkit.GameRule;
import org.bukkit.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Checks if the gamerule minecartMaxSpeed exists, using it if it does and using a default value otherwise
 */
public class MinecartSpeedGameruleValue {
    private static final double DEFAULT_SPEED_METERS_PER_TICK = 0.4d;
    private static final int DEFAULT_SPEED_METERS_PER_SECOND = 8;
    private static final String GAME_RULE_NAME = "minecartMaxSpeed";
    private final GameRule<Integer> gameRule;
    private final Map<String, Boolean> worldHasGameRule = new HashMap<>();

    @SuppressWarnings("unchecked")
    public MinecartSpeedGameruleValue() {
        gameRule = (GameRule<Integer>) GameRule.getByName(GAME_RULE_NAME);
    }

    public double obtain(World world) {
        if (gameRule == null)
            return DEFAULT_SPEED_METERS_PER_TICK;
        else if (!worldHasGameRule.containsKey(world.getName())) {
            boolean hasGameRule = Arrays.asList(world.getGameRules()).contains(GAME_RULE_NAME);
            worldHasGameRule.put(world.getName(), hasGameRule);
            return obtain(world);
        } else if (worldHasGameRule.get(world.getName()))
            return Objects.requireNonNullElse(world.getGameRuleValue(gameRule), DEFAULT_SPEED_METERS_PER_SECOND) / 20.0d;
        else
            return DEFAULT_SPEED_METERS_PER_TICK;
    }
}
