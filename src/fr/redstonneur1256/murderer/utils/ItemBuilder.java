package fr.redstonneur1256.murderer.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@SuppressWarnings({"deprecation", "unused"})
public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this(material, amount, 0);
    }

    public ItemBuilder(Material material, int amount, int durability) {
        this(new ItemStack(material, amount, (short) durability));
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public Material getType() {
        return item.getType();
    }

    public ItemBuilder setType(Material type) {
        item.setType(type);
        return this;
    }

    public int getAmount() {
        return item.getAmount();
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public short getDurability() {
        return item.getDurability();
    }

    public ItemBuilder setDurability(int durability) {
        item.setDurability((short) durability);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        item.addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        return item.getItemMeta().hasEnchant(enchantment);
    }

    public int getEnchantLevel(Enchantment enchantment) {
        return item.getItemMeta().getEnchantLevel(enchantment);
    }

    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(owner);
            item.setItemMeta(meta);
        }catch(Exception ignored) {
        }
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add(line);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String line, int index) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.set(index, line);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public List<String> getLore() {
        ItemMeta meta = item.getItemMeta();
        return meta.hasLore() ? meta.getLore() : Collections.emptyList();
    }

    public ItemBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(unbreakable);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLeatherColor(Color color) {
        try {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
        }catch(Exception ignored) {
        }
        return this;
    }

    public ItemBuilder setBannerStyle(Pattern... patterns) {
        return setBannerStyle(Arrays.asList(patterns));
    }

    public ItemBuilder setBannerStyle(List<Pattern> patterns) {
        try {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            meta.setPatterns(patterns);
            item.setItemMeta(meta);
        }catch(Exception ignored) {
        }
        return this;
    }

    public ItemBuilder setBookData(String title, String author, String... pages) {
        try {
            BookMeta meta = (BookMeta) item.getItemMeta();
            meta.setAuthor(author);
            meta.setTitle(title);
            meta.setPages(pages);
            item.setItemMeta(meta);
        }catch(Exception ignored) {
        }
        return this;
    }

    public ItemBuilder setBookData(String title, String author, BaseComponent[]... pages) {
        return setBookData(title, author, Arrays.asList(pages));
    }

    public ItemBuilder setBookData(String title, String author, List<BaseComponent[]> pages) {
        try {
            BookMeta meta = (BookMeta) item.getItemMeta();
            meta.setAuthor(author);
            meta.setTitle(title);
            meta.spigot().setPages(pages);
            item.setItemMeta(meta);
        }catch(Exception ignored) {
        }
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flags);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeItemFlags(ItemFlag... flags) {
        ItemMeta meta = item.getItemMeta();
        meta.removeItemFlags(flags);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setSpawnEggType(EntityType type) {
        try {
            SpawnEggMeta meta = (SpawnEggMeta) item.getItemMeta();
            meta.setSpawnedType(type);
            item.setItemMeta(meta);
        }catch(Exception ignored) {
        }
        return this;
    }

    public ItemBuilder addPotionEffect(PotionEffect effect) {
        try {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(effect, true);
            item.setItemMeta(meta);
        }catch(Exception ignored) {
        }
        return this;
    }

    public ItemBuilder removePotionEffect(PotionEffectType effect) {
        try {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.removeCustomEffect(effect);
            item.setItemMeta(meta);
        }catch(Exception ignored) {
        }
        return this;
    }

    public boolean hasPotionEffect(PotionEffectType effect) {
        try {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            return meta.hasCustomEffect(effect);
        }catch(Exception ignored) {
        }
        return false;
    }

    public ItemBuilder setPotionColor(int red, int green, int blue) {
        return setPotionColor(Color.fromRGB(red, green, blue));
    }

    public ItemBuilder setPotionColor(Color color) {
        try {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
        }catch(Exception ignored) {
        }
        return this;
    }

    public ItemStack toItemStack() {
        return item;
    }

    public ItemBuilder setLeatherColor(java.awt.Color color) {
        return setLeatherColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
    }

}