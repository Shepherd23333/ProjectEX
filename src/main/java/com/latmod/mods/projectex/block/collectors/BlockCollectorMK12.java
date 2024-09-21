package com.latmod.mods.projectex.block.collectors;

import com.latmod.mods.projectex.tile.collectors.TileCollectorMK12;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCollectorMK12 extends BlockCollectorMKX {
    public BlockCollectorMK12() {
        super(12);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCollectorMK12();
    }
}
