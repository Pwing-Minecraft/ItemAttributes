package net.pwing.itemattributes.feature;

import org.bukkit.event.Listener;

/**
 * Represents a feature instance.
 */
public interface FeatureInstance {

    /**
     * Returns if the feature is enabled.
     *
     * @return if the feature is enabled
     */
    boolean isEnabled();

    /**
     * Creates a listener for this feature.
     *
     * @return the created listener
     */
    default Listener createListener() {
        return null;
    }
}
