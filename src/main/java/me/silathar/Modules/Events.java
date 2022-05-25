package me.silathar.Modules;

import me.silathar.Main;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class Events implements Listener {
    private Main plugin = Main.getPlugin(Main.class);
    Methods methods = new Methods();

    @EventHandler
    public void mobSpawn(CreatureSpawnEvent event) {
        LivingEntity mob = event.getEntity();

        if (methods.allowedMobs(mob)) {
            if (!mob.fromMobSpawner()) {
                Main.mobs.put(mob, new EntityClass(mob));
                EntityClass entityMob = Main.mobs.get(mob);

                //event.getEntity().getWorld().getSpawnLocation()
            }
        }
    }

    @EventHandler
    public void hurtEntity(EntityDamageByEntityEvent event) {
        //Player Damage to Player
        if (event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof Player) {
                Player dmgPlayer = (Player) event.getDamager();
                PlayerClass dmgClass = Main.players.get(dmgPlayer);

                Player hurtPlayer = (Player) event.getEntity();
                PlayerClass hurtClass = Main.players.get(hurtPlayer);

                if (hurtClass.cancelDamageCheck(hurtPlayer, dmgPlayer) == true) {
                    event.setCancelled(true);
                } else {
                    double Damage = dmgClass.CalculateMobDamage(false, 0, 0) * dmgPlayer.getAttackCooldown();
                    hurtPlayer.damage(hurtClass.damageCalculation(hurtPlayer, Damage));
                }

            }
        }

        //Player Melee To Mob
        if (event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof LivingEntity) {
                Player player = (Player) event.getDamager();
                LivingEntity entity = (LivingEntity) event.getEntity();
                PlayerClass playerClass = Main.players.get(player);

                double Damage = playerClass.CalculateMobDamage(false, 0, 0) * player.getAttackCooldown();
                event.setDamage(Damage);
            }
        }

        //Mob Melee to Player
        if (event.getDamager() instanceof LivingEntity) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                PlayerClass playerClass = Main.players.get(player);

                LivingEntity mob = (LivingEntity) event.getDamager();
                EntityClass entityMob = Main.mobs.get(mob);

                boolean isAttackBlocked = playerClass.cancelDamageCheck(player, mob);

                if (isAttackBlocked) {
                    event.setCancelled(true);
                } else {
                    double mobDamage = event.getDamage()+(entityMob.mobLevel/2);
                    player.damage(playerClass.damageCalculation(player, mobDamage));
                }

            }
        }

        //Projectile Damage
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            //Player To Mob
            if (projectile.getShooter() instanceof Player) {
                Player player = (Player) projectile.getShooter();
                PlayerClass playerClass = Main.players.get(player);

                LivingEntity mob = (LivingEntity) event.getEntity();
                EntityClass entityMob = Main.mobs.get(mob);

                ItemStack mainHand = player.getInventory().getItemInMainHand();
                int MIN_DMG = playerClass.grabLore(player, mainHand, "MIN_DMG");
                int MAX_DMG = playerClass.grabLore(player, mainHand, "MAX_DMG");

                double Damage = playerClass.CalculateMobDamage(true, MIN_DMG, MAX_DMG);
                event.setDamage(Damage);
            //Mob To Player
            } else if (projectile.getShooter() instanceof LivingEntity) {
                if (event.getEntity() instanceof Player) {
                    LivingEntity shooter = (LivingEntity) projectile.getShooter();
                    EntityClass entityMob = Main.mobs.get(shooter);

                    Player player = (Player) event.getEntity();
                    PlayerClass playerClass = Main.players.get(player);

                    if (playerClass.cancelDamageCheck(player, shooter) == true) {
                        event.setCancelled(true);
                    } else {
                        double mobDamage = event.getDamage()+(entityMob.mobLevel/2);
                        player.damage(playerClass.damageCalculation(player, mobDamage));
                    }

                }
            }
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity mob = (LivingEntity) event.getEntity();
            EntityClass entityMob = Main.mobs.get(mob);

            if (entityMob != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void mobDeath(EntityDeathEvent event) {
        LivingEntity mob = event.getEntity();
        EntityClass entityMob = Main.mobs.get(mob);

        if (entityMob != null && mob.getKiller() instanceof Player) {
            Player player = mob.getKiller();
            PlayerClass playerClass = Main.players.get(player);

            player.sendMessage(ChatColor.GOLD + "You have slain a " + entityMob.getMobName());

            ItemStack itemDrop = ItemClass.generateDrop(player, entityMob.mobLevel);

            //DEBUGGING
            mob.getWorld().dropItem(mob.getLocation(), itemDrop);
            Main.mobs.remove(mob);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {

        } else {
            //event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Main.players.put(player, new PlayerClass(player));
        PlayerClass playerClass = Main.players.get(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Main.players.remove(player);
    }
}
