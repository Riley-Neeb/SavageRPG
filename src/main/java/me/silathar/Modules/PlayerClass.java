package me.silathar.Modules;

import com.google.common.base.CharMatcher;
import me.silathar.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerClass {
    private Main plugin = Main.getPlugin(Main.class);

    Player player;
    UUID uuid;
    String playerName;
    Integer level;
    Integer HP;
    Integer MIN_DMG = 1;
    Integer MAX_DMG = 1;

    Integer DEFENCE = 0;; //Flat Amount
    Integer BONUS_DEFENCE = 0;; //% Amount

    Integer EVASION = 0;; //Flat Amount
    Integer BONUS_EVASION = 0;; //% Amount

    Integer ADDED_HEALTH = 0; //Flat Amount
    Integer BONUS_HEALTH = 0; //% Amount

    Integer BLOCK_CHANCE = 0;
    Integer DROP_CHANCE = 0; //Subtract (DROP_CHANCE/100) from ItemClass maxRarityChance

    float MOVE_SPEED = 0;
    double BASE_HEALTH;
    float BASE_MOVE_SPEED;
    double BASE_ATTACK_SPEED;
    Integer BONUS_ATTACK_SPEED = 0;
    double WEAPON_ATTACK_SPEED = 0;
    boolean IS_BLOCKING = false;

    public PlayerClass(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.playerName = player.getName();
        this.level = 1;
        this.DEFENCE = 0;
        this.BASE_HEALTH = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
        this.BASE_MOVE_SPEED = 0.2f;
        this.BASE_ATTACK_SPEED = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getDefaultValue();

        playerLoop();
    }

    public void playerLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MIN_DMG = 1;
                MAX_DMG = 3;

                double addedHP = BASE_HEALTH + (BASE_HEALTH * (BONUS_HEALTH/100));
                float moveSpeed = BASE_MOVE_SPEED + (BASE_MOVE_SPEED*(MOVE_SPEED/100));
                double atkSpeed = WEAPON_ATTACK_SPEED + (WEAPON_ATTACK_SPEED*(BONUS_ATTACK_SPEED/100));

                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(addedHP); //+ addedHP);
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(BASE_MOVE_SPEED); // + addedSpeed);
                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(atkSpeed); // + addedSpeed);
                player.setWalkSpeed(moveSpeed);

                player.sendMessage("WalkSpeed: " + player.getWalkSpeed() + ", Attack Speed: " + atkSpeed);

                //DEFENCE CALC
                DEFENCE = 0;
                BONUS_DEFENCE = 0;
                EVASION = 0;
                BONUS_EVASION = 0;
                ADDED_HEALTH = 0;
                BLOCK_CHANCE = 0;

                grabDefence();
                grabBonusDefence();
                grabEvasion();
                grabHealth();
                grabBonusEvade();
                grabBonusHealth();

                //ITEM CALC
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                ItemStack offHand = player.getInventory().getItemInOffHand();

                if (offHand.getType().equals(Material.SHIELD)) {
                    ItemMeta offhandMeta = offHand.getItemMeta();

                    if (offhandMeta == null || (!offHand.getItemMeta().hasLore()) ) {
                        return;
                    }

                    if (player.isBlocking() || player.isHandRaised()) {
                        IS_BLOCKING = true;
                    } else {
                        IS_BLOCKING = false;
                    }

                    BLOCK_CHANCE = grabLore(player, offHand, "BLOCK_CHANCE");
                }

                if (!mainHand.getType().equals(Material.AIR)) {
                    ItemMeta weaponMainMeta = mainHand.getItemMeta();

                    if (weaponMainMeta == null) {
                        return;
                    }

                    if (weaponMainMeta.hasLore()) {
                        WEAPON_ATTACK_SPEED = wepATTK_SPEED(player, mainHand);

                        if (mainHand.getType().equals(Material.BOW)) {
                            MIN_DMG = 1;
                            MAX_DMG = 3;
                        } else if (mainHand.getType().equals(Material.CROSSBOW)) {
                            MIN_DMG = 1;
                            MAX_DMG = 3;
                        } else {
                            MIN_DMG = grabLore(player, mainHand, "MIN_DMG");
                            MAX_DMG = grabLore(player, mainHand, "MAX_DMG");
                        }
                    }
                }

                player.sendMessage(
                        "DEF: "          + DEFENCE + ", " +
                        "BONUS_DEF: "    + BONUS_DEFENCE + ", " +
                        "EVA: "          + EVASION + ", " +
                        "BON_EVA: "      + BONUS_EVASION + ", " +
                        "HP: "           + ADDED_HEALTH + ", " +
                        "BON_HP: "       + BONUS_HEALTH + ", " +
                        "BLOCK_CHANCE: " + BLOCK_CHANCE
                );

            }

        }.runTaskTimer(plugin, 0, 20);
    }



    //Functions
    public int grabLore(Player player, ItemStack item, String type) {
        if (item == null) {
            return 0;
        }

        int value = 0;

        for (Component lore : item.getItemMeta().lore()) {
            String loreLine = decomponentalize(lore);

            if (type.equals("TIER")) {
                if (loreLine.contains("Tier")) {

                }
            } else if (type.equals("BONUS_EVASION")) {
                if (loreLine.contains("Increase Evasion")) {
                    String valueText = loreLine.replace("Increase Evasion by", "");
                    valueText = valueText.replaceAll("\\s", "");
                    valueText = valueText.replaceAll("%", "");

                    return value = Integer.parseInt(valueText);
                }
            } else if (type.equals("BONUS_DEFENCE")) {
                if (loreLine.contains("Increase Defence")) {
                    String valueText = loreLine.replace("Increase Defence by", "");
                    valueText = valueText.replaceAll("\\s", "");
                    valueText = valueText.replaceAll("%", "");

                    return value = Integer.parseInt(valueText);
                }
            } else if (type.equals("BONUS_HEALTH")) {
                if (loreLine.contains("Increase Health by")) {
                    String valueText = loreLine.replace("Increase Health by", "");
                    valueText = valueText.replaceAll("\\s", "");
                    valueText = valueText.replaceAll("%", "");

                    return value = Integer.parseInt(valueText);
                }
            } else if (type.equals("MIN_DMG")) {
                if (loreLine.contains("Damage")) {
                    String dmg = loreLine.replace("Damage:", "");

                    String[] splitDamage = dmg.split("-");
                    String min_string = splitDamage[0];
                    min_string = min_string.replaceAll("\\s", "");

                    return value = Integer.parseInt(min_string);
                }
            } else if (type.equals("MAX_DMG")) {
                if (loreLine.contains("Damage:")) {
                    String dmg = loreLine.replace("Damage:", "");

                    String[] splitDamage = dmg.split("-");
                    String max_string = splitDamage[1];
                    max_string = max_string.replaceAll("\\s", "");

                    return value = Integer.parseInt(max_string);
                }
            } else if (type.equals("DEFENCE")) {
                if (loreLine.contains("Defence:")) {
                    String valueText = loreLine.replace("Defence:", "");
                    valueText = valueText.replaceAll("\\s", "");

                    return value = Integer.parseInt(valueText);
                }
            } else if (type.equals("EVASION")) {
                if (loreLine.contains("Evasion:")) {

                    String valueText = loreLine.replace("Evasion:", "");
                    valueText = valueText.replaceAll("\\s", "");

                    return value = Integer.parseInt(valueText);
                }
            } else if (type.equals("HEALTH")) {
                if (loreLine.contains("Adds") && (loreLine.contains("Health"))) {
                    String theDigits = CharMatcher.inRange('0', '9').retainFrom(loreLine);
                    theDigits = theDigits.replaceAll("\\s", "");

                    return value = Integer.parseInt(theDigits);
                }
            } else if (type.equals("ATTACK_SPEED")) {
                if (loreLine.contains("Increase Attack Speed by")) {
                    String valueText = loreLine.replace("Increase Attack Speed by", "");
                    valueText = valueText.replaceAll("\\s", "");

                    return value = Integer.parseInt(valueText);
                }
            } else if (type.equals("BLOCK_CHANCE")) {
                if (loreLine.contains("Block Chance")) {
                    String valueText = loreLine.replace("Block Chance:", "");
                    valueText = valueText.replaceAll("\\s", "");

                    return value = Integer.parseInt(valueText);
                }
            } else if (type.equals("MOVEMENT_SPEED")) {
                if (loreLine.contains("Movement Speed")) {
                    String valueText = loreLine.replace("Increase Movement Speed by", "");
                    valueText = valueText.replaceAll("\\s", "");
                    valueText = valueText.replaceAll("%", "");

                    return value = Integer.parseInt(valueText);
                }
            } else if (type.equals("ATTACK_SPEED")) {
                if (loreLine.contains("Attack Speed")) {
                    String valueText = loreLine.replace("Increase Movement Speed by", "");
                    valueText = valueText.replaceAll("\\s", "");
                    valueText = valueText.replaceAll("%", "");

                    return value = Integer.parseInt(valueText);
                }
            } else if (type.equals("DROP_CHANCE")) {
                if (loreLine.contains("Drop Chance")) {
                    String valueText = loreLine.replace("Increase Drop Chance by", "");
                    valueText = valueText.replaceAll("\\s", "");
                    valueText = valueText.replaceAll("%", "");

                    return value = Integer.parseInt(valueText);
                }
            }

        }

        return value;
    }

    public static String decomponentalize(Component component) {
        if (component == null) {
            return null;
        }

        String string = PlainTextComponentSerializer.plainText().serialize(component);
        string = ChatColor.stripColor(string);
        return ChatColor.translateAlternateColorCodes('&', string);
    }



    //ITEM EFFECTS
    public void grabDefence() {
        ItemStack Helm = player.getInventory().getHelmet();
        ItemStack Chest = player.getInventory().getChestplate();
        ItemStack Legs = player.getInventory().getLeggings();
        ItemStack Boots = player.getInventory().getBoots();

        int helm = 0;
        int chest = 0;
        int legs = 0;
        int boots = 0;

        if (Helm != null) {
            helm = grabLore(player, Helm, "DEFENCE");
        }
        if (Chest != null) {
            chest = grabLore(player, Chest, "DEFENCE");
        }
        if (Legs != null) {
            legs = grabLore(player, Legs, "DEFENCE");
        }
        if (Boots != null) {
            boots = grabLore(player, Boots, "DEFENCE");
        }

        DEFENCE = (helm + chest + legs + boots);
    }

    public void grabBonusDefence() {
        ItemStack Helm = player.getInventory().getHelmet();
        ItemStack Chest = player.getInventory().getChestplate();
        ItemStack Legs = player.getInventory().getLeggings();
        ItemStack Boots = player.getInventory().getBoots();

        int helm = 0;
        int chest = 0;
        int legs = 0;
        int boots = 0;

        if (Helm != null) {
            helm = grabLore(player, Helm, "BONUS_DEFENCE");
        }
        if (Chest != null) {
            chest = grabLore(player, Chest, "BONUS_DEFENCE");
        }
        if (Legs != null) {
            legs = grabLore(player, Legs, "BONUS_DEFENCE");
        }
        if (Boots != null) {
            boots = grabLore(player, Boots, "BONUS_DEFENCE");
        }

        BONUS_DEFENCE = (helm + chest + legs + boots);
    }

    public void grabEvasion() {
        ItemStack Helm = player.getInventory().getHelmet();
        ItemStack Chest = player.getInventory().getChestplate();
        ItemStack Legs = player.getInventory().getLeggings();
        ItemStack Boots = player.getInventory().getBoots();

        int helm = 0;
        int chest = 0;
        int legs = 0;
        int boots = 0;

        if (Helm != null) {
            helm = grabLore(player, Helm, "EVASION");
        }
        if (Chest != null) {
            chest = grabLore(player, Chest, "EVASION");
        }
        if (Legs != null) {
            legs = grabLore(player, Legs, "EVASION");
        }
        if (Boots != null) {
            boots = grabLore(player, Boots, "EVASION");
        }

        EVASION = (helm + chest + legs + boots);
    }

    public void grabHealth() {
        ItemStack Helm = player.getInventory().getHelmet();
        ItemStack Chest = player.getInventory().getChestplate();
        ItemStack Legs = player.getInventory().getLeggings();
        ItemStack Boots = player.getInventory().getBoots();

        int helm = 0;
        int chest = 0;
        int legs = 0;
        int boots = 0;

        if (Helm != null) {
            helm = grabLore(player, Helm, "HEALTH");
        }
        if (Chest != null) {
            chest = grabLore(player, Chest, "HEALTH");
        }
        if (Legs != null) {
            legs = grabLore(player, Legs, "HEALTH");
        }
        if (Boots != null) {
            boots = grabLore(player, Boots, "HEALTH");
        }

        ADDED_HEALTH = (helm + chest + legs + boots);
    }

    public void grabBonusHealth() {
        ItemStack Helm = player.getInventory().getHelmet();
        ItemStack Chest = player.getInventory().getChestplate();
        ItemStack Legs = player.getInventory().getLeggings();
        ItemStack Boots = player.getInventory().getBoots();

        int helm = 0;
        int chest = 0;
        int legs = 0;
        int boots = 0;

        if (Helm != null) {
            helm = grabLore(player, Helm, "BONUS_HEALTH");
        }
        if (Chest != null) {
            chest = grabLore(player, Chest, "BONUS_HEALTH");
        }
        if (Legs != null) {
            legs = grabLore(player, Legs, "BONUS_HEALTH");
        }
        if (Boots != null) {
            boots = grabLore(player, Boots, "BONUS_HEALTH");
        }

        BONUS_HEALTH = (helm + chest + legs + boots);
    }

    public void grabBonusEvade() {
        ItemStack Helm = player.getInventory().getHelmet();
        ItemStack Chest = player.getInventory().getChestplate();
        ItemStack Legs = player.getInventory().getLeggings();
        ItemStack Boots = player.getInventory().getBoots();

        int helm = 0;
        int chest = 0;
        int legs = 0;
        int boots = 0;

        if (Helm != null) {
            helm = grabLore(player, Helm, "BONUS_EVASION");
        }
        if (Chest != null) {
            chest = grabLore(player, Chest, "BONUS_EVASION");
        }
        if (Legs != null) {
            legs = grabLore(player, Legs, "BONUS_EVASION");
        }
        if (Boots != null) {
            boots = grabLore(player, Boots, "BONUS_EVASION");
        }

        BONUS_EVASION = (helm + chest + legs + boots);
    }

    //Material or ItemStacks
    public double wepATTK_SPEED(Player player, ItemStack item) {
        double defaultAttackSpeed = 4;
        double swordAttackSpeed = 4;
        double shovelAttackSpeed = 1;
        double axeAttackSpeed = 0.8;
        double scytheAttackSpeed = 2.4;

        if (item.getType().toString().contains("SWORD")) {
            return WEAPON_ATTACK_SPEED = swordAttackSpeed;
        } else if (item.getType().toString().contains("SHOVEL")) {
            return WEAPON_ATTACK_SPEED = shovelAttackSpeed;
        } else if (item.getType().toString().contains("AXE")) {
            return WEAPON_ATTACK_SPEED = axeAttackSpeed;
        } else if (item.getType().toString().contains("HOE")) {
            return WEAPON_ATTACK_SPEED = scytheAttackSpeed;
        } else {
            return WEAPON_ATTACK_SPEED = BASE_ATTACK_SPEED;
        }
    }



    //Damage Methods
    public double damageCalculation(Player player, double damage) {
        double pointsDEF = player.getAttribute(Attribute.GENERIC_ARMOR).getValue()+DEFENCE;
        double toughness = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
        PotionEffect effect = player.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        int resistance = effect == null ? 0 : effect.getAmplifier();
        int epf = getEPF(player);

        return calculateDamageApplied(damage, pointsDEF, toughness, resistance, epf);
    }

    public double calculateDamageApplied(double damage, double points, double toughness, int resistance, int epf) {
        double withArmorAndToughness = damage * (1 - Math.min(20, Math.max(points / 5, points - damage / (2 + toughness / 4))) / 25);
        double withResistance = withArmorAndToughness * (1 - (resistance * 0.2));
        double withEnchants = withResistance * (1 - (Math.min(20.0, epf) / 25));
        return withEnchants;
    }

    public static int getEPF(Player player) {
        PlayerInventory inv = player.getInventory();
        ItemStack helm = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack legs = inv.getLeggings();
        ItemStack boot = inv.getBoots();

        return (helm != null ? helm.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (chest != null ? chest.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (legs != null ? legs.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (boot != null ? boot.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0);
    }




    //Other Damage Methods
    public int CalculateMobDamage(boolean isProjectile, int min, int max) {
        if (isProjectile == false) {
            return (int) Math.floor(Math.random() * (MAX_DMG - MIN_DMG + 1) + MIN_DMG);
        } else {
            return (int) Math.floor(Math.random() * (max - min + 1) + min);
        }
    }

    public boolean cancelDamageCheck(Player hitPlayer, LivingEntity damager) {
        boolean setCancelled = false;

        Random random = new Random();
        int chance = random.nextInt(100 + 1 - 1) + 1;

        if (chance <= EVASION) {
            hitPlayer.sendMessage(ChatColor.GREEN + "You " + ChatColor.GOLD + "evaded " + ChatColor.GREEN + "an attack!");
            damager.sendMessage(ChatColor.RED + "Enemy " + ChatColor.GOLD + "evaded " + ChatColor.RED + "an attack!");
            setCancelled = true;
        }

        if (IS_BLOCKING) {
            if (chance <= BLOCK_CHANCE) {
                hitPlayer.sendMessage(ChatColor.GREEN + "You " + ChatColor.GOLD + "blocked " + ChatColor.GREEN + "an attack!");
                damager.sendMessage(ChatColor.RED + "Enemy " + ChatColor.GOLD + "blocked " + ChatColor.RED + "an attack!");
                setCancelled = true;
            }
        }

        return setCancelled;
    }



    //Getters
    public Integer get_MIN_DMG() {
        return MIN_DMG;
    }
    public Integer get_MAX_DMG() {
        return MAX_DMG;
    }
    public Integer get_DEFENCE() {
        return DEFENCE;
    }
}
