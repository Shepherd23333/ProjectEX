package com.latmod.mods.projectex.gui.container;

import com.latmod.mods.projectex.tile.collectors.TileCollectorMKX;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class ContainerCollectorMKX extends CollectorMK1Container {
    public ContainerCollectorMKX(EntityPlayer player, TileCollectorMKX collector) {
        super(player.inventory, collector);
    }

    @Override
    public void initSlots(InventoryPlayer invPlayer) {
        IItemHandler aux = tile.getAux(), main = tile.getInput();

        //Klein Star Slot
        this.addSlotToContainer(new ValidatedSlot(aux, TileCollectorMKX.UPGRADING_SLOT, 158, 58, SlotPredicates.COLLECTOR_INV));

        int counter = main.getSlots() - 1;
        //Fuel Upgrade Slot
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                this.addSlotToContainer(new ValidatedSlot(main, counter--, 18 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV));

        //Upgrade Result
        this.addSlotToContainer(new ValidatedSlot(aux, TileCollectorMKX.UPGRADE_SLOT, 158, 13, SlotPredicates.COLLECTOR_INV));

        //Upgrade Target
        this.addSlotToContainer(new SlotGhost(aux, TileCollectorMKX.LOCK_SLOT, 187, 36, SlotPredicates.COLLECTOR_LOCK));

        //Player inventory
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 30 + j * 18, 84 + i * 18));

        //Player hotbar
        for (int i = 0; i < 9; i++)
            this.addSlotToContainer(new Slot(invPlayer, i, 30 + i * 18, 142));
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        Slot slot = this.getSlot(slotIndex);

        if (slot == null || !slot.getHasStack())
            return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        ItemStack newStack = stack.copy();

        if (slotIndex <= 18) {
            if (!this.mergeItemStack(stack, 19, 54, false))
                return ItemStack.EMPTY;
        } else if (slotIndex <= 54) {
            if (!FuelMapper.isStackFuel(stack) || FuelMapper.isStackMaxFuel(stack) || !this.mergeItemStack(stack, 1, 16, false))
                return ItemStack.EMPTY;
        } else
            return ItemStack.EMPTY;

        if (stack.isEmpty())
            slot.putStack(ItemStack.EMPTY);
        else
            slot.onSlotChanged();

        return slot.onTake(player, stack);
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player) {
        return player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
    }
}
