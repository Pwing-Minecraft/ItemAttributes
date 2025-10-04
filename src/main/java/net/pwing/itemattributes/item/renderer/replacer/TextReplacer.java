package net.pwing.itemattributes.item.renderer.replacer;

public class TextReplacer implements Replacer<String> {
    private String rawText;

    public TextReplacer(String rawText) {
        this.rawText = rawText;
    }

    @Override
    public TextReplacer replace(String key, String value) {
        this.rawText = this.rawText.replace("%" + key + "%", value);
        return this;
    }

    @Override
    public String complete() {
        return this.rawText;
    }
}
