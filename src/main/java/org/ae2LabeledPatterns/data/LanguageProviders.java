package org.ae2LabeledPatterns.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.ae2LabeledPatterns.AE2LabeledPatterns;
import org.ae2LabeledPatterns.AE2LabeledPatternsClient;
import org.ae2LabeledPatterns.integration.tooltips.WailaToolTip;
import org.ae2LabeledPatterns.menus.InGameTooltip;
import org.ae2LabeledPatterns.integration.tooltips.TooltipProviders;
import org.ae2LabeledPatterns.items.ItemRegisters;
import org.ae2LabeledPatterns.menus.GUIText;
import org.ae2LabeledPatterns.parts.PartRegisters;

public class LanguageProviders {
    public static class ChineseLanguageProvider extends LanguageProvider{

        public ChineseLanguageProvider(PackOutput output) {
            super(output, AE2LabeledPatterns.MODID, "zh_cn");
        }

        @Override
        protected void addTranslations() {
            this.add("creativetab.ae2labeledpatterns.main", "AE2标签化样板");
            this.add(AE2LabeledPatternsClient.KEYBINDING_MOD_CATEGORY, "AE2标签化样板");
            this.add(AE2LabeledPatternsClient.KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_1_DESCRIPTION, "鼠标滚轮物品修饰键1");
            this.add(AE2LabeledPatternsClient.KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_2_DESCRIPTION, "鼠标滚轮物品修饰键2");

            for (var gui: GUIText.values()){
                this.add(gui.getTranslationKey(), gui.getChineseText());
            }
            for (var gui: InGameTooltip.values()){
                this.add(gui.getTranslationKey(), gui.getChineseText());
            }
            for (var gui: WailaToolTip.values()){
                this.add(gui.getTranslationKey(), gui.getChineseText());
            }

            this.addItem(ItemRegisters.LABELER, "标签工具");
            this.addItem(PartRegisters.LABELED_PATTERN_ACCESS_TERMINAL, "ME标签化样板管理终端");
            this.addItem(ItemRegisters.WIRELESS_TERMINAL, "无线标签化样板管理终端");
            this.add("key.ae2.wireless_labeled_pattern_access_terminal","打开无线标签化样板管理终端");
        }
    }

    public static class EnglishLanguageProvider extends LanguageProvider{

        public EnglishLanguageProvider(PackOutput output) {
            super(output, AE2LabeledPatterns.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            this.add("creativetab.ae2labeledpatterns.main", "AE2 Labeled Patterns");
            this.add(AE2LabeledPatternsClient.KEYBINDING_MOD_CATEGORY, "AE2 Labeled Patterns");
            this.add(AE2LabeledPatternsClient.KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_1_DESCRIPTION, "Modifier 1 for Mouse-Wheel Items");
            this.add(AE2LabeledPatternsClient.KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_2_DESCRIPTION, "Modifier 2 for Mouse-Wheel Items");

            for (var gui: GUIText.values()){
                this.add(gui.getTranslationKey(), gui.getEnglishText());
            }
            for (var gui: InGameTooltip.values()){
                this.add(gui.getTranslationKey(), gui.getEnglishText());
            }
            for (var gui: WailaToolTip.values()){
                this.add(gui.getTranslationKey(), gui.getEnglishText());
            }

            this.addItem(ItemRegisters.LABELER, "Labeler");
            this.addItem(PartRegisters.LABELED_PATTERN_ACCESS_TERMINAL, "ME Labeled Pattern Access Terminal");
            this.addItem(ItemRegisters.WIRELESS_TERMINAL, "Wireless Labeled Pattern Access Terminal");
            this.add("key.ae2.wireless_labeled_pattern_access_terminal", "Open Wireless Labeled P. A. Terminal");

            addJadeProviderDisplayName(TooltipProviders.PATTERN_PROVIDER_LABEL, AE2LabeledPatterns.MODID.toUpperCase() + "Pattern Provider Label");
        }

        private void addJadeProviderDisplayName(ResourceLocation providerId, String name) {
            this.add("config.jade.plugin_" + providerId.getNamespace() + "." + providerId.getPath(), name);
        }
    }
}
