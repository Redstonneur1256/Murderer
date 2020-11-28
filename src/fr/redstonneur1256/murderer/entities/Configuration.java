package fr.redstonneur1256.murderer.entities;

import fr.redstonneur1256.murderer.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Configuration {

    public boolean cancelOnLeave;
    public List<Ability> abilities;

    public void copyFrom(FileConfiguration config) {
        cancelOnLeave = config.getBoolean("cancelOnLeave");

        abilities = new ArrayList<>();

        ConfigurationSection abilities = config.getConfigurationSection("abilities");
        for(String sectionName : abilities.getKeys(false)) {
            ConfigurationSection section = abilities.getConfigurationSection(sectionName);

            int slot = section.getInt("slot");
            Material material = Material.valueOf(section.getString("item"));
            String name = section.getString("name");
            List<String> lore = section.getStringList("lore");
            PotionEffectType type = PotionEffectType.getByName(section.getString("effect"));
            int duration = section.getInt("duration") * 20;
            int amplifier = section.getInt("amplifier") - 1;
            boolean self = section.getBoolean("self");

            ItemStack item = new ItemBuilder(material)
                    .setName(ChatColor.translateAlternateColorCodes('&', name))
                    .setLore(replaceColors(lore))
                    .toItemStack();

            PotionEffect effect = new PotionEffect(type, duration, amplifier, false, false);

            Ability ability = new Ability(slot, item, effect, self);
            this.abilities.add(ability);
        }

    }

    private List<String> replaceColors(List<String> lore) {
        return lore.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
    }

}
