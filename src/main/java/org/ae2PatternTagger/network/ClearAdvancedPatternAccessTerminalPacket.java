
package org.ae2PatternTagger.network;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.ae2PatternTagger.menus.AdvancedPatternAccessTerminalScreen;
import org.slf4j.LoggerFactory;

/**
 * Clears all data from the pattern access terminal before a full reset.
 */
public record ClearAdvancedPatternAccessTerminalPacket() implements ClientboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClearAdvancedPatternAccessTerminalPacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    ClearAdvancedPatternAccessTerminalPacket::write,
                    ClearAdvancedPatternAccessTerminalPacket::decode);

    public static final Type<ClearAdvancedPatternAccessTerminalPacket> TYPE = CustomAppEngPayload
            .createType("clear_advanced_pattern_access_terminal");

    @Override
    public Type<ClearAdvancedPatternAccessTerminalPacket> type() {
        return TYPE;
    }

    public static ClearAdvancedPatternAccessTerminalPacket decode(RegistryFriendlyByteBuf data) {
        return new ClearAdvancedPatternAccessTerminalPacket();
    }

    public void write(RegistryFriendlyByteBuf data) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        LoggerFactory.getLogger(this.getClass()).debug("Clearing advanced pattern access terminal data for player: {}", player.getName().getString());
        if (Minecraft.getInstance().screen instanceof AdvancedPatternAccessTerminalScreen patternAccessTerminal) {
            patternAccessTerminal.clear();
        }
    }
}
