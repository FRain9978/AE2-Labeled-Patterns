package org.ae2LabeledPatterns.menus;

import appeng.core.localization.LocalizationEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;
import org.jetbrains.annotations.NotNull;

public enum GUIText implements LocalizationEnum {
    LabelerInputPlaceHolder("type label name here", "请输入标签名称"),
    LabelerTitle("Labeler", "标签工具"),
    LabelerEditConfirm("Confirm", "确认"),
    LabelerEditSave("Save", "保存"),
    LabelerEditLock("Lock Edit", "输入锁定"),
    LabelerEditLockHint("Block the input field for editing", "锁定输入框以防止编辑"),
    LabelListTitle("Saved Labels", "保存的标签"),
    LabelListSelect("Select", "选择"),
    LabelListDelete("Delete", "删除"),
    LabeledTerminalCycleTagButtonMessage("Label Filter ", "标签过滤"),
    LabeledTerminalCycleTagButtonEmptyFocus("No Focusing label", "无过滤"),
    LabeledTerminalCycleTagButtonFocusing("Focusing: %s", "当前: %s"),

    // -------------------button text-------------------
    MoveConvenienceButtonTitle("Quick Move Convenience", "快速移动便利"),
    MoveConvenienceButtonNone("None", "无"),
//    MoveConvenienceButtonForAll("Try to insert the same pattern to every single pattern provider when current filtering label is not empty. Need to have blank pattern in your inventory",
//            "当有过滤标签时，尝试将相同的样板插入到的每一个样板供应器中。物品栏中需要有空白样板。"),
//    MoveConvenienceButtonForAllStrict("Strictly insert the same pattern to every single pattern provider when current filtering label is not empty. If you don't have enough blank patterns in your inventory, it will not work.",
//            "严格地将相同的样板插入到的每一个样板供应器中。如果物品栏中没有足够的空白样板，将不会起效。"),
    MoveConvenienceButtonForAll("Try to insert the same pattern to all pattern providers in current selected group. Need blank patterns.",
            "试图将相同的样板插入到的每一个样板供应器中，需要空白样板。"),
    MoveConvenienceButtonForAllStrict("Strictly insert pattern to providers. Only work with enough blank patterns.",
            "严格地将样板插入供应器中。在有足够的空白样板才起效。"),
    ShowGroupSelectRatioTitle("Group Selection", "组选择"),
    ShowGroupSelectRatioYes("Check box is shown", "按钮显示"),
    ShowGroupSelectRatioNo("Check box is hidden", "按钮隐藏"),
    ;

    private final String root = "gui." + Ae2LabeledPatterns.MODID;

    @NotNull
    private final String englishText;

    private final String chineseText;

    private final Component text;

    GUIText(@NotNull String englishText) {
        this.englishText = englishText;
        this.chineseText = englishText;
        this.text = Component.translatable(getTranslationKey());
    }

    GUIText(@NotNull String englishText, String chineseText) {
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
