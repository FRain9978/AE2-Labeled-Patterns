package org.ae2LabeledPatterns.integration.tooltips;

import appeng.core.localization.LocalizationEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;
import org.jetbrains.annotations.NotNull;

public enum InGameTooltip implements LocalizationEnum {
    PatternProviderLabel("Label: %s, Color: %s","标签: %s，颜色: %s"),

    CycleLabelerMode("Current mode: %s", "当前模式: %s"),
    LabelerModeSingleSet("Set(Single)", "应用(单个)"),
    LabelerModeSingleClear("Clear(Single)", "清除(单个)"),
    LabelerModeAreaSet("Set(Area)", "应用(区域)"),
    LabelerModeAreaClear("Clear(Area)", "清除(区域)"),
    CycleLabelerLabel("Current label: %s", "当前标签: %s"),

    LabelerSelectedAreaTooBig("The area is too big, maximum is %d block space.", "范围太大，最大为%d方块空间"),
    LabelerSelectFirstPoint("First point selected", "第一个点已选择"),
    LabelerSelectDiffDimension("Selected point is in a different dimension", "选择的点在不同的维度"),
    LabelerSetProviderLabel("Set label to %s", "标签设置为 %s"),
    LabelerAreaSetProviderLabel("Set area label to %s", "区域标签设置为 %s"),
    LabelerClearProviderLabel("Label cleared", "标签已清除"),
    LabelerAreaClearProviderLabel("Area label cleared", "区域标签已清除"),
    ;
    private final String root = "waila." + Ae2LabeledPatterns.MODID;

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
