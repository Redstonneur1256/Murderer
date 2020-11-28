package fr.redstonneur1256.murderer.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static void dropItems(ItemStack[] items, Location location) {
        Random random = ThreadLocalRandom.current();
        World world = location.getWorld();
        if(world == null)
            return;

        for(int i = 0; i < items.length; i++) {
            ItemStack itemStack = items[i];
            if(itemStack == null || itemStack.getType() == Material.AIR || i == 9)
                continue;

            double velX = random.nextDouble() * (random.nextBoolean() ? 1 : -1) * 0.25;
            double velY = Math.abs(random.nextDouble()) * 0.25;
            double velZ = random.nextDouble() * (random.nextBoolean() ? 1 : -1) * 0.25;

            Item item = world.dropItem(location, itemStack);
            item.setVelocity(new Vector(velX, velY, velZ).multiply(0.5));
        }
    }

}
