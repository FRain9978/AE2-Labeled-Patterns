package org.ae2PatternTagger.ae2patterntagger.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;
import org.ae2PatternTagger.ae2patterntagger.blocks.BlockRegisters;
import org.ae2PatternTagger.ae2patterntagger.items.ItemRegisters;
import org.ae2PatternTagger.ae2patterntagger.menus.GUIText;

public class LanguageProviders {
    public static class ChineseLanguageProvider extends LanguageProvider{

        public ChineseLanguageProvider(PackOutput output) {
            super(output, Ae2patterntagger.MODID, "zh_cn");
        }

        @Override
        protected void addTranslations() {
            this.add("creativetab.ae2patterntagger.main", "AE2标签工具");

            for (var gui : GUIText.values()){
                this.add(gui.getTranslationKey(), gui.getChineseText());
            }

            this.addBlock(BlockRegisters.MY_BLOCK, "我的方块");

            this.addItem(ItemRegisters.MY_ITEM, "我的物品");
            this.addItem(ItemRegisters.TAGGER, "标签工具");
        }
    }

    public static class EnglishLanguageProvider extends LanguageProvider{

        public EnglishLanguageProvider(PackOutput output) {
            super(output, Ae2patterntagger.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            this.add("creativetab.ae2patterntagger.main", "AE2 Pattern Tagger");

            for (var gui : GUIText.values()){
                this.add(gui.getTranslationKey(), gui.getEnglishText());
            }

            this.addBlock(BlockRegisters.MY_BLOCK, "My Block");

            this.addItem(ItemRegisters.MY_ITEM, "My Item");
            this.addItem(ItemRegisters.TAGGER, "Tagger");
        }
    }
}
