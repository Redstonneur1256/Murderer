package fr.redstonneur1256.murderer.utils;

import fr.redstonneur1256.murderer.MurdererPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class AbilityCoolDownTask extends BukkitRunnable {

    private MurdererPlugin plugin;
    private int timer;
    private Consumer<Integer> valueChanged;

    public AbilityCoolDownTask(MurdererPlugin plugin, int time, Consumer<Integer> valueChanged) {
        this.plugin = plugin;
        this.timer = time;
        this.valueChanged = valueChanged;
    }

    public BukkitTask start() {
        return runTaskTimer(plugin, 20, 20);
    }

    @Override
    public void run() {
        timer--;
        if(timer <= 0) {
            cancel();
        }

        valueChanged.accept(timer);
    }

}
