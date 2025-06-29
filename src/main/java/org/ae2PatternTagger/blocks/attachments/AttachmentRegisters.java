package org.ae2PatternTagger.blocks.attachments;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.ae2PatternTagger.Ae2patterntagger;
import org.ae2PatternTagger.items.components.PatternProviderTag;

import java.util.function.Supplier;

public class AttachmentRegisters {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Ae2patterntagger.MODID);

    public static final Supplier<AttachmentType<PatternProviderTag>> PATTERN_PROVIDER_TAG = ATTACHMENTS.register(
            "pattern_provider_tag", () -> AttachmentType.builder(
                    () -> PatternProviderTag.Empty)
                    .serialize(PatternProviderTag.CODEC)
                    .build()
    );
}
