package org.ae2LabeledPatterns.network;

import appeng.core.network.ClientboundPacket;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;
import org.ae2LabeledPatterns.attachments.AttachmentRegisters;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;

public record SaveLabelAttachmentPacket(
        PatternProviderLabel label,
        BlockPos blockPos
) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, SaveLabelAttachmentPacket> STREAM_CODEC = StreamCodec.ofMember(
            SaveLabelAttachmentPacket::write,
            SaveLabelAttachmentPacket::decode
    );

    public static final Type<SaveLabelAttachmentPacket> TYPE =
            new CustomPacketPayload.Type<>(Ae2LabeledPatterns.makeId("save_tag_attachment"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static SaveLabelAttachmentPacket decode(RegistryFriendlyByteBuf stream){
        var tag = PatternProviderLabel.STREAM_CODEC.decode(stream);
        var blockPos = stream.readBlockPos();
        return new SaveLabelAttachmentPacket(tag, blockPos);
    }

    private void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        PatternProviderLabel.STREAM_CODEC.encode(registryFriendlyByteBuf, label);
        registryFriendlyByteBuf.writeBlockPos(blockPos);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player){
        var level = player.level();
        var blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof PatternProviderLogicHost){
            blockEntity.setData(AttachmentRegisters.PATTERN_PROVIDER_LABEL, label);
        }
    }
}
