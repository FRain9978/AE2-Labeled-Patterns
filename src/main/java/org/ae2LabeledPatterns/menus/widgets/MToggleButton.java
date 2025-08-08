package org.ae2LabeledPatterns.menus.widgets;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ToggleButton;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;

public class MToggleButton extends MIconButton{
    private final ToggleButton.Listener listener;

    private final Blitter iconOn;
    private final Blitter iconOff;

    private List<Component> tooltipOn = Collections.emptyList();
    private List<Component> tooltipOff = Collections.emptyList();

    private boolean state;

    public MToggleButton(Blitter on, Blitter off, Component displayName,
                         Component displayHint, ToggleButton.Listener listener) {
        this(on, off, listener);
        setTooltipOn(List.of(displayName, displayHint));
        setTooltipOff(List.of(displayName, displayHint));
    }

    public MToggleButton(Blitter on, Blitter off, ToggleButton.Listener listener) {
        super(null);
        this.iconOn = on;
        this.iconOff = off;
        this.listener = listener;
    }

    public void setTooltipOn(List<Component> lines) {
        this.tooltipOn = lines;
    }

    public void setTooltipOff(List<Component> lines) {
        this.tooltipOff = lines;
    }

    @Override
    public void onPress() {
        this.listener.onChange(!state);
    }

    public void setState(boolean isOn) {
        this.state = isOn;
    }

    @Override
    protected Blitter getIconBlitter() {
        return this.state ? iconOn : iconOff;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return state ? tooltipOn : tooltipOff;
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return super.isTooltipAreaVisible() && !getTooltipMessage().isEmpty();
    }
}
