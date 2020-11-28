package fr.redstonneur1256.murderer.commands;

import fr.redstonneur1256.murderer.MurdererPlugin;
import fr.redstonneur1256.murderer.entities.State;
import fr.redstonneur1256.murderer.menu.PlayerAbilityMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MurdererCommand implements CommandExecutor {

    private MurdererPlugin plugin;

    public MurdererCommand(MurdererPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            sendHelp(sender, label);
            return false;
        }

        switch(args[0].toLowerCase()) {
            case "start":
                if(plugin.getState() != State.lobby) {
                    sender.sendMessage(ChatColor.RED + "The game as already started !");
                    return false;
                }

                Player murderer;
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

                if(args.length >= 2) {
                    murderer = Bukkit.getPlayer(args[1]);

                    if(murderer == null || !murderer.isOnline()) {
                        sender.sendMessage(ChatColor.RED + "Failed to start the game: the specified player is not online");
                        return false;
                    }

                }else {
                    Random random = ThreadLocalRandom.current();
                    murderer = players.get(random.nextInt() % players.size());
                }

                plugin.setMurderer(murderer);

                murderer.sendMessage(ChatColor.RED + "You are murderer, don't get killed");

                PlayerAbilityMenu menu = plugin.getMenu();

                menu.start();
                for(Player player : players) {
                    // Always use UUID, even if this case where is almost impossible for him to change
                    if(murderer.getUniqueId().equals(player.getUniqueId())) {
                        menu.openMurderer(player);
                    }else {
                        menu.openWaiting(player);
                    }
                }

                break;
            case "stop":
                if(plugin.getState() == State.lobby) {
                    sender.sendMessage("§cThe game is not running!");
                    return false;
                }

                plugin.reset();
                break;
            default:
                sendHelp(sender, label);
                break;
        }


        return false;
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.RED + "Invalid command usage");
        sender.sendMessage(ChatColor.RED + "/" + label + " start [murderer] - Starts the game, the murderer can be specified");
        sender.sendMessage(ChatColor.RED + "/" + label + " stop - Reset the game bodies, murderer and murderer skill");
    }

}
