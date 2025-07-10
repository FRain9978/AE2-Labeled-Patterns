package org.ae2LabeledPatterns.attachments;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;

import java.util.function.Supplier;

public class AttachmentRegisters {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Ae2LabeledPatterns.MODID);

    public static final Supplier<AttachmentType<PatternProviderLabel>> PATTERN_PROVIDER_LABEL = ATTACHMENTS.register(
            "pattern_provider_label", () -> AttachmentType.builder(
                    () -> PatternProviderLabel.Empty)
                    .serialize(PatternProviderLabel.CODEC)
                    .build()
    );
}
