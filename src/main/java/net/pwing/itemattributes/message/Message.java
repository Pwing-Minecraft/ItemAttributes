package net.pwing.itemattributes.message;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class Message {
    private final String translationKey;
    private Component text;

    boolean context;

    Message(String translationKey, Component defaultText) {
        this.translationKey = translationKey;
        this.text = defaultText;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    Component getText() {
        return this.text;
    }

    void setText(Component text) {
        this.text = text;
    }

    public void send(CommandSender sender) {
        TextUtils.sendMessage(sender, this.toComponent(sender));
    }

    public void send(CommandSender sender, String... replacements) {
        TextUtils.sendMessage(sender, this.toComponent(sender, replacements));
    }

    public void send(CommandSender sender, Component... replacements) {
        TextUtils.sendMessage(sender, this.toComponent(sender, replacements));
    }

    public void send(CommandSender sender, Message... replacements) {
        Component[] compReplacements = new Component[replacements.length];
        for (int i = 0; i < compReplacements.length; i++) {
            Message replacement = replacements[i];
            compReplacements[i] = replacement.toComponent(sender);
        }

        TextUtils.sendMessage(sender, this.toComponent(sender, compReplacements));
    }

    public Message withContext(CommandSender sender, String... replacements) {
        return Message.of(this.translationKey, this.toComponent(sender, replacements)).attachContext();
    }

    public String asMiniMessage(CommandSender sender) {
        return Messages.MINI_MESSAGE.serialize(this.toComponent(sender));
    }

    public Component toComponent(CommandSender sender) {
        // Replace viewer with the sender name
        return this.text.replaceText(builder -> builder.matchLiteral("%viewer%").replacement(sender.getName()));
    }

    public Component toComponent() {
        return this.text;
    }

    public Component toComponent(CommandSender sender, String... replacements) {
        Component[] compReplacements = new Component[replacements.length];
        for (int i = 0; i < compReplacements.length; i++) {
            String replacement = replacements[i];
            compReplacements[i] = Component.text(replacement);
        }

        return this.toComponent(sender, compReplacements);
    }

    public Component toComponent(CommandSender sender, Message... replacements) {
        Component[] compReplacements = new Component[replacements.length];
        for (int i = 0; i < compReplacements.length; i++) {
            Message replacement = replacements[i];
            compReplacements[i] = replacement.toComponent(sender);
        }

        return this.toComponent(sender, compReplacements);
    }

    public Component toComponent(CommandSender sender, Component... replacements) {
        Component text = this.text;
        for (Component replacement : replacements) {
            text = text.replaceText(builder -> builder.matchLiteral("{}").once().replacement(replacement));
        }

        // Replace viewer with the sender name
        text = text.replaceText(builder -> builder.matchLiteral("%viewer%").replacement(sender.getName()));

        return text;
    }

    private Message attachContext() {
        this.context = true;
        return this;
    }

    static Message of(String translationKey, Component text) {
        return new Message(translationKey, text);
    }
}
