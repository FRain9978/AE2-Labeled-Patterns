package org.ae2PatternTagger.ae2patterntagger.blocks;

import appeng.api.AECapabilities;
import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.menu.locator.MenuLocators;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyEntityBlock extends AEBaseEntityBlock<MyBlockEntity>
{
    public MyEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.full();
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull BlockHitResult hitResult) {
//        if (!level.isClientSide){
//            boolean hasCapability = level.getCapability(AECapabilities.IN_WORLD_GRID_NODE_HOST, pos, state, getBlockEntity(level, pos)) != null;
//            player.sendSystemMessage(Component.empty().append(Component.literal(String.format("you right click me !\n do i has node host capability? %s", hasCapability))));
//        }
        MyBlockEntity blockEntity = (MyBlockEntity) level.getBlockEntity(pos);
        if (blockEntity != null){
            if (!level.isClientSide) {
                blockEntity.openMenu(player, MenuLocators.forBlockEntity(blockEntity));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new MyBlockEntity(blockPos, blockState);
    }
}
