package org.ae2PatternTagger.items.components;

import appeng.api.config.YesNo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TaggerSetting(YesNo isLockEdit) {
    public TaggerSetting(){
        this(YesNo.NO);
    }

    public static final Codec<TaggerSetting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(
                    YesNo::valueOf,
                    YesNo::name
            ).fieldOf("isLockEdit").forGetter(TaggerSetting::isLockEdit))
            .apply(instance, TaggerSetting::new)
    );

    public static final StreamCodec<ByteBuf, TaggerSetting> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(
                    YesNo::valueOf,
                    YesNo::name), TaggerSetting::isLockEdit,
            TaggerSetting::new);
}
