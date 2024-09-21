package com.latmod.mods.projectex.block.collectors;

import com.latmod.mods.projectex.tile.collectors.TileCollectorMK10;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCollectorMK10 extends BlockCollectorMKX {
    public BlockCollectorMK10() {
        super(10);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCollectorMK10();
    }
}
