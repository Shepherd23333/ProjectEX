package com.latmod.mods.projectex.block.collectors;

import com.latmod.mods.projectex.tile.collectors.TileCollectorMK9;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCollectorMK9 extends BlockCollectorMKX {
    public BlockCollectorMK9() {
        super(9);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCollectorMK9();
    }
}
