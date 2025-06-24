package org.ae2PatternTagger.ae2patterntagger.items;

import appeng.api.config.Setting;
import appeng.api.config.YesNo;
import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.storage.ISubMenuHost;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.ae2PatternTagger.ae2patterntagger.MSettings;
import org.ae2PatternTagger.ae2patterntagger.blocks.attachments.AttachmentRegisters;
import org.ae2PatternTagger.ae2patterntagger.items.components.ComponentRegisters;
import org.ae2PatternTagger.ae2patterntagger.items.components.PatternProviderTag;
import org.ae2PatternTagger.ae2patterntagger.menus.TaggerMenu;
import org.ae2PatternTagger.ae2patterntagger.network.SaveTagAttachmentPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TaggerItem extends Item implements IMenuItem, IConfigurableObject {
    private final IConfigManager configManager;

    public TaggerItem(Properties properties) {
        super(properties);
        this.configManager = IConfigManager.builder(this::onConfigChanged)
                .registerSetting(MSettings.TAGGER_INPUT_LOCKED, YesNo.NO)
                .build();
    }

    private void onConfigChanged(IConfigManager manager, Setting<?> setting) {

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        var selfData = stack.getComponents().getOrDefault(ComponentRegisters.PATTERN_PROVIDER_TAG.get(), new PatternProviderTag("sample"));
        tooltipComponents.add(Component.literal(String.format("name: %s color: %s", selfData.name(), selfData.color())).withColor(TextColor.parseColor(selfData.color().toString().substring(0, 7)).getOrThrow().getValue()));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
//        player.sendSystemMessage(Component.literal("use item"));
        if (player.isCrouching()) return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        if (!level.isClientSide()) {
            MenuOpener.open(TaggerMenu.TYPE, player, MenuLocators.forHand(player, usedHand));
        }
        player.swing(usedHand);
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()),
                player.getItemInHand(usedHand));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        if (blockEntity instanceof PatternProviderLogicHost){
            if (blockEntity.hasData(AttachmentRegisters.PATTERN_PROVIDER_TAG)){
                player.sendSystemMessage(Component.literal("-----------------"));
                player.sendSystemMessage(Component.literal("Client: " + level.isClientSide));
                player.sendSystemMessage(Component.literal("Content: "+ blockEntity.getData(AttachmentRegisters.PATTERN_PROVIDER_TAG).name()));
                player.sendSystemMessage(Component.literal("Color: "+ blockEntity.getData(AttachmentRegisters.PATTERN_PROVIDER_TAG).color().toString()));
                player.sendSystemMessage(Component.literal("-----------------"));
            }
            if (level.isClientSide){
                return InteractionResult.sidedSuccess(true);
            }
            ServerPlayer serverPlayer = (ServerPlayer) player;
            if (player.isCrouching()){
                ItemStack itemStack = context.getItemInHand();
                if (itemStack.getItem() instanceof TaggerItem){
                    var hasTag = itemStack.has(ComponentRegisters.PATTERN_PROVIDER_TAG.get());
                    if (hasTag){
                        var tag = itemStack.getComponents().get(ComponentRegisters.PATTERN_PROVIDER_TAG.get());
                        var data = blockEntity.getData(AttachmentRegisters.PATTERN_PROVIDER_TAG);
                        if (tag != null && !tag.equals(data)) {
                            blockEntity.setData(AttachmentRegisters.PATTERN_PROVIDER_TAG, tag);
                            serverPlayer.connection.send(
                                    new SaveTagAttachmentPacket(tag, blockEntity.getBlockPos()));
                        }
                    }
                    return InteractionResult.sidedSuccess(false);
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public @Nullable ItemMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new TaggerItemMenuHost(this, player, locator);
    }

    @Override
    public IConfigManager getConfigManager() {
        return configManager;
    }

    public class TaggerItemMenuHost extends ItemMenuHost<TaggerItem> implements ISubMenuHost{

        public TaggerItemMenuHost(TaggerItem item, Player player, ItemMenuHostLocator locator) {
            super(item, player, locator);
        }

        @Override
        public void returnToMainMenu(Player player, ISubMenu subMenu) {
            MenuOpener.open(TaggerMenu.TYPE, player, subMenu.getLocator(), true);
        }

        @Override
        public ItemStack getMainMenuIcon() {
            return TaggerItem.this.getDefaultInstance();
        }
    }
}
