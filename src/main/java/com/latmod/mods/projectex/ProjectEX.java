package com.latmod.mods.projectex;

import com.latmod.mods.projectex.gui.ProjectEXGuiHandler;
import com.latmod.mods.projectex.item.ProjectEXItems;
import com.latmod.mods.projectex.net.ProjectEXNetHandler;
import com.latmod.mods.projectex.tile.AlchemyTableRecipes;
import com.latmod.mods.projectex.tile.TilePowerFlower;
import moze_intel.projecte.PECore;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = ProjectEX.MOD_ID,
        name = ProjectEX.MOD_NAME,
        version = Tags.VERSION,
        dependencies = "required-after:projecte@[2.0.0,)"
)
public class ProjectEX {
    public static final String MOD_ID = "projectex";
    public static final String MOD_NAME = "Project EX";

    public static final CreativeTabs TAB = new CreativeTabs(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ProjectEXItems.PERSONAL_LINK);
        }
    };

    @Mod.Instance(MOD_ID)
    public static ProjectEX INSTANCE;

    @SidedProxy(serverSide = "com.latmod.mods.projectex.ProjectEXCommon", clientSide = "com.latmod.mods.projectex.client.ProjectEXClient")
    public static ProjectEXCommon PROXY;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        ProjectEXNetHandler.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ProjectEXGuiHandler());
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
            ProjectEXKeyBindings.init();

        if (ProjectEXConfig.general.blacklist_power_flower_from_watch)
            FMLInterModComms.sendMessage(PECore.MODID, "timewatchblacklist", TilePowerFlower.class.getName());

        AlchemyTableRecipes.INSTANCE.addDefaultRecipes();
    }
}