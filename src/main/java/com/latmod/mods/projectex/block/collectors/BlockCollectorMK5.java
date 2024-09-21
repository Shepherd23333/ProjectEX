package com.latmod.mods.projectex.block.collectors;

import com.latmod.mods.projectex.tile.collectors.TileCollectorMK5;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCollectorMK5 extends BlockCollectorMKX {
    public BlockCollectorMK5() {
        super(5);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCollectorMK5();
    }
}
