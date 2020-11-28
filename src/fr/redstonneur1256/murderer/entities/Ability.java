package fr.redstonneur1256.murderer.entities;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class Ability {

    public final int slot;
    public final ItemStack item;
    public PotionEffect effect;
    public boolean self;

    public Ability(int slot, ItemStack item, PotionEffect effect, boolean self) {
        this.slot = slot;
        this.item = item;
        this.effect = effect;
        this.self = self;
    }

}
