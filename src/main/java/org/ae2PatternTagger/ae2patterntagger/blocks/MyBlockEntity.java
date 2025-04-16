package org.ae2PatternTagger.ae2patterntagger.blocks;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.stacks.AEItemKey;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.me.helpers.IGridConnectedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.networking.GridHelper;

import java.util.EnumSet;
import java.util.List;

import static org.ae2PatternTagger.ae2patterntagger.blocks.BlockRegisters.MY_BLOCK;
import static org.ae2PatternTagger.ae2patterntagger.blocks.BlockRegisters.MY_BLOCK_ENTITY;

public class MyBlockEntity extends AENetworkedPoweredBlockEntity implements PatternProviderLogicHost {
//    private final IManagedGridNode node = GridHelper.createManagedNode(this, MyBlockEntityListener.INSTANCE)
//            .setInWorldNode(true)
//            .setVisualRepresentation(MY_BLOCK.get())
//            .setFlags(GridFlags.REQUIRE_CHANNEL).setIdlePowerUsage(2d)
//            .setExposedOnSides(getGridConnectableSides(null));

    private final PatternProviderLogic logic = new PatternProviderLogic(getMainNode(), this);

    public MyBlockEntity(BlockPos pos, BlockState blockState) {
        super(MY_BLOCK_ENTITY.get(), pos, blockState);
        getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL).setIdlePowerUsage(2d);
        this.setInternalMaxPower(160000);
        this.setPowerSides(getGridConnectableSides(getOrientation()));
    }

    @Override
    public InternalInventory getInternalInventory() {
        return logic.getPatternInv();
    }

    @Override
    protected Item getItemFromBlockEntity() {
        return getBlockState().getBlock().asItem();
    }

    @Override
    public PatternProviderLogic getLogic() {
        return logic;
    }

    @Override
    public EnumSet<Direction> getTargets() {
        return EnumSet.allOf(Direction.class);
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(getBlockState().getBlock());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return getBlockState().getBlock().asItem().getDefaultInstance();
    }

    @Override
    public int getPriority() {
        return super.getPriority();
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        this.logic.addDrops(drops);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.logic.clearContent();
    }

    @Override
    public void onReady() {
        super.onReady();
        this.logic.updatePatterns();
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.logic.writeToNBT(data, registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.logic.readFromNBT(data, registries);
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        this.logic.onMainNodeStateChanged();
    }
}
