package net.pwing.itemattributes.feature;

import net.pwing.itemattributes.feature.attribute.CustomAttributes;
import net.pwing.itemattributes.feature.entities.Entities;
import net.pwing.itemattributes.feature.items.Items;
import net.pwing.itemattributes.feature.placeholder.Placeholders;
import net.pwing.itemattributes.feature.spell.Spells;

public final class Features {
    private static boolean initialized = false;

    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        CustomAttributes.init();
        Entities.init();
        Items.init();
        Spells.init();
        Placeholders.init();
    }
}
