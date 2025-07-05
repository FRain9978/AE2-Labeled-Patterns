package org.ae2LabeledPatterns.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;
import org.ae2LabeledPatterns.blocks.BlockRegisters;
import org.ae2LabeledPatterns.integration.tooltips.InGameTooltip;
import org.ae2LabeledPatterns.integration.tooltips.TooltipProviders;
import org.ae2LabeledPatterns.items.ItemRegisters;
import org.ae2LabeledPatterns.menus.GUIText;

public class LanguageProviders {
    public static class ChineseLanguageProvider extends LanguageProvider{

        public ChineseLanguageProvider(PackOutput output) {
            super(output, Ae2LabeledPatterns.MODID, "zh_cn");
        }

        @Override
        protected void addTranslations() {
            this.add("creativetab.ae2labeledpatterns.main", "AE2标签化样板");
            this.add(Ae2LabeledPatterns.ClientModEvents.KEYBINDING_MOD_CATEGORY, "AE2标签化样板");
            this.add(Ae2LabeledPatterns.ClientModEvents.KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_1_DESCRIPTION, "用鼠标滚轮物品的修饰键1");
            this.add(Ae2LabeledPatterns.ClientModEvents.KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_2_DESCRIPTION, "用鼠标滚轮物品的修饰键2");

            for (var gui: GUIText.values()){
                this.add(gui.getTranslationKey(), gui.getChineseText());
            }
            for (var gui: InGameTooltip.values()){
                this.add(gui.getTranslationKey(), gui.getChineseText());
            }

            this.addBlock(BlockRegisters.MY_BLOCK, "我的方块");

            this.addItem(ItemRegisters.MY_ITEM, "我的物品");
            this.addItem(ItemRegisters.LABELER, "标签工具");
        }
    }

    public static class EnglishLanguageProvider extends LanguageProvider{

        public EnglishLanguageProvider(PackOutput output) {
            super(output, Ae2LabeledPatterns.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            this.add("creativetab.ae2labeledpatterns.main", "AE2 Labeled Patterns");
            this.add(Ae2LabeledPatterns.ClientModEvents.KEYBINDING_MOD_CATEGORY, "AE2 Labeled Patterns");
            this.add(Ae2LabeledPatterns.ClientModEvents.KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_1_DESCRIPTION, "Modifier 1 for Mouse-Wheel Items");
            this.add(Ae2LabeledPatterns.ClientModEvents.KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_2_DESCRIPTION, "Modifier 2 for Mouse-Wheel Items");

            for (var gui: GUIText.values()){
                this.add(gui.getTranslationKey(), gui.getEnglishText());
            }
            for (var gui: InGameTooltip.values()){
                this.add(gui.getTranslationKey(), gui.getEnglishText());
            }

            this.addBlock(BlockRegisters.MY_BLOCK, "My Block");

            this.addItem(ItemRegisters.MY_ITEM, "My Item");
            this.addItem(ItemRegisters.LABELER, "Labeler");

            addJadeProviderDisplayName(TooltipProviders.PATTERN_PROVIDER_LABEL, Ae2LabeledPatterns.MODID.toUpperCase() + "Pattern Provider Label");
        }

        private void addJadeProviderDisplayName(ResourceLocation providerId, String name) {
            this.add("config.jade.plugin_" + providerId.getNamespace() + "." + providerId.getPath(), name);
        }
    }
}
