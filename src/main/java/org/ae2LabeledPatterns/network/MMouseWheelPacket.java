package org.ae2LabeledPatterns.network;

import appeng.core.network.ServerboundPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.ae2LabeledPatterns.AE2LabeledPatterns;
import org.ae2LabeledPatterns.items.IMMouseWheelItem;

public record MMouseWheelPacket(boolean wheelUp, int index) implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, MMouseWheelPacket> STREAM_CODEC = StreamCodec.ofMember(MMouseWheelPacket::write, MMouseWheelPacket::decode);
    public static final CustomPacketPayload.Type<MMouseWheelPacket> TYPE =
            new CustomPacketPayload.Type<>(AE2LabeledPatterns.makeId("mouse_wheel_packet"));

    public MMouseWheelPacket(boolean wheelUp, int index) {
        this.wheelUp = wheelUp;
        this.index = index;
    }

    public CustomPacketPayload.Type<MMouseWheelPacket> type() {
        return TYPE;
    }

    public static MMouseWheelPacket decode(RegistryFriendlyByteBuf byteBuf) {
        boolean wheelUp = byteBuf.readBoolean();
        int index = byteBuf.readInt();
        return new MMouseWheelPacket(wheelUp, index);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeBoolean(this.wheelUp);
        data.writeInt(this.index);
    }

    public void handleOnServer(ServerPlayer player) {
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        Item item = mainHand.getItem();
        if (item instanceof IMMouseWheelItem mouseWheelItem) {
            mouseWheelItem.onWheel(player, mainHand, this.wheelUp, this.index);
        } else {
            item = offHand.getItem();
            if (item instanceof IMMouseWheelItem mouseWheelItem) {
                mouseWheelItem.onWheel(player, offHand, this.wheelUp, this.index);
            }
        }

    }

    public boolean wheelUp() {
        return this.wheelUp;
    }
}