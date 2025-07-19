package org.ae2LabeledPatterns.integration.ae2wtlib;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToolboxPanel;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.ae2LabeledPatterns.menus.LabeledPatternAccessTerminalScreen;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import org.jetbrains.annotations.NotNull;

public class WireLessLabeledTerminalScreen extends LabeledPatternAccessTerminalScreen<WirelessLabeledTerminalMenu>
        implements IUniversalTerminalCapable {

    private final ScrollingUpgradesPanel upgradesPanel;

    public WireLessLabeledTerminalScreen(WirelessLabeledTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        if (this.getMenu().isWUT()) {
            this.addToLeftToolbar(this.cycleTerminalButton());
        }

        this.upgradesPanel = this.addUpgradePanel(this.widgets, this.getMenu());
        if (this.getMenu().getToolbox().isPresent()) {
            this.widgets.add("toolbox", new ToolboxPanel(style, this.getMenu().getToolbox().getName()));
        }
    }

    @Override
    public void init() {
        super.init();
        this.upgradesPanel.setMaxRows(Math.max(2, this.getVisibleRows()));
    }

    @Override
    public @NotNull WTMenuHost getHost() {
        return (WTMenuHost) this.getMenu().getHost();
    }

    @Override
    public void storeState() {

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int keyPressed) {
        boolean value = super.keyPressed(keyCode, scanCode, keyPressed);
        return value || this.checkForTerminalKeys(keyCode, scanCode);
    }
}
