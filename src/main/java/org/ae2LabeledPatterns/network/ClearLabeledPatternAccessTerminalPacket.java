
package org.ae2LabeledPatterns.network;

import appeng.core.network.ClientboundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.ae2LabeledPatterns.AE2LabeledPatterns;
import org.ae2LabeledPatterns.menus.LabeledPatternAccessTerminalScreen;
import org.slf4j.LoggerFactory;

/**
 * Clears all data from the pattern access terminal before a full reset.
 */
public record ClearLabeledPatternAccessTerminalPacket() implements ClientboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClearLabeledPatternAccessTerminalPacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    ClearLabeledPatternAccessTerminalPacket::write,
                    ClearLabeledPatternAccessTerminalPacket::decode);

    public static final Type<ClearLabeledPatternAccessTerminalPacket> TYPE =
            new CustomPacketPayload.Type<>(AE2LabeledPatterns.makeId("clear_advanced_pattern_access_terminal"));

    @Override
    public Type<ClearLabeledPatternAccessTerminalPacket> type() {
        return TYPE;
    }

    public static ClearLabeledPatternAccessTerminalPacket decode(RegistryFriendlyByteBuf data) {
        return new ClearLabeledPatternAccessTerminalPacket();
    }

    public void write(RegistryFriendlyByteBuf data) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        LoggerFactory.getLogger(this.getClass()).debug("Clearing labeled pattern access terminal data for player: {}", player.getName().getString());
        if (Minecraft.getInstance().screen instanceof LabeledPatternAccessTerminalScreen<?> patternAccessTerminal) {
            patternAccessTerminal.clear();
        }
    }
}
