package org.ae2LabeledPatterns.capability;

import appeng.items.tools.powered.powersink.PoweredItemCapabilities;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.ae2LabeledPatterns.items.ItemRegisters;

public class CapabilityRegisters {
    public static void Init(RegisterCapabilitiesEvent event){
        event.registerItem(Capabilities.EnergyStorage.ITEM,
                (object, context) -> new PoweredItemCapabilities(object, (ItemWT)ItemRegisters.WIRELESS_TERMINAL.get()), ItemRegisters.WIRELESS_TERMINAL.get());
    }
}
