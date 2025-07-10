package org.ae2LabeledPatterns.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record BlockTarget(ResourceLocation dimension, BlockPos pos) {
    public static final Codec<BlockTarget> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("dimension").forGetter(BlockTarget::dimension),
                    BlockPos.CODEC.fieldOf("pos").forGetter(BlockTarget::pos)
            ).apply(instance, BlockTarget::new));

    public static final StreamCodec<ByteBuf, BlockTarget> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC,
                    BlockTarget::dimension,
                    BlockPos.STREAM_CODEC,
                    BlockTarget::pos,
                    BlockTarget::new
            );
}
