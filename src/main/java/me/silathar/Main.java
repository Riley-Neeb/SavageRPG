package me.silathar;

import me.silathar.Modules.*;
import me.silathar.Modules.Commands;
import me.silathar.Modules.EntityClass;
import me.silathar.Modules.Events;
import me.silathar.Modules.PlayerClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class Main extends JavaPlugin implements Listener {

    public static Plugin plugin;
    public static Map<Entity, EntityClass> mobs = new HashMap<>();
    public static Map<Player, PlayerClass> players = new HashMap<>();

    public String version = "Version_003";
    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Savage MMO has Initialized");
        //Events
        getServer().getPluginManager().registerEvents(new Events(), this);
        getServer().getPluginManager().registerEvents(new Commands(), this);
        //Commands
        this.getCommand("reloadmmo").setExecutor(new Commands());
        //Settings
        plugin = this;
        mobs.clear();
        players.clear();
        loadConfig();

        //Reload Message Debug
        getServer().getConsoleSender().sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "\n\nSavage MMO has reloaded! Stuff may break. Restart if so.");
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(ChatColor.AQUA + "Savage MMO has reloaded! Stuff may break. Restart if so.");

            Main.players.put(player, new PlayerClass(player));

             //Add mobs back in when reloading
            for (World world : Bukkit.getServer().getWorlds()) {
                for (Entity otherEntity : world.getEntities()) {
                    if (otherEntity.getName().contains("Level")) {
                        LivingEntity mob = (LivingEntity) otherEntity;

                        mob.customName(null);
                        Main.mobs.put(mob, new EntityClass(mob));
                    }
                }
            }
        }

    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        loadConfig();
        reloadConfig();
    }
}
