package com.latmod.mods.projectex;

import moze_intel.projecte.utils.Constants;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ProjectEX.MOD_ID)
@Config(modid = ProjectEX.MOD_ID, category = "")
public class ProjectEXConfig {
    @Config.LangKey("stat.generalButton")
    public static final General general = new General();

    @Config.RequiresMcRestart
    public static final ItemsConfig items = new ItemsConfig();

    public static final Tiers tiers = new Tiers();

    public static void sync() {
        ConfigManager.sync(ProjectEX.MOD_ID, Config.Type.INSTANCE);
        general.stoneTableItemList = null;
        general.stoneTableCache = null;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ProjectEX.MOD_ID))
            sync();
    }

    private static class ItemKey {
        private Item item;
        private int metadata;

        @Override
        public int hashCode() {
            return item.hashCode() * 31 + metadata;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ItemKey) {
                ItemKey key = (ItemKey) obj;
                return item == key.item && metadata == key.metadata;
            }

            return false;
        }

        @Override
        public String toString() {
            return item.getRegistryName() + "@" + metadata;
        }
    }

    public static class General {
        @Config.Comment("With this enabled, Power Flowers will not be affected by Watch of Flowing Time.")
        public boolean blacklist_power_flower_from_watch = false;

        @Config.Comment("If set to false, it will only copy items with EMC value.")
        public boolean final_star_copy_any_item = false;

        @Config.Comment("If set to false, it will remove item NBT.")
        public boolean final_star_copy_nbt = false;

        @Config.Comment("Set to 0 to completely disable it's copying.")
        @Config.RangeInt(min = 0)
        public int final_star_update_interval = 10;

        @Config.Comment({
                "Max item that will be displayed.",
                "Reduce this if you are having problems with auto-crafting or similar things."
        })
        @Config.RangeInt(min = 1)
        public int emc_link_max_out = 2000000000;

        @Config.Comment("The whitelist for Stone Table will be ignored unless this is set to true.")
        public boolean enable_stone_table_whitelist = true;

        public String[] stone_table_whitelist = {
                "oredict:ingot",
                "oredict:gem",
                "oredict:dust",
                "oredict:nugget",
                "oredict:block",
                "oredict:ore",

                "oredict:stone",
                "oredict:cobblestone",
                "oredict:sand",
                "oredict:dirt",
                "oredict:gravel",
                "oredict:obsidian",
                "oredict:netherrack",
                "oredict:endstone",

                "minecraft:coal@*",
                "minecraft:flint",
                "minecraft:clay_ball",

                "oredict:dye",
                "oredict:string",
                "oredict:leather",
                "oredict:sugercane",
                "oredict:feather",
                "oredict:gunpowder",
                "minecraft:wheat_seeds",

                "oredict:logWood",
                "oredict:treeSapling",
                "oredict:blockGlassColorless",

                "oredict:enderpearl",
                "minecraft:blaze_rod",
                "minecraft:ghast_tear",
        };

        private HashSet<ItemKey> stoneTableItemList = null;
        private HashMap<ItemKey, Boolean> stoneTableCache = null;

        public boolean isStoneTableWhitelisted(ItemStack stack) {
            if (!enable_stone_table_whitelist)
                return true;

            if (stoneTableItemList == null) {
                stoneTableItemList = new HashSet<>();
                HashMap<String, HashSet<ItemKey>> oreDict = new HashMap<>();

                for (String s : OreDictionary.getOreNames()) {
                    HashSet<ItemKey> set = new HashSet<>();

                    for (ItemStack stack1 : OreDictionary.getOres(s)) {
                        Item item = stack1.getItem();
                        ItemKey key = new ItemKey();
                        key.item = item;
                        key.metadata = stack1.getMetadata();
                        set.add(key);
                    }

                    oreDict.put(s, set);
                }

                for (String s : general.stone_table_whitelist) {
                    String[] s1 = s.trim().split("@", 2);

                    if (s1[0].isEmpty())
                        continue;

                    if (s1.length == 1 && s1[0].startsWith("oredict:")) {
                        String s2 = s1[0].substring(8);

                        if (s2.isEmpty())
                            continue;

                        for (Map.Entry<String, HashSet<ItemKey>> entry : oreDict.entrySet())
                            if (!entry.getValue().isEmpty() && entry.getKey().startsWith(s2))
                                stoneTableItemList.addAll(entry.getValue());
                    } else {
                        Item item = Item.getByNameOrId(s1[0]);

                        if (item != null && item != Items.AIR) {
                            ItemKey key = new ItemKey();
                            key.item = item;
                            key.metadata = s1.length == 1 ? 0 : s1[1].equals("*") ? OreDictionary.WILDCARD_VALUE : Integer.parseInt(s1[1]);
                            stoneTableItemList.add(key);
                        }
                    }
                }
            }

            if (stoneTableCache == null)
                stoneTableCache = new HashMap<>();

            ItemKey key = new ItemKey();
            key.item = stack.getItem();
            key.metadata = stack.getMetadata();
            Boolean b = stoneTableCache.get(key);

            if (b == null) {
                b = false;

                for (ItemKey key1 : stoneTableItemList)
                    if (key.item == key1.item && (key.metadata == key1.metadata || key1.metadata == OreDictionary.WILDCARD_VALUE)) {
                        b = true;
                        break;
                    }

                stoneTableCache.put(key, b);
            }

            return b;
        }
    }

    public static class Tiers {
        public final BlockTier basic = new BlockTier(1);
        public final BlockTier dark = new BlockTier(2);
        public final BlockTier red = new BlockTier(3);
        public final BlockTier magenta = new BlockTier(4);
        public final BlockTier pink = new BlockTier(5);
        public final BlockTier purple = new BlockTier(6);
        public final BlockTier violet = new BlockTier(7);
        public final BlockTier blue = new BlockTier(8);
        public final BlockTier cyan = new BlockTier(9);
        public final BlockTier green = new BlockTier(10);
        public final BlockTier lime = new BlockTier(11);
        public final BlockTier yellow = new BlockTier(12);
        public final BlockTier orange = new BlockTier(13);
        public final BlockTier white = new BlockTier(14);
        public final BlockTier fading = new BlockTier(15);
        public final BlockTier final_tier = new BlockTier(1000000000000D, 1000000000000D, Double.MAX_VALUE);
    }

    public static class BlockTier {
        @Config.LangKey("projectex.tiers.collector_output")
        @Config.RangeDouble(min = 0D)
        public double collector_output;

        @Config.LangKey("projectex.tiers.relay_bonus")
        @Config.RangeDouble(min = 0D)
        public double relay_bonus;

        @Config.LangKey("projectex.tiers.relay_transfer")
        @Config.RangeDouble(min = 1D)
        public double relay_transfer;

        public BlockTier(int level) {
            BigInteger t = ProjectEXUtils.getBonus(level);
            collector_output = Constants.COLLECTOR_MK1_GEN.multiply(t).doubleValue();
            relay_bonus = t.doubleValue();
            relay_transfer = Constants.RELAY_MK1_OUTPUT.multiply(t).doubleValue();
        }

        public BlockTier(double col, double rbs, double rtf) {
            collector_output = col;
            relay_bonus = rbs;
            relay_transfer = rtf;
        }

        public BigDecimal getCo() {
            return BigDecimal.valueOf(collector_output);
        }

        public BigDecimal getRb() {
            return BigDecimal.valueOf(relay_bonus);
        }

        public BigDecimal getRt() {
            return BigDecimal.valueOf(relay_transfer);
        }

        public BigDecimal powerFlowerOutput() {
            return getCo().multiply(BigDecimal.valueOf(18)).add(getRb().multiply(BigDecimal.valueOf(30)));
        }
    }

    public static class ItemsConfig {
        @Config.LangKey("tile.projectex.personal_link.name")
        public boolean link = true;

        @Config.LangKey("item.projectex.knowledge_sharing_book.name")
        public boolean knowledge_sharing_book = true;

        public boolean stars = true;

        @Config.LangKey("item.projectex.final_star.name")
        public boolean final_star = true;

        public boolean collectors = true;
        public boolean relays = true;
        public boolean power_flowers = true;
        public boolean tome = true;

        @Config.LangKey("tile.projectex.stone_table.name")
        public boolean stone_table = true;

        @Config.LangKey("item.projectex.arcane_tablet.name")
        public boolean arcane_tablet = true;

        @Config.LangKey("item.projectex.matter.clay.name")
        public boolean clay_matter = true;

        @Config.LangKey("tile.projectex.alchemy_table.name")
        public boolean alchemy_table = true;
    }
}