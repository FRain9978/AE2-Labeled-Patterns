package org.ae2LabeledPatterns.integration.ae2wtlib;

import appeng.api.features.GridLinkables;
import appeng.items.tools.powered.WirelessTerminalItem;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.ae2LabeledPatterns.AE2LabeledPatterns;
import org.ae2LabeledPatterns.items.ItemRegisters;

public class AE2wtlibRegisters {
//    public static final WirelessLabeledTerminalItem WIRELESS_TERMINAL = new WirelessLabeledTerminalItem();
    public static final String WIRELESS_TERMINAL_TRANSLATION_KEY = "item." + AE2LabeledPatterns.MODID + ".wireless_labeled_pattern_access_terminal";

    public static void Init(RegisterEvent e){
        if (e.getRegistryKey().equals(Registries.ITEM)) {
            GridLinkables.register(ItemRegisters.WIRELESS_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
            Icon.Texture TX = new Icon.Texture(AE2LabeledPatterns.makeId("textures/guis/icon_wireless_labeled_pattern_access_terminal.png"), 16, 16);
            AddTerminalEvent.register(event -> event.builder(
                    "labeled_pattern_access",
                    WirelessLabeledTerminalHost::new,
                    WirelessLabeledTerminalMenu.TYPE,
                    (ItemWT) ItemRegisters.WIRELESS_TERMINAL.get(),
                    new Icon(0, 0, 16, 16, TX)
            ).hotkeyName("wireless_labeled_pattern_access_terminal").translationKey(WIRELESS_TERMINAL_TRANSLATION_KEY).addTerminal());
        }
    }
}
