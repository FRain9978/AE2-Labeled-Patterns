package org.ae2LabeledPatterns.items.components;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.ae2LabeledPatterns.integration.tooltips.InGameTooltip;

public enum LabelerMode implements IModeComponent {
    SINGLE_SET(InGameTooltip.LabelerModeSingleSet.text()),
    AREA_SET(InGameTooltip.LabelerModeAreaSet.text()),
    SINGLE_CLEAR(InGameTooltip.LabelerModeSingleClear.text()),
    AREA_CLEAR(InGameTooltip.LabelerModeAreaClear.text()),
    ;

    private final Component component;

    LabelerMode(Component component){
        this.component = component;
    }

    @Override
    public MutableComponent text() {
        return (MutableComponent) component;
    }
}
