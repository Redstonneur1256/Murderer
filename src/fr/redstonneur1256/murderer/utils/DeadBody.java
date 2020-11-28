package fr.redstonneur1256.murderer.utils;

import com.mojang.authlib.GameProfile;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class DeadBody {

    private Location location;
    private EntityPlayer fakePlayer;
    private Inventory inventory;
    private ArmorStand display;
    private boolean reported;

    public DeadBody(Location location, Player player, ItemStack[] inventory) {
        UUID uuid = UUID.randomUUID();

        GameProfile realProfile = ((CraftPlayer) player).getProfile();

        GameProfile profile = new GameProfile(uuid, player.getName());
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();

        profile.getProperties().putAll(realProfile.getProperties());

        this.location = location;
        this.fakePlayer = new EntityPlayer(MinecraftServer.getServer(), world, profile, new PlayerInteractManager(world));
        this.inventory = Bukkit.createInventory(fakePlayer.getBukkitEntity(), 6 * 9, ChatColor.AQUA + player.getName() + "'s inventory");

        this.fakePlayer.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
        this.inventory.setContents(inventory);
    }

    public void create() {
        display = location.getWorld().spawn(location.clone().subtract(0, 1, 0), ArmorStand.class);
        display.setGravity(false);
        display.setCustomNameVisible(true);
        display.setCustomName(ChatColor.RED + fakePlayer.getName() + "'s body");
        display.setVisible(false);
    }

    public void destroy() {
        display.remove();

        ItemStack[] content = inventory.getContents();
        Utils.dropItems(content, location);

        // Clear inventory if someone has it open at same time
        Arrays.fill(content, null);
        inventory.setContents(content);
    }

    public void show(JavaPlugin plugin, Player player) {
        hide(player);

        if(!player.getWorld().getUID().equals(location.getWorld().getUID())) {
            return;
        }

        Location loc = player.getLocation();
        loc.setY(0);

        //WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        BlockPosition blockPos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        //PacketPlayOutBlockChange blockChange1 = new PacketPlayOutBlockChange(world, blockPos);
        //blockChange1.block = CraftMagicNumbers.getBlock(Material.BED).fromLegacyData(0);
        //PacketPlayOutBlockChange blockChange2 = new PacketPlayOutBlockChange(world, blockPos.a(0, 0, 1));
        //blockChange2.block = CraftMagicNumbers.getBlock(Material.BED).fromLegacyData(2);

        Packet<?> addListPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, fakePlayer);
        Packet<?> spawnPacket = new PacketPlayOutNamedEntitySpawn(fakePlayer);
        Packet<?> bedPacket = new PacketPlayOutBed(fakePlayer, blockPos);
        Packet<?> teleportPacket = new PacketPlayOutEntityTeleport(fakePlayer);

        Packet<?> removeListPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, fakePlayer);

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        ((CraftPlayer) player).sendBlockChange(loc, Material.BED_BLOCK, (byte) 0);
        ((CraftPlayer) player).sendBlockChange(loc.add(0, 0, 1), Material.BED_BLOCK, (byte) 2);

        connection.sendPacket(addListPacket);
        connection.sendPacket(spawnPacket);
        connection.sendPacket(bedPacket);
        connection.sendPacket(teleportPacket);

        //Bukkit.getScheduler().runTaskLater(plugin, () -> connection.sendPacket(removeListPacket), 20 * 5);
    }

    public void hide(Player player) {
        Packet<?> packetRemove = new PacketPlayOutEntityDestroy(fakePlayer.getId());
        Packet<?> removeListPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, fakePlayer);


        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        connection.sendPacket(packetRemove);
        connection.sendPacket(removeListPacket);
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    public void report() {
        if(reported) {
            return;
        }
        reported = true;

        String message = ChatColor.GOLD + "A body was reported";
        String coordinates = location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();

        Bukkit.broadcastMessage(message + " " + coordinates);
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(message, ChatColor.GOLD + coordinates, 20, 20, 20);
        }
    }

    public ArmorStand getDisplay() {
        return display;
    }

    public boolean hasBeenReported() {
        return reported;
    }

}
