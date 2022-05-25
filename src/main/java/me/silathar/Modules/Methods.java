package me.silathar.Modules;

import me.silathar.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public class Methods implements Listener {

    private Main plugin = Main.getPlugin(Main.class);

    public void messageAllPlayers(String message) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(ChatColor.GRAY + message);
        }
    }

    public boolean allowedMobs(Entity entity) {
        String mobName = entity.getName();

        //OVERWORLD
        if (mobName.equals("Skeleton")) {
            return true;
        } else if (mobName.equals("Skeleton Horse")) {
            return true;
        } else if (mobName.equals("Zombie")) {
            return true;
        } else if (mobName.equals("Creeper")) {
            return true;
        } else if (mobName.equals("Spider")) {
            return true;
        } else if (mobName.equals("Enderman")) {
            return true;
        } else if (mobName.equals("Polar Bear")) {
            return true;
        } else if (mobName.equals("Phantom")) {
            return true;
        } else if (mobName.equals("Witch")) {
            return true;
        } else if (mobName.equals("Slime")) {
            return true;
        } else if (mobName.equals("Silverfish")) {
            return true;
        } else if (mobName.equals("Pillager")) {
            return true;
        } else if (mobName.equals("Stray")) {
            return true;
        } else if (mobName.equals("Drowned")) {
            return true;
        } else if (mobName.equals("Chicken Jockey")) {
            return true;
        } else if (mobName.equals("Elder Guardian")) {
            return true;
        } else if (mobName.equals("Chicken Jockey")) {
            return true;
        } else if (mobName.equals("Evoker")) {
            return true;
        } else if (mobName.equals("Guardian")) {
            return true;
        } else if (mobName.equals("Husk")) {
            return true;
        } else if (mobName.equals("Magma Cube")) {
            return true;
        } else if (mobName.equals("Evoker")) {
            return true;
        } else if (mobName.equals("Ravager")) {
            return true;
        } else if (mobName.equals("Skeleton Horseman")) {
            return true;
        } else if (mobName.equals("Spider Jockey")) {
            return true;
        } else if (mobName.equals("Vindicator")) {
            return true;
        } else if (mobName.equals("Zombiefied Villager")) {
            return true;
        } else if (mobName.equals("Zombie Villager")) {
            return true;
        //NETHER CREATURES
        } else if (mobName.equals("Piglin")) {
            return true;
        } else if (mobName.equals("Piglin Brute")) {
            return true;
        } else if (mobName.equals("Hoglin")) {
            return true;
        } else if (mobName.equals("Wither Skeleton")) {
            return true;
        } else if (mobName.equals("Vex")) {
            return true;
        } else if (mobName.equals("Endermite")) {
            return true;
        } else if (mobName.equals("Ghast")) {
            return true;
        } else if (mobName.equals("Blaze")) {
            return true;
        } else if (mobName.equals("Zombified Piglin")) {
            return true;
        } else if (mobName.equals("Zoglin")) {
            return true;
        } else if (mobName.equals("Ghast")) {
            return true;
        //END CREATURES
        } else if (mobName.equals("Shulker")) {
            return true;
        } else if (mobName.equals("End Dragon")) {
            return true;
        } else if (mobName.equals("Ender Dragon")) {
            return true;
        } else {
            return false;
        }
    }

}
