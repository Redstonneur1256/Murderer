package fr.redstonneur1256.murderer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpectatorManager implements Listener {

    private static final String spectatorTeamName;

    static {
        spectatorTeamName = "spectators";
    }

    //private MurdererPlugin plugin;
    private List<UUID> spectators;
    private Scoreboard mainScoreboard;
    private Team spectatorTeam;

    public SpectatorManager() {
        //this.plugin = plugin;

        this.spectators = new ArrayList<>();

        this.mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        this.spectatorTeam = mainScoreboard.getTeam(spectatorTeamName);
        if(this.spectatorTeam == null) {
            this.spectatorTeam = mainScoreboard.registerNewTeam(spectatorTeamName);
        }
        this.spectatorTeam.setCanSeeFriendlyInvisibles(true);
        this.spectatorTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }


    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        spectatorTeam.addEntry(player.getName());

        player.setPlayerListName("§f" + player.getName());
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void removeSpectator(Player player) {
        if(!spectators.contains(player.getUniqueId())) {
            return;
        }
        spectators.remove(player.getUniqueId());

        spectatorTeam.removeEntry(player.getName());
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setScoreboard(mainScoreboard);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeSpectator(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

    }

}
