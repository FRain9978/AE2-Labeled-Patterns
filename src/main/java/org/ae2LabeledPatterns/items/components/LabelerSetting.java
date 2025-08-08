package org.ae2LabeledPatterns.items.components;

import appeng.api.config.YesNo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LabelerSetting(YesNo isLockEdit, LabelerMode mode, Boolean isRename) {
    public LabelerSetting(){
        this(YesNo.NO, LabelerMode.SINGLE_SET, false);
    }

    public static final Codec<LabelerSetting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(
                    YesNo::valueOf,
                    YesNo::name
            ).fieldOf("isLockEdit").forGetter(LabelerSetting::isLockEdit),
            Codec.STRING.xmap(
                    LabelerMode::valueOf,
                    LabelerMode::name
            ).fieldOf("mode").forGetter(LabelerSetting::mode))
            .and(Codec.BOOL.fieldOf("isRename").forGetter(LabelerSetting::isRename))
            .apply(instance, LabelerSetting::new)
    );

    public static final StreamCodec<ByteBuf, LabelerSetting> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(
                    YesNo::valueOf,
                    YesNo::name), LabelerSetting::isLockEdit,
            ByteBufCodecs.STRING_UTF8.map(
                    LabelerMode::valueOf,
                    LabelerMode::name), LabelerSetting::mode,
            ByteBufCodecs.BOOL,
            LabelerSetting::isRename,
            LabelerSetting::new);
}
