package net.pwing.itemattributes.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentIteratorType;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.pwing.itemattributes.ItemAttributes;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TextUtils {
    public static final TextReplacementConfig CAPITALIZE = TextReplacementConfig.builder()
            .match(".+")
            .replacement((matchResult, inputText) -> inputText.content(inputText.content().toUpperCase(Locale.ROOT)))
            .build();

    public static final TextReplacementConfig LOWERCASE = TextReplacementConfig.builder()
            .match(".+")
            .replacement((matchResult, inputText) -> inputText.content(inputText.content().toLowerCase(Locale.ROOT)))
            .build();

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public static String toLegacy(Component component) {
        return SERIALIZER.serialize(component).replaceAll("<([^<>]+)>", "");
    }

    public static Component fromLegacy(String legacy) {
        return SERIALIZER.deserialize(legacy);
    }

    public static String toPlain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String toMiniMessage(Component component) {
        return Messages.MINI_MESSAGE.serialize(component);
    }

    public static Component fromMiniMessage(String miniMessage) {
        return Messages.MINI_MESSAGE.deserialize(miniMessage);
    }

    public static String capitalize(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }

        char[] buffer = str.toCharArray();
        boolean capitalizeNext = true;

        for(int i = 0; i < buffer.length; ++i) {
            char ch = buffer[i];
            if (Character.isWhitespace(ch)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer[i] = Character.toTitleCase(ch);
                capitalizeNext = false;
            }
        }

        return new String(buffer);
    }

    public static List<Component> splitComponentAtIntervals(@NotNull Component component, int perLine, @Nullable Style style) {
        if (!(component instanceof TextComponent)) {
            return List.of(component);
        }

        if (perLine == Integer.MAX_VALUE) {
            return List.of(component);
        }

        List<List<WordComponentWrapper>> lines = new ArrayList<>();
        List<WordComponentWrapper> currentLine = new ArrayList<>();

        int count = 0;
        Map<TextComponent, Style> childrensParentsStyles = new HashMap<>();

        for (Component splitPart : component.compact().iterable(ComponentIteratorType.DEPTH_FIRST)) {
            if (!(splitPart instanceof TextComponent splitComponent)) {
                continue;
            }

            Style currentStyle = splitComponent.style();
            Style parentsStyle = childrensParentsStyles.get(splitComponent);
            if (parentsStyle != null) {
                currentStyle = currentStyle.merge(parentsStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
            }

            List<Component> children = splitComponent.children();
            for (Component child : children) {
                if (child instanceof TextComponent textComponentChild) {
                    childrensParentsStyles.put(textComponentChild, currentStyle);
                }
            }

            String content = splitComponent.content();
            String[] linesArray = content.split("\n", -1); // Preserve empty lines

            for (int lineIndex = 0; lineIndex < linesArray.length; lineIndex++) {
                String line = linesArray[lineIndex];
                String[] words = line.isBlank() ? new String[] { "" } : line.split(" ");

                for (int i = 0; i < words.length; i++) {
                    String part = words[i].trim();
                    boolean trailingSpace = i < words.length - 1 || line.endsWith(" ");

                    if (trailingSpace) {
                        part += ' ';
                    }

                    count += part.length();
                    currentLine.add(new WordComponentWrapper(part, currentStyle));

                    if (count >= perLine && trailingSpace) {
                        lines.add(currentLine);
                        currentLine = new ArrayList<>();
                        count = 0;
                    }
                }

                // If we processed a newline, force a new line
                if (lineIndex < linesArray.length - 1) {
                    lines.add(currentLine);
                    currentLine = new ArrayList<>();
                    count = 0;
                }
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
        }

        List<Component> components = new ArrayList<>();

        for (List<WordComponentWrapper> line : lines) {
            List<TextComponent> componentList = new ArrayList<>();
            for (WordComponentWrapper word : line) {
                if (word.word.isEmpty()) {
                    continue;
                }

                Style mergedStyle = style == null ? word.style : word.style.merge(style, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
                componentList.add(Component.text(word.word).style(mergedStyle));
            }

            components.add(componentList.stream().collect(Component.toComponent()));
        }

        return components;
    }

    public static void sendMessage(CommandSender sender, Component message) {
        ItemAttributes.getAudiences().sender(sender).sendMessage(message);
    }

    public record WordComponentWrapper(String word, Style style) {
    }
}
