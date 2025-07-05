package org.ae2LabeledPatterns.network;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.ServerboundPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;

public class InitNetwork {
    public static void init(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(Ae2LabeledPatterns.MODID);

        // Clientbound

        clientbound(registrar, ClearLabeledPatternAccessTerminalPacket.TYPE, ClearLabeledPatternAccessTerminalPacket.STREAM_CODEC);
        clientbound(registrar, LabeledPatternAccessTerminalPacket.TYPE, LabeledPatternAccessTerminalPacket.STREAM_CODEC);
        clientbound(registrar, SaveLabelAttachmentPacket.TYPE, SaveLabelAttachmentPacket.STREAM_CODEC);

        serverbound(registrar, InventoryQuickMovePacket.TYPE, InventoryQuickMovePacket.STREAM_CODEC);
        serverbound(registrar, MMouseWheelPacket.TYPE, MMouseWheelPacket.STREAM_CODEC);

        // Serverbound
//        serverbound(registrar, ColorApplicatorSelectColorPacket.TYPE, ColorApplicatorSelectColorPacket.STREAM_CODEC);

        // Bidirectional
//        bidirectional(registrar, ConfigValuePacket.TYPE, ConfigValuePacket.STREAM_CODEC);
    }

    private static <T extends ClientboundPacket> void clientbound(PayloadRegistrar registrar,
                                                                  CustomPacketPayload.Type<T> type,
                                                                  StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToClient(type, codec, ClientboundPacket::handleOnClient);
    }

    private static <T extends ServerboundPacket> void serverbound(PayloadRegistrar registrar,
                                                                  CustomPacketPayload.Type<T> type,
                                                                  StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToServer(type, codec, ServerboundPacket::handleOnServer);
    }

    private static <T extends ServerboundPacket & ClientboundPacket> void bidirectional(PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playBidirectional(type, codec, (payload, context) -> {
            if (context.flow().isClientbound()) {
                payload.handleOnClient(context);
            } else if (context.flow().isServerbound()) {
                payload.handleOnServer(context);
            }
        });
    }
}
