package org.ae2PatternTagger.ae2patterntagger.integration.tooltips;

import appeng.api.integrations.igtooltip.*;
import appeng.block.AEBaseBlock;
import appeng.block.crafting.PatternProviderBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.AEBaseInvBlockEntity;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.core.AppEng;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import net.minecraft.resources.ResourceLocation;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;
import org.ae2PatternTagger.ae2patterntagger.blocks.MyBlockEntity;
import org.ae2PatternTagger.ae2patterntagger.blocks.MyEntityBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TooltipProviders implements TooltipProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TooltipProviders.class);

    public static final ResourceLocation PATTERN_PROVIDER_TAG = Ae2patterntagger.makeId("pattern_provider_tag");

    static {
        PartTooltips.addBody(AEBaseBlockEntity.class, new PatternProviderDataProvider());
        PartTooltips.addServerData(AEBaseBlockEntity.class, new PatternProviderDataProvider());
    }

    @Override
    public void registerClient(ClientRegistration registration) {
        LOG.debug("Registering client tooltip providers for {}", PATTERN_PROVIDER_TAG);
        registration.addBlockEntityBody(
                AEBaseBlockEntity.class,
                AEBaseBlock.class,
                PATTERN_PROVIDER_TAG,
                new PatternProviderDataProvider(),
                DEFAULT_PRIORITY - 1
        );
//        registration.addBlockEntityBody(
//                MyBlockEntity.class,
//                MyEntityBlock.class,
//                PATTERN_PROVIDER_TAG,
//                new PatternProviderDataProvider(),
//                DEFAULT_PRIORITY + 1
//        );
    }

    @Override
    public void registerCommon(CommonRegistration registration) {
        LOG.debug("Registering common tooltip providers for {}", PATTERN_PROVIDER_TAG);
        registration.addBlockEntityData(
                PATTERN_PROVIDER_TAG,
                AEBaseBlockEntity.class,
                new PatternProviderDataProvider());
//        registration.addBlockEntityData(
//                PATTERN_PROVIDER_TAG,
//                MyBlockEntity.class,
//                new PatternProviderDataProvider());
    }

    @Override
    public void registerBlockEntityBaseClasses(BaseClassRegistration registration) {
//        LOG.debug("Registering base block entity classes for {}", PATTERN_PROVIDER_TAG);
//        registration.addBaseBlockEntity(MyBlockEntity.class, MyEntityBlock.class);
    }
}
