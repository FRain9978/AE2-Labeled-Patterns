package org.ae2LabeledPatterns.menus;

import appeng.core.localization.LocalizationEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.ae2LabeledPatterns.AE2LabeledPatterns;
import org.jetbrains.annotations.NotNull;

public enum GUIText implements LocalizationEnum {
    LabelerHoverTooltipLabel("Label: %s", "标签: %s"),
    LabelerHoverTooltipMode("Mode: %s", "模式: %s"),
    LabelerHoverTooltipRenameOn("Rename function is on", "重命名功能开启"),
    LabelerHoverTooltipRenameOff("Rename function is off", "重命名功能关闭"),
    LabelerHoverTooltipShiftTip("Hold Shift to see more information", "按住Shift键查看更多信息"),
    LabelerHoverTooltipChangeMode("Hold %s and scroll mouse to change mode", "按住键 %s 并滚动鼠标以更改模式"),
    LabelerHoverTooltipChangeLabel("Hold %s and scroll mouse to change label", "按住键 %s 并滚动鼠标以更改标签"),

    LabelerInputPlaceHolder("type label name here", "请输入标签名称"),
    LabelerTitle("Labeler", "标签工具"),
    LabelerEditConfirm("Confirm", "确认"),
    LabelerEditSave("Save", "保存"),
    LabelerEditLock("Lock Edit", "输入锁定"),
    LabelerEditLockHint("Block the input field for editing", "锁定输入框以防止编辑"),
    LabelerOpenList("Open Label List", "打开标签列表"),
    LabelerRenameButtonTitle("Rename Function", "重命名功能"),
    LabelerRenameButtonHint("Enable to edit the provider's custom name instead of label.", "开启以修改供应器的自定义名称而不是标签。"),

    LabelListTitle("Saved Labels", "保存的标签"),
    LabelListSelect("Select", "选择"),
    LabelListDelete("Delete", "删除"),

    LabeledTerminalCycleTagButtonMessage("Label Filter ", "标签过滤"),
    LabeledTerminalCycleTagButtonEmptyFocus("No Focusing label", "无过滤"),
    LabeledTerminalCycleTagButtonFocusing("Focusing: %s", "当前: %s"),
    LabeledTerminalSettingAutoFocus("Auto-Focus on open", "打开时自动聚焦搜索框"),
    LabeledTerminalSettingRememberPosition("Remember the position of scroll bar on close", "关闭时记住滚动条位置"),



    // -------------------button text-------------------
    MoveConvenienceButtonTitle("Quick Move Convenience", "快速移动便利"),
    MoveConvenienceButtonNone("None", "无"),
    MoveConvenienceButtonForAll("Try to insert the same pattern to all pattern providers in current selected group. Only work with enough blank patterns.",
            "试图将相同的样板插入到的每一个样板供应器中，有足够的空白样板才起效。"),
    ShowGroupSelectRatioTitle("Group Selection", "组选择"),
    ShowGroupSelectRatioYes("Check box is shown", "按钮显示"),
    ShowGroupSelectRatioNo("Check box is hidden", "按钮隐藏"),
    ;

    private final String root = "gui." + AE2LabeledPatterns.MODID;

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
