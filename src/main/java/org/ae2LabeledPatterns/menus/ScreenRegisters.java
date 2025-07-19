package org.ae2LabeledPatterns.menus;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.ae2LabeledPatterns.integration.ae2wtlib.WireLessLabeledTerminalScreen;
import org.ae2LabeledPatterns.integration.ae2wtlib.WirelessLabeledTerminalMenu;

public final class ScreenRegisters {
    public static void register(RegisterMenuScreensEvent event){
        appeng.init.client.InitScreens.register(event, LabelerMenu.TYPE, LabelerScreen::new, "/screens/labeler.json");
        appeng.init.client.InitScreens.<LabeledPatternAccessTerminalMenu, LabeledPatternAccessTerminalScreen<LabeledPatternAccessTerminalMenu>>register
                (event, LabeledPatternAccessTerminalMenu.TYPE,
                LabeledPatternAccessTerminalScreen::new, "/screens/labeled_pattern_access_terminal.json");
        appeng.init.client.InitScreens.<WirelessLabeledTerminalMenu, WireLessLabeledTerminalScreen>register
                (event, WirelessLabeledTerminalMenu.TYPE,
                WireLessLabeledTerminalScreen::new, "/screens/wireless_labeled_pattern_access_terminal.json");
    }
}
