package com.latmod.mods.projectex.gui.button;

import com.latmod.mods.projectex.gui.container.ContainerTableBase;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.utils.EMCFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author LatvianModder
 */
public class ButtonCreateItem extends ButtonSlot {
    public ItemStack type;

    public ButtonCreateItem(ContainerTableBase t, int id, int x, int y) {
        super(t, id, x, y);
        type = ItemStack.EMPTY;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

            if (!type.isEmpty()) {
                GlStateManager.color(1F, 1F, 1F, 1F);
                GlStateManager.enableDepth();
                RenderHelper.enableGUIStandardItemLighting();
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
                RenderItem renderItem = mc.getRenderItem();
                renderItem.renderItemAndEffectIntoGUI(mc.player, type, x, y);
                renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, type, x, y, null);
                RenderHelper.disableStandardItemLighting();

                BigDecimal emc = new BigDecimal(ProjectEAPI.getEMCProxy().getValue(type));
                BigDecimal stored = new BigDecimal(table.playerData.getEMC());
                String s;

                BigDecimal d = stored.divide(emc, 1, RoundingMode.HALF_DOWN);

                if (d.compareTo(BigDecimal.ONE) >= 0) {
                    s = EMCFormat.format(d, EMCFormat.IgnoreShiftType.FORMAT);
                } else if (d.compareTo(BigDecimal.valueOf(0.1)) >= 0) {
                    s = d.toPlainString();
                } else {
                    s = "";
                }

                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 17, y + 12, 0);
                GlStateManager.scale(0.5F, 0.5F, 1F);
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                mc.fontRenderer.drawStringWithShadow(s, -mc.fontRenderer.getStringWidth(s), 0, 16777215);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.enableBlend();
                GlStateManager.popMatrix();
            }

            if (hovered) {
                GlStateManager.color(1F, 1F, 1F, 1F);
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.colorMask(true, true, true, false);
                drawGradientRect(x, y, x + width, y + height, -2130706433, -2130706433);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }
}