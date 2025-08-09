package org.ae2LabeledPatterns.menus.subscreens;

import appeng.client.gui.AESubScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.TabButton;
import org.ae2LabeledPatterns.config.ClientConfig;
import org.ae2LabeledPatterns.menus.GUIText;
import org.ae2LabeledPatterns.menus.LabeledPatternAccessTerminalMenu;
import org.ae2LabeledPatterns.menus.LabeledPatternAccessTerminalScreen;

public class LabeledTerminalSettingScreen<C extends LabeledPatternAccessTerminalMenu> extends AESubScreen<C, LabeledPatternAccessTerminalScreen<C>> {

    private final AECheckbox autoFocus;
    private final AECheckbox rememberPosition;

    public LabeledTerminalSettingScreen(LabeledPatternAccessTerminalScreen<C> parent) {
        super(parent, "/screens/labeled_terminal_settings.json");

        addBackButton();
        autoFocus = widgets.addCheckbox("autoFocusCheckbox", GUIText.LabeledTerminalSettingAutoFocus.text(), this::save);
        rememberPosition = widgets.addCheckbox("rememberPositionCheckbox", GUIText.LabeledTerminalSettingRememberPosition.text(), this::save);
        updateState();
    }

    private void updateState(){
        autoFocus.setSelected(ClientConfig.GetAutoFocus());
        rememberPosition.setSelected(ClientConfig.GetRememberPosition());
    }

    private void save(){
        ClientConfig.SetAutoFocus(autoFocus.isSelected());
        ClientConfig.SetRememberPosition(rememberPosition.isSelected());
        ClientConfig.Save();
        updateState();
    }

    private void addBackButton() {
        var label = menu.getHost().getMainMenuIcon().getHoverName();
        TabButton button = new TabButton(Icon.BACK, label, btn -> returnToParent());
        widgets.add("back", button);
    }
}
