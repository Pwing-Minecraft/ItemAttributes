package net.pwing.itemattributes.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Messages {
    public static final TextColor PRIMARY_COLOR = NamedTextColor.WHITE;
    public static final TextColor SECONDARY_COLOR = NamedTextColor.GRAY;
    public static final TextColor FAILURE_COLOR = NamedTextColor.RED;
    public static final TextColor SUCCESS_COLOR = NamedTextColor.GREEN;

    static final TagResolver RESOLVER = TagResolver.builder()
            .tag("primary", Tag.styling(PRIMARY_COLOR))
            .tag("secondary", Tag.styling(SECONDARY_COLOR))
            .tag("failure", Tag.styling(FAILURE_COLOR))
            .tag("success", Tag.styling(SUCCESS_COLOR))
            .build();

    static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    // Misc
    public static final Message HEADER = message("header",
            Component.text("------------------").color(NamedTextColor.GRAY)
                    .append(Component.text("[").color(SECONDARY_COLOR))
                    .append(Component.text(" "))
                    .append(Component.text("{}").color(PRIMARY_COLOR).decorate(TextDecoration.BOLD))
                    .append(Component.text(" "))
                    .append(Component.text("]").color(SECONDARY_COLOR))
                    .append(Component.text("------------------")).color(NamedTextColor.GRAY)
    );

    private static final Message EMPTY = wrap(Component.empty());

    // Command messages
    public static final Message PLUGIN_RELOADED = success("command-plugin-reloaded", "Plugin reloaded!");
    public static final Message MUST_BE_PLAYER = error("command-must-be-player", "You must be a player to use this command!");
    public static final Message COMMAND_USAGE = error("command-usage", "Invalid syntax! Usage: {}");
    public static final Message NO_PERMISSION = error("command-no-permission", "You do not have permission to execute this command!");
    public static final Message UNKNOWN_ERROR = error("command-unknown-error", "An unknown error occurred while executing this command! Please contact a server administrator!");
    public static final Message AN_ERROR_OCCURRED = error("command-an-error-occurred", "An error occurred while executing this command: {}!");
    public static final Message PLAYER_NOT_ONLINE = error("command-player-not-online", "The player <secondary>{}</secondary> is not online!");
    public static final Message PLAYER_NOT_FOUND = error("command-player-not-found", "The player <secondary>{}</secondary> could not be found!");
    public static final Message INVALID_TYPE = error("command-invalid-type", "The specified type <secondary>{}</secondary> was not a valid {}!");
    public static final Message INVALID_POSITION = error("command-invalid-position", "The specified position (<secondary>{}</secondary>) is not valid! Expected format: <secondary><x>,<y>,<z></secondary>.");
    public static final Message CLICK_TO_PREPARE = info("command-click-to-prepare", "Click to prepare: <secondary>{}</secondary>");
    public static final Message INVALID_NUMBER = error("command-invalid-number", "The specified number <secondary>{}</secondary> is not valid!");
    public static final Message PAGE = message("command-page", "Page");
    public static final Message COMMAND_PREVIOUS_PAGE = message("command-previous-page", "Click to go to the previous page.");
    public static final Message COMMAND_NEXT_PAGE = message("command-next-page", "Click to go to the next page.");

    // Attribute messages
    public static final Message ATTRIBUTE_MUST_BE_HOLDING_ITEM = error("attribute-must-be-holding-item", "You must be holding an item to bind an attribute to it!");
    public static final Message ATTRIBUTE_NOT_FOUND = error("attribute-not-found", "The attribute <secondary>{}</secondary> could not be found!");
    public static final Message ATTRIBUTE_BOUND = success("attribute-bound", "Successfully bound the attribute <secondary>{}</secondary> to your held item with a value of {}!");
    public static final Message ATTRIBUTE_UNBOUND = success("attribute-unbound", "Successfully unbound the attribute <secondary>{}</secondary> from your held item!");
    public static final Message ATTRIBUTE_NOT_BOUND = error("attribute-not-bound", "The attribute <secondary>{}</secondary> is not bound to your held item!");
    public static final Message ATTRIBUTE_HAS_NO_ATTRIBUTES = error("attribute-has-no-attributes", "This item has no attributes on it! To add an attribute, run <secondary>/attributes bind <attribute> <value></secondary>.");

    // Template messages
    public static final Message TEMPLATE_MUST_BE_HOLDING_ITEM = error("template-must-be-holding-item", "You must be holding an item to bind a template to it!");
    public static final Message TEMPLATE_NOT_FOUND = error("template-not-found", "The template <secondary>{}</secondary> could not be found!");
    public static final Message TEMPLATE_BOUND = success("template-bound", "Successfully bound the template <secondary>{}</secondary> to your held item!");

    // Tier messages
    public static final Message TIER_MUST_BE_HOLDING_ITEM = error("tier-must-be-holding-item", "You must be holding an item to bind a tier to it!");
    public static final Message TIER_NOT_FOUND = error("tier-not-found", "The tier <secondary>{}</secondary> could not be found!");
    public static final Message TIER_BOUND = success("tier-bound", "Successfully bound the tier <secondary>{}</secondary> to your held item!");

    // Generic item messages
    public static final Message MUST_BE_HOLDING_ITEM = error("item-must-be-holding-item", "You must be holding an item to perform this action!");
    public static final Message NAME_SET = success("item-name-set", "Successfully set the name of the item to <secondary>{}</secondary>!");
    public static final Message NAME_CLEARED = success("item-name-cleared", "Successfully cleared the name of the item!");
    public static final Message DESCRIPTION_SET = success("item-description-set", "Successfully set the description of the item to <secondary>{}</secondary>!");
    public static final Message DESCRIPTION_CLEARED = success("item-description-cleared", "Successfully cleared the description of the item!");

    // Generated messages
    public static final Message ITEM_GENERATED = success("item-generated", "Successfully generated item!");
    public static final Message ITEM_GENERATION_FAILED = error("item-generation-failed", "Failed to generate item: {}.");

    // Slot messages
    public static final Message SLOT_MUST_BE_HOLDING_ITEM = error("slot-must-be-holding-item", "You must be holding an item to add a slot to it!");
    public static final Message SLOT_NOT_FOUND = error("slot-not-found", "The slot <secondary>{}</secondary> could not be found!");
    public static final Message SLOT_BINDING_FAILED = error("slot-binding-failed", "Failed to bind the slot <secondary>{}</secondary> to your held item: {}.");
    public static final Message SLOT_OF_TYPE_NOT_FOUND = error("slot-of-type-not-found", "A slot of type <secondary>{}</secondary> could not be found!");
    public static final Message SLOT_INDEX_NOT_FOUND = error("slot-index-not-found", "The slot at index <secondary>{}</secondary> could not be found!");
    public static final Message SLOT_BOUND = success("slot-bound", "Successfully bound the slot <secondary>{}</secondary> to your held item!");
    public static final Message SLOT_REMOVED = success("slot-removed", "Successfully removed the slot at index <secondary>{}</secondary> from your held item!");
    public static final Message SLOT_EMPTY = message("slot-empty", "<gray>[<yellow>Empty</yellow>]</gray>");
    public static final Message SLOT_CREATOR_BOUND = success("slot-creator-bound", "Successfully bound the slot creator ability to your held item!");
    public static final Message SLOT_CREATOR_UNBOUND = success("slot-creator-unbound", "Successfully unbound the slot creator ability from your held item!");
    public static final Message SLOT_SELECTED_SPELL = info("slot-selected-spell", "Now using spell: <secondary>{}</secondary>.");
    public static final Message SLOT_HAS_NO_SLOTS = error("slot-has-no-slots", "This item has no slots on it! To add a slot, run <secondary>/items slot add <slot></secondary>.");
    public static final Message SLOT_NUMBER = info("slot-number", "Slot #{}");
    public static final Message SLOT_NAME = info("slot-name", "Name: {}");

    static void init() {
        // no-op
    }

    public static Message empty() {
        return EMPTY;
    }

    public static Message wrap(String defaultText) {
        return new Message("unregistered", MINI_MESSAGE.deserialize(defaultText, RESOLVER));
    }

    public static Message wrap(Component defaultComponent) {
        return new Message("unregistered", defaultComponent);
    }

    public static Message info(String translationKey, String defaultText) {
        return message(translationKey, MINI_MESSAGE.deserialize(defaultText, RESOLVER).color(PRIMARY_COLOR));
    }

    public static Message error(String translationKey, String defaultText) {
        return message(translationKey, MINI_MESSAGE.deserialize(defaultText, RESOLVER).color(FAILURE_COLOR));
    }

    public static Message success(String translationKey, String defaultText) {
        return message(translationKey, MINI_MESSAGE.deserialize(defaultText, RESOLVER).color(SUCCESS_COLOR));
    }

    public static Message message(String translationKey, String text) {
        return message(translationKey, MINI_MESSAGE.deserialize(text, RESOLVER));
    }

    public static Message message(String translationKey, String text, StyleBuilderApplicable... styles) {
        return message(translationKey, MINI_MESSAGE.deserialize(text, RESOLVER).style(Style.style(styles)));
    }

    public static Message message(String translationKey, String text, Style style) {
        return message(translationKey, MINI_MESSAGE.deserialize(text, RESOLVER).style(style));
    }

    public static Message message(String translationKey, Component text) {
        return MessageLoader.register(Message.of(translationKey, text));
    }

    @Nullable
    public static Message get(String translationKey) {
        return MessageLoader.get(translationKey);
    }
}
