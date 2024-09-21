package com.latmod.mods.projectex.tile;

import com.latmod.mods.projectex.ProjectEXConfig;
import com.latmod.mods.projectex.integration.PersonalEMC;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.UUID;

public class TileTransmutationInterface extends TileEntity implements IItemHandler, ITickable {
    public UUID owner = new UUID(0L, 0L);
    public String name = "";
    private boolean isDirty = false;
    private ItemStack[] stack;

    public TileTransmutationInterface() {
    }

    private ItemStack[] fetchKnowledge() {
        if (stack != null)
            return stack;
        IKnowledgeProvider provider = PersonalEMC.get(world, owner);
        return provider != null ? stack = provider.getKnowledge().toArray(new ItemStack[0]) : new ItemStack[]{};
    }

    private int getCount(@Nullable IKnowledgeProvider provider, int slot) {
        if (provider == null)
            return 0;

        BigInteger emc = provider.getEMC();
        if (emc.compareTo(BigInteger.ZERO) < 1)
            return 0;
        BigInteger value = ProjectEAPI.getEMCProxy().getValue(fetchKnowledge()[slot]);
        if (value.compareTo(BigInteger.ZERO) < 1)
            return 0;

        return emc.divide(value).min(BigInteger.valueOf(ProjectEXConfig.general.emc_link_max_out)).intValue();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        owner = nbt.getUniqueId("owner");
        name = nbt.getString("name");

        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setUniqueId("owner", owner);
        nbt.setString("name", name);

        return super.writeToNBT(nbt);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void onLoad() {
        if (world.isRemote)
            world.tickableTileEntities.remove(this);

        validate();
    }

    @Override
    public void update() {
        stack = null;

        if (isDirty) {
            isDirty = false;
            world.markChunkDirty(pos, this);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) this : super.getCapability(capability, side);
    }

    public boolean hasNoOwner() {
        return owner.getLeastSignificantBits() == 0L && owner.getMostSignificantBits() == 0L;
    }

    @Override
    public void markDirty() {
        isDirty = true;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return ProjectEAPI.getEMCProxy().hasValue(stack);
    }

    @Override
    public int getSlots() {
        return fetchKnowledge().length + 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (world.isRemote || hasNoOwner() || slot < 1 || fetchKnowledge().length < slot)
            return ItemStack.EMPTY;
        IKnowledgeProvider provider = PersonalEMC.get(world, owner);
        int count = getCount(provider, slot - 1);
        if (count < 1)
            return ItemStack.EMPTY;
        ItemStack item = stack[slot - 1].copy();
        item.setCount(count);
        return item;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (world.isRemote || slot != 0 || hasNoOwner() || !isItemValid(slot, stack) || stack.isEmpty() || stack.hasTagCompound())
            return stack;
        fetchKnowledge();

        int count = stack.getCount();
        stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
        if (count < 1)
            return stack;
        if (simulate)
            return ItemStack.EMPTY;

        IKnowledgeProvider provider = PersonalEMC.get(world, owner);
        BigInteger emc = ProjectEAPI.getEMCProxy().getSellValue(stack);
        if (provider == null)
            return stack;
        BigInteger sum = emc.multiply(BigInteger.valueOf(count));
        PersonalEMC.add(provider, sum);

        EntityPlayerMP player = world.getMinecraftServer().getPlayerList().getPlayerByUUID(owner);
        if (player != null && provider.addKnowledge(stack))
            provider.sync(player);

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        IKnowledgeProvider provider = PersonalEMC.get(world, owner);
        if (world.isRemote || hasNoOwner() || slot < 1 || fetchKnowledge().length < slot || provider == null)
            return ItemStack.EMPTY;

        amount = Math.min(amount, getCount(provider, slot - 1));
        if (amount < 1)
            return ItemStack.EMPTY;
        ItemStack item = stack[slot - 1].copy();
        item.setCount(amount);
        if (simulate)
            return item;

        BigInteger emc = ProjectEAPI.getEMCProxy().getValue(item);
        BigInteger sum = emc.multiply(BigInteger.valueOf(amount));
        PersonalEMC.remove(provider, sum);

        return item;
    }

    @Override
    public int getSlotLimit(int slot) {
        return ProjectEXConfig.general.emc_link_max_out;
    }
}
