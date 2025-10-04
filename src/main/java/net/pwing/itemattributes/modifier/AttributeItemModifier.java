package net.pwing.itemattributes.modifier;

import me.redned.config.ConfigOption;
import me.redned.config.PostProcessable;
import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.AttributeCalculator;
import net.pwing.itemattributes.attribute.ItemAttribute;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.util.ExpressionUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.function.UnaryOperator;

public class AttributeItemModifier implements ItemModifier, PostProcessable {

    @ConfigOption(name = "key", description = "The key of the attribute to require.", required = true)
    private NamespacedKey key;

    @ConfigOption(name = "value", description = "The value of the attribute to require.", required = true)
    private double value;

    @ConfigOption(name = "modifier-expression", description = "A modifier for handling the attribute value.")
    private String modifierExpression;

    private ItemAttribute attribute;

    @Override
    public void postProcess() {
        ItemAttribute attribute = ItemAttributes.getInstance().getAttributesConfig().getAttributes().get(this.key.getKey());
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute with key " + this.key + " not found.");
        }

        this.attribute = attribute;
    }

    public ItemAttribute getAttribute() {
        return this.attribute;
    }

    public Number getValue() {
        return this.value;
    }

    @Override
    public ModifierType<?> getType() {
        return ModifierType.ATTRIBUTE;
    }

    @Override
    public Component renderModifierInfo(AttributableItem item) {
        Component display = item.render(this.attribute.getDisplay());
        return AttributeCalculator.computeOperators(display, this.attribute.getType().convert(this.value));
    }

    @Override
    public void apply(ItemStack item, UnaryOperator<String> modifierOperator) {
        double value = this.value;
        if (this.modifierExpression != null) {
            String parsedExpression = modifierOperator.apply(AttributeCalculator.computeOperators(this.modifierExpression, value));
            value = ExpressionUtils.createExpression(parsedExpression).evaluate();
        }

        ItemAttributes.getInstance().getAttributeManager().bindAttribute(item, this.attribute, value);
    }
}
