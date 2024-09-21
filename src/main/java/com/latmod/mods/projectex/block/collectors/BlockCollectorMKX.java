package com.latmod.mods.projectex.block.collectors;

import com.latmod.mods.projectex.ProjectEXUtils;
import com.latmod.mods.projectex.gui.ProjectEXGuiHandler;
import com.latmod.mods.projectex.tile.collectors.TileCollectorMKX;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.blocks.BlockDirection;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCFormat;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.List;

public class BlockCollectorMKX extends BlockDirection {
    private final int tier;

    public BlockCollectorMKX(int tier) {
        super(Material.GLASS);
        this.setLightLevel(1.0F);
        this.setHardness(0.3f);
        this.tier = tier;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCollectorMKX(4);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote)
            ProjectEXGuiHandler.open(player, ProjectEXGuiHandler.COLLECTOR_MKX, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        String unit = I18n.format("pe.emc.name"), rate = I18n.format("pe.emc.rate");
        BigInteger s = Constants.COLLECTOR_MK1_MAX.multiply(ProjectEXUtils.getBonus(tier)), r = Constants.COLLECTOR_MK1_GEN.multiply(ProjectEXUtils.getBonus(tier));
        tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("pe.emc.maxgenrate_tooltip")
                + TextFormatting.BLUE + EMCFormat.format(r) + " " + rate
        );
        tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("pe.emc.maxstorage_tooltip")
                + TextFormatting.BLUE + EMCFormat.format(s) + " " + unit
        );
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        CollectorMK1Tile tile = ((CollectorMK1Tile) world.getTileEntity(pos));
        ItemStack charging = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP).getStackInSlot(CollectorMK1Tile.UPGRADING_SLOT);
        if (!charging.isEmpty()) {
            if (charging.getItem() instanceof IItemEmc) {
                IItemEmc itemEmc = ((IItemEmc) charging.getItem());
                BigInteger max = itemEmc.getMaximumEMC(charging), current = itemEmc.getStoredEMC(charging);
                return MathUtils.scaleToRedstone(current, max);
            } else {
                BigInteger needed = tile.getEmcToNextGoal(), current = tile.getStoredEmc();
                return MathUtils.scaleToRedstone(current, needed);
            }
        } else
            return MathUtils.scaleToRedstone(tile.getStoredEmc(), tile.getMaximumEmc());
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity ent = world.getTileEntity(pos);
        if (ent != null) {
            IItemHandler handler = ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            for (int i = 0; i < handler.getSlots(); i++)
                if (i != CollectorMK1Tile.LOCK_SLOT && !handler.getStackInSlot(i).isEmpty())
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
        }
        super.breakBlock(world, pos, state);
    }
}
