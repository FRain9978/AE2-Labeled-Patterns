package org.ae2PatternTagger.menus;

import appeng.core.localization.LocalizationEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.ae2PatternTagger.Ae2patterntagger;
import org.jetbrains.annotations.NotNull;

public enum GUIText implements LocalizationEnum {
    TaggerInputPlaceHolder("type tag name here", "请输入标签名称"),
    TaggerTitle("Tagger", "标签工具"),
    TaggerEditConfirm("Confirm", "确认"),
    TaggerEditSave("Save", "保存"),
    TaggerEditLock("Lock Edit", "输入锁定"),
    TaggerEditLockHint("Block the input field for editing", "锁定输入框不允许编辑"),
    TaggerListTitle("Saved Tags", "保存的标签"),
    TaggerListSelect("Select", "选择"),
    TaggerListDelete("Delete", "删除"),
    AdvancedTerminalCycleTagButtonMessage("Tag Filter ", "标签过滤"),
    AdvancedTerminalCycleTagButtonEmptyFocus("No Focusing tag", "无关注标签"),
    AdvancedTerminalCycleTagButtonFocusing("Focusing: %s, Color: %s", "当前: %s, 颜色: %s"),

    // -------------------button text-------------------
    MoveConvenienceButtonTitle("Quick Move Convenience", "快速移动方式"),
    MoveConvenienceButtonNone("None", "无"),
//    MoveConvenienceButtonForAll("Try to insert the same pattern to every single pattern provider when current filtering tag is not empty. Need to have blank pattern in your inventory",
//            "当有过滤标签时，尝试将相同的样板插入到的每一个样板供应器中。物品栏中需要有空白样板。"),
//    MoveConvenienceButtonForAllStrict("Strictly insert the same pattern to every single pattern provider when current filtering tag is not empty. If you don't have enough blank patterns in your inventory, it will not work.",
//            "严格地将相同的样板插入到的每一个样板供应器中。如果物品栏中没有足够的空白样板，将不会起效。"),
    MoveConvenienceButtonForAll("Insert the same pattern to all pattern providers.",
            "将相同的样板插入到的每一个样板供应器中"),
    MoveConvenienceButtonForAllStrict("Strictly insert the same pattern to all pattern providers.",
            "严格地将相同的样板插入到的每一个样板供应器中。"),
    ;
    private final String root = "gui." + Ae2patterntagger.MODID;

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
