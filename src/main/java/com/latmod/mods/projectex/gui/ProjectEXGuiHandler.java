package com.latmod.mods.projectex.gui;

import com.latmod.mods.projectex.ProjectEX;
import com.latmod.mods.projectex.gui.container.*;
import com.latmod.mods.projectex.tile.TileAlchemyTable;
import com.latmod.mods.projectex.tile.TileLink;
import com.latmod.mods.projectex.tile.collectors.TileCollectorMKX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ProjectEXGuiHandler implements IGuiHandler {
    public static final int LINK = 1;
    public static final int STONE_TABLE = 2;
    public static final int ARCANE_TABLET = 3;
    public static final int ALCHEMY_TABLE = 4;
    public static final int COLLECTOR_MKX = 5;

    public static void open(EntityPlayer player, int id, int x, int y, int z) {
        player.openGui(ProjectEX.INSTANCE, id, player.world, x, y, z);
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        switch (id) {
            case LINK:
                if (tileEntity instanceof TileLink)
                    return new ContainerLink(player, (TileLink) tileEntity);
                break;
            case STONE_TABLE:
                return new ContainerStoneTable(player);
            case ARCANE_TABLET:
                return new ContainerArcaneTablet(player);
            case ALCHEMY_TABLE:
                if (tileEntity instanceof TileAlchemyTable)
                    return new ContainerAlchemyTable(player, (TileAlchemyTable) tileEntity);
                break;
            case COLLECTOR_MKX:
                if (tileEntity instanceof TileCollectorMKX)
                    return new ContainerCollectorMKX(player, (TileCollectorMKX) tileEntity);
                break;
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        Container container = (Container) getServerGuiElement(id, player, world, x, y, z);
        switch (id) {
            case LINK:
                if (container instanceof ContainerLink)
                    return new GuiLink((ContainerLink) container);
                break;
            case STONE_TABLE:
                if (container instanceof ContainerStoneTable)
                    return new GuiStoneTable((ContainerStoneTable) container);
                break;
            case ARCANE_TABLET:
                if (container instanceof ContainerArcaneTablet)
                    return new GuiArcaneTablet((ContainerArcaneTablet) container);
                break;
            case ALCHEMY_TABLE:
                if (container instanceof ContainerAlchemyTable)
                    return new GuiAlchemyTable((ContainerAlchemyTable) container);
                break;
            case COLLECTOR_MKX:
                if (container instanceof ContainerCollectorMKX)
                    return new GuiCollectorMKX((ContainerCollectorMKX) container);
                break;
        }
        return null;
    }
}