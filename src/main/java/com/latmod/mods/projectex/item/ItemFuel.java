package com.latmod.mods.projectex.item;


import com.latmod.mods.projectex.block.EnumFuel;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemFuel extends Item {
    public ItemFuel() {
        setHasSubtypes(true);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return getTranslationKey() + "." + EnumFuel.byMeta(stack.getMetadata()).getName();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (EnumFuel fuel : EnumFuel.VALUES) {
                items.add(new ItemStack(this, 1, fuel.ordinal()));
            }
        }
    }

    public int getItemBurnTime(ItemStack stack) {
        Item f = ObjHandler.fuels;
        return (int) Math.min(Integer.MAX_VALUE, Math.pow(4, stack.getItemDamage() + 1) * f.getItemBurnTime(new ItemStack(f, 1, 2)));
    }
}
