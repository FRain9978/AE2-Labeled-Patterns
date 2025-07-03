package org.ae2LabeledPatterns;

import appeng.api.AECapabilities;
import appeng.api.networking.IInWorldGridNodeHost;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.ae2LabeledPatterns.blocks.BlockRegisters;

public final class InitCapabilities {
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, BlockRegisters.MY_BLOCK_ENTITY.get(),
                (object, context) -> (IInWorldGridNodeHost) object);
    }
}
