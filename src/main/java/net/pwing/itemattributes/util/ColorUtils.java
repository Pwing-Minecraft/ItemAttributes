package net.pwing.itemattributes.util;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public final class ColorUtils {
    private static final Map<Color, Material> COLOR_TO_MATERIAL = new HashMap<Color, Material>() {
        {
            put(Color.WHITE, Material.WHITE_CONCRETE);
            put(Color.SILVER, Material.LIGHT_GRAY_TERRACOTTA);
            put(Color.GRAY, Material.GRAY_TERRACOTTA);
            put(Color.BLACK, Material.BLACK_CONCRETE);
            put(Color.RED, Material.RED_TERRACOTTA);
            put(Color.MAROON, Material.MAGENTA_TERRACOTTA);
            put(Color.YELLOW, Material.YELLOW_TERRACOTTA);
            put(Color.OLIVE, Material.GREEN_TERRACOTTA);
            put(Color.LIME, Material.LIME_TERRACOTTA);
            put(Color.GREEN, Material.GREEN_TERRACOTTA);
            put(Color.AQUA, Material.CYAN_CONCRETE);
            put(Color.TEAL, Material.CYAN_CONCRETE);
            put(Color.BLUE, Material.BLUE_TERRACOTTA);
            put(Color.NAVY, Material.CYAN_TERRACOTTA);
            put(Color.FUCHSIA, Material.PINK_TERRACOTTA);
            put(Color.PURPLE, Material.PURPLE_TERRACOTTA);
            put(Color.ORANGE, Material.ORANGE_CONCRETE);

            // TextColors
            putText(NamedTextColor.DARK_RED, Material.RED_CONCRETE);
            putText(NamedTextColor.RED, Material.RED_TERRACOTTA);
            putText(NamedTextColor.DARK_AQUA, Material.CYAN_TERRACOTTA);
            putText(NamedTextColor.GOLD, Material.ORANGE_TERRACOTTA);
            putText(NamedTextColor.LIGHT_PURPLE, Material.PINK_CONCRETE);
        }

        public Material putText(NamedTextColor color, Material value) {
            return super.put(Color.fromRGB(color.value()), value);
        }
    };

    public static void init() {
        // no-op
    }

    public static Material getClosestMaterial(Color target) {
        Material result = COLOR_TO_MATERIAL.get(target);
        if (result != null) {
            return result;
        }

        Material closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (Map.Entry<Color, Material> entry : COLOR_TO_MATERIAL.entrySet()) {
            double distance = getColorDistance(target, entry.getKey());
            if (distance < closestDistance) {
                closest = entry.getValue();
                closestDistance = distance;
            }
        }

        if (closest == null) {
            throw new IllegalStateException("No closest material found for color: " + target);
        }

        // Store the closest material for the target color
        COLOR_TO_MATERIAL.put(target, closest);
        return closest;
    }

    private static double getColorDistance(Color c1, Color c2) {
        int rDiff = c1.getRed() - c2.getRed();
        int gDiff = c1.getGreen() - c2.getGreen();
        int bDiff = c1.getBlue() - c2.getBlue();
        return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
    }
}
