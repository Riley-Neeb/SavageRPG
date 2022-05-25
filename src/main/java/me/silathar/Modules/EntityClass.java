package me.silathar.Modules;


import me.silathar.Main;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EntityClass {
    private static Main plugin = Main.getPlugin(Main.class);

    LivingEntity entity;
    UUID uuid;
    Integer mobLevel;
    Integer addedLevels;
    double maxHP;
    double mobHP;
    String mobName;
    int timeAlive;

    public EntityClass(LivingEntity entity) {
        //NORMAL = overworld
        //NETHER = nether
        //THE_END

        this.entity = entity;
        this.uuid = entity.getUniqueId();
        this.mobLevel = generateLevel(entity);
        this.maxHP = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()+(6*mobLevel);
        this.mobHP = maxHP;
        this.mobName =  ChatColor.YELLOW + "Level [" + mobLevel + "] " + ChatColor.RED + entity.getName();
        this.timeAlive = 0;

        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
        entity.setHealth(maxHP);

        entity.customName(Component.text(""));
        entity.customName(Component.text(mobName));
        entity.setCustomNameVisible(true);
        mobLoop();
    }

    public static int generateLevel(LivingEntity entity) {
        Integer level = 1;
        int maxChance = 100;
        Random rn = new Random();
        int chance = (int) (rn.nextDouble()*maxChance);

        double maxHeight = 320.0;
        double minHeight = -64.0;

        double y = entity.getLocation().getY();
        int addedLevel = (int) Math.floor(Math.abs(y-maxHeight)/50);

        if (chance <= 5) {
            if (y <= -50) {
                return generateMinMax(40, 50);
            } else {
                return generateMinMax(30, 40);
            }

        } else if (chance > 5 && chance <= 15) {
            if (y <= -25) {
                return generateMinMax(30, 40);
            } else {
                return generateMinMax(20, 30);
            }
        } else if (chance > 15 && chance <= 20) {
            if (y <= 0) {
                return generateMinMax(20, 30);
            } else {
                return generateMinMax(10, 20);
            }

        } else if (chance > 20 && chance <= 30) {
            if (y <= 25) {
                return generateMinMax(10, 20);
            } else {
                return generateMinMax(5, 10);
            }
        } else if (chance > 30 && chance <= 100) {
            return generateMinMax(1, 5);
        }

        if (entity.getWorld().getEnvironment().equals("NORMAL")) {

        } else if (entity.getWorld().getEnvironment().equals("NETHER")) {
            level = level+generateMinMax(25,75);

        } else if (entity.getWorld().getEnvironment().equals("THE_END")) {
            level = level+generateMinMax(50,100);
        }

        level = level+addedLevel;
        return level;
    }

    public static int generateMinMax(int min, int max) {
        Random random = new Random();
        return random.nextInt(max + 1 - min) + min;
    }

    public void mobLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                timeAlive += 1;
                int mobTagRange = 15;

                entity.setCustomNameVisible(false);
                mobHP = entity.getHealth();

                if (timeAlive > 30) {
                    Random rn = new Random();
                    int chance = (int) (rn.nextDouble()*800);

                    if (chance <= 10) {
                        this.cancel();
                        entity.setHealth(0.0);
                        Main.mobs.remove(entity);
                    }
                }

                for (Entity otherEntity : entity.getNearbyEntities(mobTagRange, mobTagRange, mobTagRange)) {
                    timeAlive = 0;
                    if (otherEntity instanceof Player) {
                        entity.setCustomNameVisible(true);
                    }
                }

                if (mobHP <= 0) {
                    this.cancel();
                }


                //addedLevels = timeAlive/2400;
                //addedLevels = timeAlive;
                //mobLevel = mobLevel+addedLevels;
            }

        }.runTaskTimer(plugin, 0, 20);
    }

    public Integer getMobLevel() {
        return mobLevel;
    }

    public String getMobName() {
        return mobName;
    }
}
