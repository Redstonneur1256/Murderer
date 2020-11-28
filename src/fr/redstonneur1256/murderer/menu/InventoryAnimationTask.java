package fr.redstonneur1256.murderer.menu;

import fr.redstonneur1256.murderer.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;

public class InventoryAnimationTask extends BukkitRunnable {

    private static final ItemStack red = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 14)
            .setName("§f")
            .toItemStack();
    private static final ItemStack green = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 5)
            .setName("§f")
            .toItemStack();


    private Inventory inventory;
    private int position;
    private int direction;

    public InventoryAnimationTask(Inventory inventory) {
        this.inventory = inventory;
        this.position = 0;
        this.direction = 1;
    }

    public BukkitTask start(JavaPlugin plugin) {
        return runTaskTimer(plugin, 0, 2);
    }

    @Override
    public void run() {
        position += direction;
        if(position >= 4 || position <= 0) {
            direction = -direction;
        }

        ItemStack[] items = inventory.getContents();

        Arrays.fill(items, red);
        items[position] = green;

        inventory.setContents(items);
    }

}
