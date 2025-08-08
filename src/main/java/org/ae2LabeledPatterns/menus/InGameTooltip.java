package org.ae2LabeledPatterns.menus;

import appeng.core.localization.LocalizationEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.ae2LabeledPatterns.AE2LabeledPatterns;
import org.jetbrains.annotations.NotNull;

public enum InGameTooltip implements LocalizationEnum {

    CycleLabelerMode("Current mode: %s", "当前模式: %s"),
    LabelerModeSingleSet("Set(Single)", "应用(单个)"),
    LabelerModeSingleClear("Clear(Single)", "清除(单个)"),
    LabelerModeAreaSet("Set(Area)", "应用(区域)"),
    LabelerModeAreaClear("Clear(Area)", "清除(区域)"),
    LabelerModeCopy("Copy", "复制"),
    LabelerModeRename("Rename", "重命名"),
    CycleLabelerLabel("Current label: %s", "当前标签: %s"),

    LabelerSelectedAreaTooBig("The area is too big, maximum is %d block space.", "范围太大，最大为%d方块空间"),
    LabelerSelectFirstPoint("First point selected", "第一个点已选择"),
    LabelerSelectDiffDimension("Selected point is in a different dimension", "选择的点在不同的维度"),
    LabelerSetProviderLabel("Set label to %s", "标签设置为 %s"),
    LabelerSetProviderName("Rename to %s", "重命名为 %s"),
    LabelerAreaSetProviderLabel("Set labels in area to %s", "区域内标签设置为 %s"),
    LabelerAreaSetProviderName("In Area rename to %s", "区域内重命名为 %s"),
    LabelerClearProviderLabel("Label cleared", "标签已清除"),
    LabelerClearProviderName("Name cleared", "命名已清除"),
    LabelerAreaClearProviderLabel("Labels in area cleared", "区域内标签已清除"),
    LabelerAreaClearProviderName("Names in area cleared", "区域内命名已清除"),
    LabelerCopiedLabel("Copied label: %s", "已复制标签: %s"),
    LabelerCopiedName("Copied name: %s", "已复制命名: %s"),
    ;
    private final String root = "chat." + AE2LabeledPatterns.MODID;

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
