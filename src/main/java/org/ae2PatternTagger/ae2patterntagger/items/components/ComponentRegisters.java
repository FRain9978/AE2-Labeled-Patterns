package org.ae2PatternTagger.ae2patterntagger.items.components;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;

import java.util.List;

public class ComponentRegisters {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Ae2patterntagger.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PatternProviderTag>> PATTERN_PROVIDER_TAG =
            COMPONENTS.registerComponentType(
                    "pattern_provider_tag",
                    builder -> builder
                            .persistent(PatternProviderTag.CODEC)
                            .networkSynchronized(PatternProviderTag.STREAM_CODEC)
                            .cacheEncoding()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<PatternProviderTag>>> SAVED_TAGS =
            COMPONENTS.registerComponentType(
                    "saved_tags",
                    builder -> builder
                            .persistent(Codec.list(PatternProviderTag.CODEC))
                            .networkSynchronized(PatternProviderTag.STREAM_CODEC.apply(ByteBufCodecs.list()))
                            .cacheEncoding()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TaggerSetting>> TAGGER_SETTING =
            COMPONENTS.registerComponentType(
                    "tagger_setting",
                    builder -> builder
                            .persistent(TaggerSetting.CODEC)
                            .networkSynchronized(TaggerSetting.STREAM_CODEC)
                            .cacheEncoding()
            );
}
