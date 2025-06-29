package org.ae2PatternTagger.integration.tooltips;

import appeng.core.localization.LocalizationEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.ae2PatternTagger.Ae2patterntagger;
import org.jetbrains.annotations.NotNull;

public enum InGameTooltip implements LocalizationEnum {
    PatternProviderTag("Tag name: %s, Color: %s","标签: %s，颜色: %s"),
    ;
    private final String root = "waila." + Ae2patterntagger.MODID;

    @NotNull
    private final String englishText;

    private final String chineseText;

    private final Component text;

    InGameTooltip(@NotNull String englishText) {
        this.englishText = englishText;
        this.chineseText = englishText;
        this.text = Component.translatable(getTranslationKey());
    }

    InGameTooltip(@NotNull String englishText, String chineseText) {
        this.englishText = englishText;
        this.chineseText = chineseText;
        this.text = Component.translatable(getTranslationKey());
    }

    public @NotNull String getEnglishText() {
        return englishText;
    }

    public @NotNull String getChineseText() {
        return chineseText;
    }

    @Override
    public String getTranslationKey() {
        return this.root + '.' + name().toLowerCase();
    }

    @Override
    public MutableComponent text() {
        return (MutableComponent) this.text;
    }

    public String getLocal() {
        return text.getString();
    }
}
