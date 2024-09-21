package com.latmod.mods.projectex.tile.collectors;

import com.latmod.mods.projectex.ProjectEXUtils;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.utils.Constants;

public class TileCollectorMKX extends CollectorMK1Tile {
    public TileCollectorMKX(int level) {
        super(Constants.COLLECTOR_MK1_MAX.multiply(ProjectEXUtils.getBonus(level)), Constants.COLLECTOR_MK1_GEN.multiply(ProjectEXUtils.getBonus(level)));
    }

    @Override
    protected int getInvSize() {
        return 16;
    }
}
