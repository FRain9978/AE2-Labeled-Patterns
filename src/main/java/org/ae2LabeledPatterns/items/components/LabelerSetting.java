package org.ae2LabeledPatterns.items.components;

import appeng.api.config.YesNo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LabelerSetting(YesNo isLockEdit) {
    public LabelerSetting(){
        this(YesNo.NO);
    }

    public static final Codec<LabelerSetting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(
                    YesNo::valueOf,
                    YesNo::name
            ).fieldOf("isLockEdit").forGetter(LabelerSetting::isLockEdit))
            .apply(instance, LabelerSetting::new)
    );

    public static final StreamCodec<ByteBuf, LabelerSetting> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(
                    YesNo::valueOf,
                    YesNo::name), LabelerSetting::isLockEdit,
            LabelerSetting::new);
}
