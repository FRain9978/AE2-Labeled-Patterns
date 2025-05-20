package org.ae2PatternTagger.ae2patterntagger.menus;

import appeng.core.localization.LocalizationEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum GUIText implements LocalizationEnum {
    TaggerInputPlaceHolder("enter tag name here", "输入标签名称"),
    TaggerTitle("Tagger", "标签工具"),
    TaggerEditConfirm("Confirm", "确认"),
    TaggerEditSave("Save", "保存"),
    TaggerEditLock("Lock Edit", "输入锁定"),
    TaggerEditLockHint("Block the input field for editing", "锁定输入框不允许编辑"),
    TaggerListTitle("Tag List", "标签列表"),
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
