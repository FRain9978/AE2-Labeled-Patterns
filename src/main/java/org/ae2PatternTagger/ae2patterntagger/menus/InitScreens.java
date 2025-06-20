package org.ae2PatternTagger.ae2patterntagger.menus;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class InitScreens {
    public static void register(RegisterMenuScreensEvent event){
        appeng.init.client.InitScreens.register(event, TaggerMenu.TYPE, TaggerScreen::new, "/screens/tagger.json");
        appeng.init.client.InitScreens.register(event, AdvancedPatternAccessTerminalMenu.TYPE,
                AdvancedPatternAccessTerminalScreen::new, "/screens/terminals/pattern_access_terminal.json");
    }
}
