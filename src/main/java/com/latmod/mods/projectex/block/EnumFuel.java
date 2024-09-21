package com.latmod.mods.projectex.block;

import com.latmod.mods.projectex.item.ProjectEXItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.function.Supplier;

public enum EnumFuel implements IStringSerializable, Supplier<ItemStack> {
    MAGENTA("magenta"),
    PINK("pink"),
    PURPLE("purple"),
    VIOLET("violet"),
    BLUE("blue"),
    CYAN("cyan"),
    GREEN("green"),
    LIME("lime"),
    YELLOW("yellow"),
    ORANGE("orange"),
    WHITE("white");

    public static final EnumFuel[] VALUES = values();
    private final String name;

    EnumFuel(String n) {
        name = n;
    }

    public static EnumFuel byMeta(int meta) {
        return meta < 0 || meta >= VALUES.length ? MAGENTA : VALUES[meta];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack get() {
        return new ItemStack(ProjectEXItems.FUEL, 1, ordinal());
    }
}
