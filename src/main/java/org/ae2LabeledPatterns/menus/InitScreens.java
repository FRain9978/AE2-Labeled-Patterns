package org.ae2LabeledPatterns.menus;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class InitScreens {
    public static void register(RegisterMenuScreensEvent event){
        appeng.init.client.InitScreens.register(event, LabelerMenu.TYPE, LabelerScreen::new, "/screens/labeler.json");
        appeng.init.client.InitScreens.register(event, LabeledPatternAccessTerminalMenu.TYPE,
                LabeledPatternAccessTerminalScreen::new, "/screens/labeled_pattern_access_terminal.json");
    }
}
