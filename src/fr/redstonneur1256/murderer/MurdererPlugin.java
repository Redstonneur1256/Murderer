package fr.redstonneur1256.murderer;

import fr.redstonneur1256.murderer.commands.MurdererCommand;
import fr.redstonneur1256.murderer.entities.Ability;
import fr.redstonneur1256.murderer.entities.Configuration;
import fr.redstonneur1256.murderer.entities.State;
import fr.redstonneur1256.murderer.menu.PlayerAbilityMenu;
import fr.redstonneur1256.murderer.utils.AbilityCoolDownTask;
import fr.redstonneur1256.murderer.utils.DeadBody;
import fr.redstonneur1256.murderer.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class MurdererPlugin extends JavaPlugin {

    private Configuration configuration;
    private SpectatorManager spectators;
    private PlayerAbilityMenu menu;
    private List<DeadBody> bodies;
    private State state;
    private UUID murdererUUID;
    private Ability ability;
    private long abilityCoolDown;
    private BukkitTask coolDownTask;

    public MurdererPlugin() {

    }

    @Override
    public void onEnable() {
        configuration = new Configuration();
        spectators = new SpectatorManager();
        menu = new PlayerAbilityMenu(this);
        bodies = new ArrayList<>();

        saveDefaultConfig();
        reloadConfig();
        reset();

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new MurderListeners(this), this);
        pluginManager.registerEvents(menu, this);
        pluginManager.registerEvents(spectators, this);

        getCommand("murderer").setExecutor(new MurdererCommand(this));

        for(World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("announceAdvancements", "false");
            world.setGameRuleValue("showDeathMessages", "false");
        }
    }

    @Override
    public void onDisable() {
        bodies.forEach(DeadBody::destroy);
        for(Player player : Bukkit.getOnlinePlayers()) {
            for(DeadBody body : bodies) {
                body.hide(player);
            }
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        configuration.copyFrom(getConfig());
    }

    public void useAbility() {
        if(abilityCoolDown > 0 || ability == null) {
            return;
        }

        if(ability.self) {
            Player player = Bukkit.getPlayer(murdererUUID);
            if(player != null) {
                player.addPotionEffect(ability.effect);
            }
        }else {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(spectators.isSpectator(player) || player.getUniqueId().equals(murdererUUID)) {
                    continue;
                }
                player.addPotionEffect(ability.effect);
            }
        }

        startCoolDown();
    }

    public void startCoolDown() {
        AbilityCoolDownTask task = new AbilityCoolDownTask(this, 60, time -> {
            abilityCoolDown = time;
            Player murderer = Bukkit.getPlayer(murdererUUID);
            if(murderer == null)
                return;

            PlayerInventory inventory = murderer.getInventory();
            if(time == 0) {
                inventory.setItem(9, new ItemBuilder(Material.STONE_BUTTON)
                        .setName(ChatColor.GREEN + "Click to use your ability").toItemStack());
            }else {
                inventory.setItem(9, new ItemBuilder(Material.STAINED_GLASS_PANE, time, 14)
                        .setName(ChatColor.RED + "Recharging " + time).toItemStack());
            }

        });
        coolDownTask = task.start();
    }

    public void reset() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            for(DeadBody body : bodies) {
                body.hide(player);
            }
            if(spectators.isSpectator(player)) {
                spectators.removeSpectator(player);
            }

            for(PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            player.getInventory().setItem(9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15)
                    .setName(ChatColor.RED + "Reserved slot").toItemStack());

        }

        bodies.forEach(DeadBody::destroy);
        bodies.clear();

        if(coolDownTask != null && !coolDownTask.isCancelled()) {
            coolDownTask.cancel();
        }

        state = State.lobby;

    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public SpectatorManager getSpectators() {
        return spectators;
    }

    public PlayerAbilityMenu getMenu() {
        return menu;
    }

    public boolean isState(State state) {
        return this.state == state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public UUID getMurdererUUID() {
        return murdererUUID;
    }

    public void setMurderer(Player murderer) {
        this.murdererUUID = murderer.getUniqueId();
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    public void addBody(DeadBody body) {
        bodies.add(body);
        body.create();
    }

    public List<DeadBody> getBodies() {
        return bodies;
    }

    public void setInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        if(player.getUniqueId().equals(murdererUUID)) {

            if(coolDownTask != null && coolDownTask.isCancelled()) {
                inventory.setItem(9, new ItemBuilder(Material.STONE_BUTTON)
                        .setName(ChatColor.GREEN + "Click to use your ability").toItemStack());
            }

        }else {
            inventory.setItem(9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15)
                    .setName(ChatColor.RED + "Reserved slot").toItemStack());
        }
    }
}
