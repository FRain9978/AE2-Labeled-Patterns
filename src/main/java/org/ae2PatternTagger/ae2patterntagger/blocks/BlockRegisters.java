package org.ae2PatternTagger.ae2patterntagger.blocks;

import appeng.blockentity.AEBaseBlockEntity;
import appeng.core.definitions.DeferredBlockEntityType;
import com.mojang.datafixers.DSL;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger.MODID;
import static org.ae2PatternTagger.ae2patterntagger.items.ItemRegisters.ITEMS;

public class BlockRegisters {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES  = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);


    public static final DeferredBlock<Block> MY_BLOCK;
    public static final String MY_BLOCK_ID = "my_block";

    static {
        //SAMPLE_BLOCK = BLOCKS.registerBlock(SAMPLE_BLOCK_ID, Block::new , BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK));
//        MY_BLOCK = BLOCKS.registerBlock(MY_BLOCK_ID, MyEntityBlock::new , BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT));
        MY_BLOCK = registerBlockWithItem(MY_BLOCK_ID, () -> new MyEntityBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)));

    }

    public static final Supplier<BlockEntityType<MyBlockEntity>> MY_BLOCK_ENTITY;
    public static final String MY_BLOCK_ENTITY_ID = "my_block_entity";

    static {
        MY_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
                MY_BLOCK_ID,
                () -> BlockEntityType.Builder.of(MyBlockEntity::new, MY_BLOCK.get()).build(null));
//        MY_BLOCK_ENTITY = registerBlockEntity(MY_BLOCK_ENTITY_ID, MyBlockEntity::new, MY_BLOCK.get());
    }

    private static <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Supplier<T> block){
        var outBlock =  BLOCKS.register(name, block);
        ITEMS.registerSimpleBlockItem(name, outBlock);
        return outBlock;
    }

//    private static <T extends BlockEntity> DeferredBlockEntityType<T> registerBlockEntity
//            (String name, BlockEntityType.BlockEntitySupplier<? extends T> factory, Block block){
//        return BLOCK_ENTITY_TYPES.register(
//                name,
//                () -> BlockEntityType.Builder.of(factory, block).build(null));
//    }
}
