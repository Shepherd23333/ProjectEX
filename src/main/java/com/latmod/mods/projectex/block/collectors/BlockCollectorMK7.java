package com.latmod.mods.projectex.block.collectors;

import com.latmod.mods.projectex.tile.collectors.TileCollectorMK7;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCollectorMK7 extends BlockCollectorMKX {
    public BlockCollectorMK7() {
        super(7);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCollectorMK7();
    }
}
