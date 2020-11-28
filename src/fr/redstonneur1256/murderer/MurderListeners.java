package fr.redstonneur1256.murderer;

import fr.redstonneur1256.murderer.entities.State;
import fr.redstonneur1256.murderer.utils.DeadBody;
import fr.redstonneur1256.murderer.utils.ItemBuilder;
import fr.redstonneur1256.murderer.utils.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class MurderListeners implements Listener {

    private MurdererPlugin plugin;

    public MurderListeners(MurdererPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        if(plugin.isState(State.playing)) {

            plugin.getSpectators().addSpectator(player);

            ItemStack item = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("You are spectator").toItemStack();

            ItemStack[] contents = inventory.getContents();
            Arrays.fill(contents, item);
            inventory.setContents(contents);

        }else {
            inventory.setItem(9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15)
                    .setName(ChatColor.RED + "Reserved slot").toItemStack());
        }

        showBodies(player);

        if(plugin.isState(State.selecting)) {
            plugin.getMenu().openWaiting(player);
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PlayerInventory inventory = player.getInventory();
        inventory.setItem(9, null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        if(slot != 9) {
            return;
        }

        event.setCancelled(true);

        if(!player.getUniqueId().equals(plugin.getMurdererUUID())) {
            return;
        }

        plugin.useAbility();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        Player target = (Player) event.getEntity();
        Player killer = (Player) event.getDamager();

        if(event.getFinalDamage() < target.getHealth()) { // Not gonna die, return.
            return;
        }

        Location location = target.getLocation();

        event.setDamage(0.0);

        if(plugin.getMurdererUUID().equals(killer.getUniqueId())) {
            // The player has been killed by the murderer:

            AttributeInstance attribute = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            target.setHealth(attribute == null ? 20 : attribute.getValue());
            target.setVelocity(new Vector());

            plugin.getSpectators().addSpectator(target);

            int alive = 0;
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(plugin.getSpectators().isSpectator(player) || player.getUniqueId().equals(killer.getUniqueId())) {
                    continue;
                }
                alive++;
            }
            ItemStack[] contents = target.getInventory().getContents().clone();

            ItemStack[] inventory = new ItemStack[6 * 9];
            System.arraycopy(contents, 0, inventory, 0, contents.length);
            inventory[9] = null;

            DeadBody body = new DeadBody(location, target, inventory);
            plugin.addBody(body);
            for(Player player : Bukkit.getOnlinePlayers()) {
                body.show(plugin, player);
            }

            target.getInventory().clear();

            if(alive == 0) {
                Bukkit.broadcastMessage(ChatColor.GREEN + "The murderer have killed everyone !");
            }

        }else if(plugin.getMurdererUUID().equals(target.getUniqueId())) {
            // They have killed the murderer:

            Utils.dropItems(target.getInventory().getContents(), location);

            Bukkit.broadcastMessage(ChatColor.RED + "The murderer has been killed !\nIt was " + target.getName() + " !");

        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if(entity.getType() == EntityType.ARMOR_STAND) {
            for(DeadBody body : plugin.getBodies()) {
                if(body.getDisplay().getUniqueId().equals(entity.getUniqueId())) {

                    if(body.hasBeenReported() || player.getGameMode() == GameMode.SPECTATOR) {
                        body.openInventory(player);
                    }else {
                        body.report();
                    }

                    break;
                }
            }
        }
    }

    @EventHandler
    public void onDie(PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops();

        drops
                .stream()
                .filter(ItemStack::hasItemMeta)
                .filter(item -> item.getItemMeta().hasDisplayName())
                .filter(item -> item.getItemMeta().getDisplayName().contains("§"))
                .forEach(drops::remove);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player player = event.getPlayer();

            plugin.setInventory(player);

            showBodies(player);
        }, 1);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> showBodies(event.getPlayer()), 1);
    }

    private void showBodies(Player player) {
        for(DeadBody body : plugin.getBodies()) {
            body.show(plugin, player);
        }
    }

}
