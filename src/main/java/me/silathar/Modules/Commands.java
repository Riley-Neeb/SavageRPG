package me.silathar.Modules;

import me.silathar.Main;
import me.silathar.Modules.EntityClass;
import me.silathar.Modules.Methods;
import me.silathar.Modules.PlayerClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Commands implements Listener, CommandExecutor {
    private Main plugin = Main.getPlugin(Main.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("reloadmmo")) {
                if (player.isOp() || player.getName().equals("Fatul")) {
                    plugin.mobs.clear();
                    plugin.players.clear();
                    plugin.loadConfig();

                    //Reload Message Debug
                    plugin.getServer().getConsoleSender().sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "\n\nSavage MMO has reloaded! Stuff may break. Restart if so.");
                    for (Player playersOnline : Bukkit.getServer().getOnlinePlayers()) {
                        playersOnline.sendMessage(ChatColor.AQUA + "Savage MMO has reloaded! Stuff may break. Restart if so.");

                        Main.players.put(playersOnline, new PlayerClass(playersOnline));

                        //Add mobs back in when reloading
                        for (World world : Bukkit.getServer().getWorlds()) {
                            for (Entity otherEntity : world.getEntities()) {
                                if (otherEntity.getName().contains("Level")) {
                                    LivingEntity mob = (LivingEntity) otherEntity;
                                    EntityClass entityMob = Main.mobs.get(mob);

                                    mob.customName(null);

                                    if (entityMob == null) {
                                        Main.mobs.put(mob, new EntityClass(mob));
                                    }
                                }
                            }
                        }
                    }
                }
            }




        } else {
            return false;
        }

        return true;
    }



}