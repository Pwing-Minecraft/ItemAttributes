/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pwing.itemattributes.util;

import com.google.common.base.Strings;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.translation.GlobalTranslator;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.message.TextUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.PrimitiveIterator;

/**
 * Pagination calculator for players. Handles calculation of text widths,
 * centering text, adding padding, adding spacing, and more.
 */
public final class PaginationCalculator {

    private static final String NON_UNICODE_CHARS;
    private static final List<Integer> NON_UNICODE_CHAR_WIDTHS;
    private static final List<Byte> UNICODE_CHAR_WIDTHS;
    private static final int LINE_WIDTH = 300;
    private static final Component DEFAULT_PADDING = Component.space();

    private static final int CHARS_TO_SPLIT = 40;

    static {
        Configuration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(ItemAttributes.getInstance().getResource("font-sizes.yml")));

        NON_UNICODE_CHARS = configuration.getString("non-unicode");
        NON_UNICODE_CHAR_WIDTHS = configuration.getIntegerList("char-widths");
        UNICODE_CHAR_WIDTHS = configuration.getByteList("glyph-widths");
    }

    /**
     * Gets the width of a character with the specified code
     * point, accounting for if its text is bold our not.
     *
     * @param codePoint The code point of the character
     * @param isBold Whether or not the character is bold or not
     * @return The width of the character at the code point
     */
    private static int getWidth(final int codePoint, final boolean isBold) {
        final int nonUnicodeIdx = NON_UNICODE_CHARS.indexOf(codePoint);
        int width;
        if (codePoint == 32) {
            width = 4;
        } else if (codePoint > 0 && nonUnicodeIdx != -1) {
            width = NON_UNICODE_CHAR_WIDTHS.get(nonUnicodeIdx);
        } else if (UNICODE_CHAR_WIDTHS.get(codePoint) != 0) {
            //from 1.9 & 255 to avoid strange signed int math ruining things.
            //https://bugs.mojang.com/browse/MC-7181
            final int temp = UNICODE_CHAR_WIDTHS.get(codePoint) & 255;
            // Split into high and low nibbles.
            //bit digits
            //87654321 >>> 4 = 00008765
            final int startColumn = temp >>> 4;
            //87654321 & 00001111 = 00004321
            final int endColumn = temp & 15;

            width = (endColumn + 1) - startColumn;
            //Why does this scaling happen?
            //I believe it makes unicode fonts skinnier to better match the character widths of the default Minecraft
            // font however there is a int math vs float math bug in the Minecraft FontRenderer.
            //The float math is adjusted for rendering, they attempt to do the same thing for calculating string widths
            //using integer math, this has potential rounding errors, but we should copy it and use ints as well.
            width = (width / 2) + 1;
        } else {
            width = 0;
        }
        //if bolded width gets 1 added.
        if (isBold && width > 0) {
            width += 1;
        }

        return width;
    }

    public static int getWidth(@NotNull final Component text) {
        return getWidth(text, LINE_WIDTH);
    }

    /**
     * Calculates the width of a given text as the number of character
     * pixels/columns the line takes up.
     *
     * @param text The text to get the width of
     * @return The amount of character pixels/columns the text takes up
     */
    public static int getWidth(@NotNull final Component text, int fullLineWidth) {
        final Deque<Component> children = new ArrayDeque<>(1 + text.children().size());
        children.add(text);
        int total = 0;

        Component child;
        while ((child = children.pollFirst()) != null) {
            // Add all children
            for (final Component grandchild : child.children()) {
                children.add(grandchild.style(child.style().merge(grandchild.style())));
            }

            // Then if we contain text, we can capture that
            final PrimitiveIterator.OfInt i_it;
            if (child instanceof TextComponent textComponent) {
                i_it = textComponent.content().codePoints().iterator();
            } else if (child instanceof final TranslatableComponent tl) {
                // Try to find a value registered from Adventure
                final MessageFormat global = GlobalTranslator.translator().translate(tl.key(), Locale.ENGLISH);
                if (global != null) {
                    i_it = global.toPattern().codePoints().iterator();
                    children.addAll(tl.args());
                } else {
                    // TODO
                    /*
                    // If there's no adventure translation, then fall back to native
                    final String mc = Language.getInstance().getOrDefault(tl.key());
                    if (!mc.equals(tl.key())) {
                        // we found a translation, so let's include the with elements
                        children.addAll(tl.args());
                    }
                    // Either way, this is the best we'll get for calculating width
                    i_it = mc.codePoints().iterator();

                     */
                    throw new UnsupportedOperationException("No adventure translation found for TranslatableComponent");
                }
            } else {
                continue;
            }

            final boolean bold = child.style().hasDecoration(TextDecoration.BOLD);

            int codePoint;
            boolean newLine = false;
            while (i_it.hasNext()) {
                codePoint = i_it.nextInt();
                if (codePoint == '\n') {
                    // if the previous character is a '\n'
                    if (newLine) {
                        total += fullLineWidth;
                    } else {
                        total = ((int) Math.ceil((double) total / fullLineWidth)) * fullLineWidth;
                        newLine = true;
                    }
                } else {
                    final int width = getWidth(codePoint, bold);
                    total += width;
                    newLine = false;
                }
            }
        }

        return total;
    }

    /**
     * Centers a text within the middle of the chat box.
     *
     * <p>Generally used for titles and footers.</p>
     *
     * <p>To use no heading, just pass in a 0 width text for
     * the first argument.</p>
     *
     * @param text The text to center
     * @return The centered text, or if too big, the original text
     */
    public static Component center(@NotNull Component text) {
        return center(text, LINE_WIDTH, false);
    }

    /**
     * Centers a text within the middle of the chat box.
     *
     * <p>Generally used for titles and footers.</p>
     *
     * <p>To use no heading, just pass in a 0 width text for
     * the first argument.</p>
     *
     * @param text The text to center
     * @param padding A padding character with a width >1
     * @return The centered text, or if too big, the original text
     */
    public static Component center(@NotNull Component text, @NotNull Component padding) {
        return center(text, padding, LINE_WIDTH, false);
    }

    /**
     * Centers a text within the middle of the chat box.
     *
     * <p>Generally used for titles and footers.</p>
     *
     * <p>To use no heading, just pass in a 0 width text for
     * the first argument.</p>
     *
     * @param text The text to center
     * @param width The width of the chat box
     * @param padEnd If padding should be added to the end of the text
     * @return The centered text, or if too big, the original text
     */
    public static Component center(@NotNull Component text, int width, boolean padEnd) {
        List<Component> components = TextUtils.splitComponentAtIntervals(text, CHARS_TO_SPLIT, text.style());
        TextComponent.Builder builder = Component.text();
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            builder.append(center(component, DEFAULT_PADDING, width, padEnd));
            if (i < components.size() - 1) {
                builder.append(Component.newline());
            }
        }

        return builder.build();
    }

    /**
     * Centers a text within the middle of the chat box.
     *
     * <p>Generally used for titles and footers.</p>
     *
     * <p>To use no heading, just pass in a 0 maxWidth text for
     * the first argument.</p>
     *
     * @param text The text to center
     * @param padding A padding character with a width >1
     * @param maxWidth The width of the chat box before wrapping
     * @return The centered text, or if too big, the original text
     */
    //TODO: Probably should completely rewrite this to not compute padding, but loop until the padding is done, unless
    //we can get accurate computation of padding ahead of time.
    public static Component center(@NotNull Component text, @NotNull Component padding, int maxWidth, boolean padEnd) {
        int inputLength = getWidth(text, maxWidth);

        //Minecraft breaks lines when the next character would be > then LINE_WIDTH, this seems most graceful way to fail
        if (inputLength >= maxWidth) {
            return text;
        }

        final Component textWithSpaces = addSpaces(Component.space(), text);

        //Minecraft breaks lines when the next character would be > then LINE_WIDTH
        final boolean addSpaces = getWidth(textWithSpaces, maxWidth) <= maxWidth;

        int paddingLength = getWidth(padding, maxWidth);
        final TextComponent.Builder output = Component.text();

        //Using 0 width unicode symbols as padding throws us into an unending loop, replace them with the default padding
        if (paddingLength < 1) {
            padding = DEFAULT_PADDING;
            paddingLength = getWidth(padding, maxWidth);
        }

        //if we only need padding
        if (inputLength == 0) {
            addPadding(padding, output, (int) Math.floor((double) maxWidth / paddingLength));
        } else {
            if (addSpaces) {
                text = textWithSpaces;
                inputLength = getWidth(textWithSpaces, maxWidth);
            }

            final int paddingNecessary = maxWidth - inputLength;

            final int paddingCount = (int)Math.floor((double) paddingNecessary / paddingLength);
            //pick a halfway point
            final int beforePadding = (int)Math.ceil(paddingCount / 2.0);
            //Do not use ceil, this prevents floating point errors.
            final int afterPadding = paddingCount - beforePadding;

            addPadding(padding, output, beforePadding);
            output.append(text);
            if (padEnd) {
                addPadding(padding, output, afterPadding);
            }
        }

        return output.build();
    }

    /**
     * Adds spaces to both sides of the specified text.
     *
     * <p>Overrides all color and style with the
     * text's color and style.</p>
     *
     * @param spaces The spaces to use
     * @param text The text to add to
     * @return The text with the added spaces
     */
    private static Component addSpaces(@NotNull final BuildableComponent<?, ?> spaces, @NotNull final Component text) {
        return spaces.toBuilder()
                .style(text.style())
                .append(text.style(Style.empty()))
                .append(spaces)
                .build();
    }

    /**
     * Adds the specified padding text to a piece of text being built
     * up to a certain amount specified by a count.
     *
     * @param padding The padding text to use
     * @param build The work in progress text to add to
     * @param count The amount of padding to add
     */
    private static void addPadding(@NotNull final Component padding, @NotNull final TextComponent.Builder build, final int count) {
        if (count > 0) {
            // In simple cases, we can create a more compact component
            if (padding instanceof TextComponent && padding.children().isEmpty()) {
                build.append(Component.text(Strings.repeat(((TextComponent) padding).content(), count), padding.style()));
            } else {
                build.append(Collections.nCopies(count, padding));
            }
        }
    }
}
