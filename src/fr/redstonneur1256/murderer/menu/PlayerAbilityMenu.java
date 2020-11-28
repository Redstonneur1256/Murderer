package fr.redstonneur1256.murderer.menu;

import fr.redstonneur1256.murderer.MurdererPlugin;
import fr.redstonneur1256.murderer.entities.Ability;
import fr.redstonneur1256.murderer.entities.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PlayerAbilityMenu implements Listener {

    private MurdererPlugin plugin;
    private List<UUID> opened;
    private Inventory waitingInventory;
    private BukkitTask waitingAnimationTask;

    public PlayerAbilityMenu(MurdererPlugin plugin) {
        this.plugin = plugin;
        this.opened = new ArrayList<>();
        this.waitingInventory = Bukkit.createInventory(null, InventoryType.HOPPER, "Wait until murderer select ability");
    }

    public void start() {
        waitingAnimationTask = new InventoryAnimationTask(waitingInventory).start(plugin);
    }

    public void openWaiting(Player player) {
        UUID uuid = player.getUniqueId();
        if(opened.contains(uuid)) {
            return;
        }
        opened.add(uuid);

        player.openInventory(waitingInventory);
    }

    public void openMurderer(Player player) {
        UUID uuid = player.getUniqueId();
        if(opened.contains(uuid)) {
            return;
        }
        opened.add(uuid);

        Inventory inventory = Bukkit.createInventory(null, 3 * 9,
                ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "Select your ability:");

        for(Ability ability : plugin.getConfiguration().abilities) {
            inventory.setItem(ability.slot, ability.item);
        }

        player.openInventory(inventory);
    }

    public void closeAll() {
        // I need to clear before closing so inventory is not opened back
        List<UUID> copy = new ArrayList<>(opened);
        opened.clear();

        for(UUID uuid : copy) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                return;
            }
            player.closeInventory();
        }

        if(waitingAnimationTask != null) {
            waitingAnimationTask.cancel();
            waitingAnimationTask = null;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(!opened.contains(player.getUniqueId())) {
            return;
        }

        if(!plugin.getMurdererUUID().equals(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        Optional<Ability> ability = plugin.getConfiguration().abilities
                .stream()
                .filter(a -> a.slot == event.getRawSlot())
                .findFirst();

        if(!ability.isPresent()) {
            event.setCancelled(true);
            return;
        }

        plugin.setAbility(ability.get());
        plugin.startCoolDown();

        plugin.setState(State.playing);

        // TODO: Process clicked item & release everyone

        closeAll();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if(opened.contains(player.getUniqueId())) {
            // Run task in same tick but later to not cause stackoverflow
            Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(inventory));
        }

    }


}
