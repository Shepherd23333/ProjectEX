package com.latmod.mods.projectex.gui;

import com.latmod.mods.projectex.gui.container.ContainerCollectorMKX;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.math.BigInteger;

public class GuiCollectorMKX extends GuiContainer {
    private static final ResourceLocation texture = Constants.gui;
    public final ContainerCollectorMKX container;

    public GuiCollectorMKX(ContainerCollectorMKX c) {
        super(c);
        container = c;
        this.xSize = 218;
        this.ySize = 165;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int var1, int var2) {
        this.fontRenderer.drawString(EMCFormat.format(container.emc), 91, 32, 4210752);

        BigInteger kleinCharge = container.kleinEmc;
        if (kleinCharge.compareTo(BigInteger.ZERO) > 0)
            this.fontRenderer.drawString(EMCFormat.format(kleinCharge), 91, 44, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GlStateManager.color(1, 1, 1, 1);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        //Light Level. Max is 12
        int progress = (int) (container.sunLevel * 12.0 / 16);

        this.drawTexturedModalRect(x + 160, y + 49 - progress, 220, 13 - progress, 12, progress);

        //EMC storage. Max is 48
        this.drawTexturedModalRect(x + 98, y + 18, 0, 166, (container.emc.divide(container.tile.getMaximumEmc()).intValue() * 48), 10);

        //Klein Star Charge Progress. Max is 48
        progress = (int) (container.kleinChargeProgress * 48);
        this.drawTexturedModalRect(x + 98, y + 58, 0, 166, progress, 10);

        //Fuel Progress. Max is 24.
        progress = (int) (container.fuelProgress * 24);
        this.drawTexturedModalRect(x + 172, y + 55 - progress, 219, 38 - progress, 10, progress + 1);
    }
}
