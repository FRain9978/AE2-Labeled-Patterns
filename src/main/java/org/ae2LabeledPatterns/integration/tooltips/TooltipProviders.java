package org.ae2LabeledPatterns.integration.tooltips;

import appeng.api.integrations.igtooltip.*;
import appeng.block.AEBaseBlock;
import appeng.blockentity.AEBaseBlockEntity;
import net.minecraft.resources.ResourceLocation;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TooltipProviders implements TooltipProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TooltipProviders.class);

    public static final ResourceLocation PATTERN_PROVIDER_LABEL = Ae2LabeledPatterns.makeId("pattern_provider_label");

    static {
        PartTooltips.addBody(AEBaseBlockEntity.class, new PatternProviderDataProvider());
        PartTooltips.addServerData(AEBaseBlockEntity.class, new PatternProviderDataProvider());
    }

    @Override
    public void registerClient(ClientRegistration registration) {
        LOG.debug("Registering client tooltip providers for {}", PATTERN_PROVIDER_LABEL);
        registration.addBlockEntityBody(
                AEBaseBlockEntity.class,
                AEBaseBlock.class,
                PATTERN_PROVIDER_LABEL,
                new PatternProviderDataProvider(),
                DEFAULT_PRIORITY - 1
        );

    }

    @Override
    public void registerCommon(CommonRegistration registration) {
        LOG.debug("Registering common tooltip providers for {}", PATTERN_PROVIDER_LABEL);
        registration.addBlockEntityData(
                PATTERN_PROVIDER_LABEL,
                AEBaseBlockEntity.class,
                new PatternProviderDataProvider());
    }

    @Override
    public void registerBlockEntityBaseClasses(BaseClassRegistration registration) {

    }
}
