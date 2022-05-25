package me.silathar.Modules;

import me.silathar.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemClass {
    private static Main plugin = Main.getPlugin(Main.class);

    private static int multiDefenceChance = 10; //10 Default
    private static int getEffectsChance = 90; //25 Default
    private static int MaxTierChance = 100; //100 Default
    private static int getPrefixChance = 25; //25 Default
    private static int maxRarityChance = 5000; //5000 Default
    private static int lowerChanceAmount = 50;

    //RNG ITEM CHANCES
    public static int generateTier(int Level) {
        Integer tier = 1;
        int maxChance = maxRarityChance;
        int maxTier = 1;

        int lowerAmount = lowerChanceAmount*Level;
        Random random = new Random();
        int chance = random.nextInt(maxRarityChance + 1 - 1) + 1;

        if (Level <= 10) {
            maxTier = 1;
        } else if (Level > 10 && Level <= 20) {
            maxTier = 2;
        } else if (Level > 20 && Level <= 30) {
            maxTier = 3;
        } else if (Level > 40 && Level <= 49) {
            maxTier = 4;
        } else if (Level > 40 && Level <= 50) {
            maxTier = 5;
        } else if (Level > 50 && Level <= 100) {
            maxTier = 6;
        }

        //Increase Max Tier possibility.
        int chance2 = random.nextInt(MaxTierChance + 1 - 1) + 1;
        if (chance2 <= 5) { //5%
            maxTier = maxTier+1;

            if (tier > 6) {
                tier = 6;
            }
        }

        if (chance == 1) {
            tier = 6; //Netherite
        } else if (chance > 1 && chance <= 25) {
            tier = 5; //Diamond
        } else if (chance > 25 && chance <= 50) {
            tier = 4; //Gold
        } else if (chance > 50 && chance <= 100) {
            tier = 3; //Iron
        } else if (chance > 100 && chance <= 200) {
            tier = 2; //Stone/Chainmail
        } else if (chance > 200 && chance <= maxChance) {
            tier = 1; // Wood/Leather
        }

        if (tier > maxTier) {
            tier = maxTier;
        }

        //FORCE
        tier = 3;
        return tier;
    }
    //ITEM DEFAULT RANGES
    public static int randomValue(String type, int tier) {
        Random ran = new Random();

        int default_min = 1;
        int default_max = 6;

        if (type.contains("Sword")) {
            default_min = 1;
            default_max = 6;
        } else if (type.contains("Battle Axe")) {
            default_min = 3;
            default_max = 15;
        } else if (type.contains("Mace")) {
            default_min = 2;
            default_max = 12;
        } else if (type.contains("Scythe")) {
            default_min = 2;
            default_max = 4;
        } else if (type.contains("Bow")) {
            default_min = 2;
            default_max = 6;
        } else if (type.contains("Crossbow")) {
            default_min = 3;
            default_max = 9;
        } else if (type.contains("Trident")) {
            default_min = 5;
            default_max = 10;
        } else if (type.contains("Helmet")) {
            default_min = 1;
            default_max = 8;
        } else if (type.contains("Platebody")) {
            default_min = 3;
            default_max = 16;
        } else if (type.contains("Platelegs")) {
            default_min = 4;
            default_max = 12;
        } else if (type.contains("Boots")) {
            default_min = 2;
            default_max = 6;
        } else if (type.contains("Shield")) {
            default_min = 1;
            default_max = 8;
        }

        default_min = default_min*tier;
        default_max = default_max*tier;

        return ran.nextInt(default_max) + default_min;
    }
    //ITEM MIN-MAX RANGE
    public static String generateMinMax(String categoryType, String type, String prefix, int tier) {
        int min = 0;
        int max = 0;
        double addedPercent = 0;

        if (!prefix.equals("None")) {
            addedPercent = checkPrefix(prefix);
        }

        do {
            min = randomValue(type, tier);
            max = randomValue(type, tier);
        } while (max < min);
        //Multiply Again
        min = min;
        max = max;


        if (categoryType.equals("Weapon")) {
            if (addedPercent != 0) {
                plugin.getServer().getConsoleSender().sendMessage("BEFORE CALCULATION: " + "" + "MIN: " + min + " - " + "MAX: " + max);
                plugin.getServer().getConsoleSender().sendMessage("ADDED PERCENT: " + addedPercent);

                double math = addedPercent / 100;
                double mul = 1 + math;

                int newMin = (int) (min*mul);
                int newMax = (int) (max*mul);

                plugin.getServer().getConsoleSender().sendMessage("AFTER CALCULATION: " + "MIN: " + newMin + ", " + "MAX: " + newMax);
                return "" + newMin + " - " + newMax;
            }
        } else {
            if (addedPercent == 0) {

                return "" + randomValue(type, tier);
            } else if (addedPercent != 0) {
                double baseDefence = randomValue(type, tier);

                plugin.getServer().getConsoleSender().sendMessage("BEFORE CALCULATION: " + "" + baseDefence     );
                plugin.getServer().getConsoleSender().sendMessage("ADDED PERCENT: " + addedPercent);

                double math = addedPercent / 100;
                double mul = 1 + math;
                double def = Math.floor(baseDefence*mul);

                int newDefence = (int) def;
                plugin.getServer().getConsoleSender().sendMessage("AFTER CALCULATION: " + "" + newDefence);
                return "" + newDefence;
            }

        }

        return "" + min*tier + " - " + max*tier;
    }

    //GENERATE EFFECTS
    public static String generateEffect(String itemCategory, int tier, String nameTag) {
        String effect = "";

        Random random = new Random();
        if (itemCategory.equals("Weapon")) {
            int weaponChance = random.nextInt(100 + 1 - 1) + 1;

            int fullAmount = random.nextInt(50 + 1 - 1) + 1;
            int halfAmount = random.nextInt(25 + 1 - 1) + 1;
            int quarterAmount = random.nextInt(12 + 1 - 1) + 1;

            if (weaponChance <= 10) {
                effect = ChatColor.GREEN + "Increase " + ChatColor.YELLOW + "Damage by " + ChatColor.GOLD + fullAmount + ChatColor.YELLOW + "%";
            } else if (weaponChance > 10 && weaponChance <= 20) {
                effect = ChatColor.GREEN + "Increase " + ChatColor.YELLOW + "Attack Speed by " + ChatColor.GOLD + halfAmount + ChatColor.YELLOW + "%";
            } else if (weaponChance > 20 && weaponChance <= 30) {
                effect = ChatColor.GREEN + "Increase " + ChatColor.YELLOW +  "Critical Strike Chance " + ChatColor.GOLD + quarterAmount + ChatColor.YELLOW + "%";
            } else if (weaponChance > 30 && weaponChance <= 40) {
                effect = ChatColor.GREEN + "Increase " + ChatColor.YELLOW +  "Drop Chance by " + ChatColor.GOLD + quarterAmount;
            } else if (weaponChance > 40 && weaponChance <= 50) {
                effect = "" + ChatColor.GOLD + quarterAmount + ChatColor.YELLOW + " Life Gained Per " + ChatColor.GOLD + "Kill";
            } else if (weaponChance > 50 && weaponChance <= 60) {
                effect = "" + ChatColor.GOLD + quarterAmount + ChatColor.YELLOW + " Life Gained Per " + ChatColor.GOLD + "Hit";
            } else if (weaponChance > 60 && weaponChance <= 70) {
                effect = ChatColor.GREEN + "Adds " + ChatColor.GOLD + generateMinMax(itemCategory, nameTag, "None", tier) + " Fire Damage";
            } else if (weaponChance > 70 && weaponChance <= 80) {
                effect = ChatColor.GREEN + "Adds " + ChatColor.GOLD + generateMinMax(itemCategory, nameTag, "None", tier) + " Poison Damage";
            } else if (weaponChance > 80 && weaponChance <= 90) {
                effect = ChatColor.GREEN + "Adds " + ChatColor.BLUE + ChatColor.GOLD + generateMinMax(itemCategory, nameTag, "None", tier) + ChatColor.YELLOW + " Cold Damage";
            } else if (weaponChance > 90 && weaponChance <= 100) {

            }

        } else if (itemCategory.equals("Armor")) {
            int armorChance = random.nextInt(100 + 1 - 1) + 1;

            int fullAmount = random.nextInt(50 + 1 - 1) + 1;
            int halfAmount = random.nextInt(25 + 1 - 1) + 1;
            int quarterAmount = random.nextInt(12 + 1 - 1) + 1;

            if (armorChance <= 10) {
                effect = ChatColor.GREEN + "Increase " + ChatColor.YELLOW +  "Defence by " + ChatColor.GOLD + fullAmount + ChatColor.YELLOW + "%";
            } else if (armorChance > 10 && armorChance <= 20) {
                effect = ChatColor.GREEN + "Increase " + ChatColor.YELLOW +  "Evasion by " + ChatColor.GOLD + fullAmount + ChatColor.YELLOW + "%";
            } else if (armorChance > 20 && armorChance <= 30) {
                effect = ChatColor.GREEN + "Increase " + ChatColor.YELLOW +  "Movement Speed by " + ChatColor.GOLD + halfAmount + ChatColor.YELLOW + "%";
            } else if (armorChance > 30 && armorChance <= 40) {
                effect = ChatColor.GREEN + "Increase " + ChatColor.YELLOW +  "Movement Speed by " + ChatColor.GOLD + halfAmount + ChatColor.YELLOW + "%";
            } else if (armorChance > 40 && armorChance <= 50) {
                effect = ChatColor.GREEN + "Adds " + ChatColor.YELLOW +  "Fire Resistance by " + ChatColor.GOLD + halfAmount+ ChatColor.YELLOW + "%";
            } else if (armorChance > 50 && armorChance <= 60) {
                effect = ChatColor.GREEN + "Adds " + ChatColor.YELLOW +  "Poison Resistance by " + ChatColor.GOLD + halfAmount+ ChatColor.YELLOW + "%";
            } else if (armorChance > 60 && armorChance <= 70) {
                effect = ChatColor.GREEN + "Adds " + ChatColor.YELLOW +  "Cold Resistance by " + ChatColor.GOLD + halfAmount+ ChatColor.YELLOW + "%";
            } else if (armorChance > 70 && armorChance <= 80) {
                effect = ChatColor.GREEN + "Increase " + ChatColor.YELLOW +  "Health by " + ChatColor.GOLD + quarterAmount+ ChatColor.YELLOW + "%";
            } else if (armorChance > 80 && armorChance <= 90) {
                effect = ChatColor.GREEN + "Adds "     +  ChatColor.GOLD + quarterAmount + ChatColor.YELLOW + " Health";
            } else if (armorChance > 90 && armorChance <= 100) {
                effect = ChatColor.GREEN + "Increase " + ChatColor.YELLOW +  "Drop Chance by " + ChatColor.GOLD + quarterAmount + ChatColor.YELLOW + "%";
            }
        }

        return effect;
    }

    //GET EFFECT TYPE
    public static boolean isEffectInList(List list, String effect) {
        if (list.size() == 0 || list == null) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "LIST IS EMPTY!");
            return false;
        }

        effect = ChatColor.stripColor(effect);
        String effType = effectType(effect);
        String[] effSplit = effType.split(",");

        String addType = effSplit[0];
        String subType = effSplit[1];

        plugin.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Tag Types: " + addType + "," + subType);

        for (int i = 0; i < list.size(); i++) {
            String listEffect = ChatColor.stripColor(list.get(i).toString());
            String listType = effectType(listEffect);
            String[] listSplit = listType.split(",");

            String list_addType = listSplit[0];
            String list_subType = listSplit[1];

            if (addType.equals(list_addType) && (subType.equals(list_subType))) {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "LIST ALREADY HAS EFFECT: " + addType + ", " + subType);
                return true;
            } else {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "LIST DOES NOT HAVE EFFECT: " + addType + ", " + subType);
            }
        }

        return false;
    }

    public static String effectType(String effect) {
        ChatColor.stripColor(effect);
        String[] split = effect.split(" ");

        String addType = "";
        String subType = "";

        for (int x = 0; x < split.length; x++) {
            if (split[x].contains ("Increase")) {
                addType = "Increase";
            }
            if (split[x].contains ("Adds")) {
                addType = "Adds";
            }
        }

        for (int x = 0; x < split.length; x++) {
            //WEAPON
            if (split[x].contains ("Damage")) {
                subType = "Damage";
            }
            if (split[x].contains ("Attack") && split[x+1].contains("Speed")) {
                subType = "Attack Speed";
            }
            if (split[x].contains ("Critical") && split[x+1].contains("Strike") && split[x+2].contains("Chance")) {
                subType = "Critical Strike Chance";
            }
            if (split[x].contains ("Per") && split[x+1].contains("Hit")) {
                subType = "Life Gained Per Hit";
            }
            if (split[x].contains ("Per") && split[x+1].contains("Kill")) {
                subType = "Life Gained Per Kill";
            }
            if (split[x].contains ("Poison") && split[x+1].contains("Damage")) {
                subType = "Poison Damage";
            }
            if (split[x].contains ("Cold") && split[x+1].contains("Damage")) {
                subType = "Cold Damage";
            }

            //ARMOR
            if (split[x].contains ("Defence")) {
                subType = "Defence";
            }
            if (split[x].contains ("Evasion")) { //Works
                subType = "Evasion";
            }

            if (addType.equals("Increase") && split[x].contains("Health")) {
                subType = "Increase Health";
            } else if (addType.equals("Adds") && split[x].contains("Health")) {
                subType = "Adds Health"; //Works
            }

            if (split[x].contains ("Movement") && split[x+1].contains("Speed")) {
                subType = "Movement Speed";
            }
            if (split[x].contains ("Fire") && split[x+1].contains("Resistance")) {
                subType = "Fire Resistance";
            }
            if (split[x].contains ("Poison") && split[x+1].contains("Resistance")) {
                subType = "Poison Resistance";
            }
            if (split[x].contains ("Cold") && split[x+1].contains("Resistance")) {
                subType = "Cold Resistance";
            }
            if (split[x].contains ("Drop") && split[x+1].contains("Chance")) {
                subType = "Drop Chance";
            }
        }

        return addType + ", " + subType;
    }

    //MULTIPLE EFFECTS
    public static List<Component> generateMultipleEffects(String itemCategory, int tier, String nameTag) {
        Random ran = new Random();
        int effectsChance = (int) (ran.nextDouble() * 100);

        List<Component> compList = new ArrayList<Component>();
        List<String> effectList = new ArrayList<String>();

        if (effectsChance <= getEffectsChance) {
            int maxEffects = 0;

            if (tier == 2) {
                maxEffects = 1;
            } else if (tier == 3) {
                maxEffects = 2;
            } else if (tier == 4) {
                maxEffects = 3;
            } else if (tier == 5) {
                maxEffects = 4;
            } else if (tier == 6) {
                maxEffects = 5;
            }

            //FORCE
            maxEffects = 5;

            if (maxEffects > 0) {
                compList.add(Component.text(ChatColor.YELLOW + "===================="));
            }

            for (int i = 0; i < maxEffects; i++) {
                String effectTag = generateEffect(itemCategory, tier, nameTag);
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "Tag Generated: " + effectTag);

                if (i > 0) {
                    if (isEffectInList(effectList, effectTag)) {
                        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Tag Not Added: " + effectTag);
                    } else {
                        effectList.add(effectTag);
                        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Tag Added: " + effectTag);
                    }
                } else {
                    effectList.add(effectTag);
                }
            }

            for (int i = 0; i < effectList.size(); i++) {
                compList.add(Component.text(effectList.get(i)));
            }
        }

        return compList;
    }


    //GENERATE PREFIX
    public static String generatePrefix(String itemCategory) {
        Random rn = new Random();

        int prefixChance = (int) (rn.nextDouble() * 100);
        int categoryChance = (int) ( Math.random() * 2 + 1);
        int typeChance = (int) (rn.nextDouble() * 100);

        if (prefixChance <= getPrefixChance) {
            if (itemCategory.equals("Weapon")) {
                if (categoryChance == 1) {
                    //Positive Attack Damage // Top Best // Bottom Worst
                    if (typeChance < 10) {
                        return ChatColor.DARK_AQUA + "Cruel";
                    } else if (typeChance > 10 && typeChance <= 20) { //50%
                        return ChatColor.DARK_AQUA + "Vicious";
                    } else if (typeChance > 20 && typeChance <= 30) { //40%
                        return ChatColor.DARK_AQUA + "Deadly";
                    } else if (typeChance > 30 && typeChance <= 40) { //35%
                        return ChatColor.DARK_AQUA + "Brutal";
                    } else if (typeChance > 40 && typeChance <= 50) { //30%
                        return ChatColor.DARK_AQUA + "Savage";
                    } else if (typeChance > 50 && typeChance <= 60) { //25%
                        return ChatColor.DARK_AQUA + "Mericless";
                    } else if (typeChance > 60 && typeChance <= 70) { //20%
                        return ChatColor.DARK_AQUA + "Ferocious";
                    } else if (typeChance > 70 && typeChance <= 80) { //15%
                        return ChatColor.DARK_AQUA + "Jagged";
                    } else if (typeChance > 80 && typeChance <= 100) { //10%
                        return ChatColor.DARK_AQUA + "Massive";
                    }
                } else if (categoryChance == 2) {
                    //Negative Attack Damage // Top Best // Bottom Worst
                    if (typeChance < 10) {
                        return ChatColor.DARK_RED + "Corroded";
                    } else if (typeChance > 10 && typeChance <= 20) {
                        return ChatColor.DARK_RED + "Eroded";
                    } else if (typeChance > 20 && typeChance <= 30) {
                        return ChatColor.DARK_RED + "Gnawed";
                    } else if (typeChance > 30 && typeChance <= 40) {
                        return ChatColor.DARK_RED + "Obtuse";
                    } else if (typeChance > 40 && typeChance <= 50) {
                        return ChatColor.DARK_RED + "Filthy";
                    } else if (typeChance > 50 && typeChance <= 60) {
                        return ChatColor.DARK_RED + "Jaded";
                    } else if (typeChance > 60 && typeChance <= 70) {
                        return ChatColor.DARK_RED + "Sleazy";
                    } else if (typeChance > 70 && typeChance <= 80) {
                        return ChatColor.DARK_RED + "Blunt";
                    } else if (typeChance > 80 && typeChance <= 100) {
                        return ChatColor.DARK_RED + "Dull";
                    }
                }

            } else if (itemCategory.equals("Armor")) {
                if (categoryChance == 1) {
                    //Positive Defence
                    if (typeChance < 10) {
                        return ChatColor.DARK_AQUA + "Adamant"; //50% Buff
                    } else if (typeChance > 10 && typeChance <= 20) {
                        return ChatColor.DARK_AQUA + "Steadfast";
                    } else if (typeChance > 20 && typeChance <= 30) {
                        return ChatColor.DARK_AQUA + "Devout";
                    } else if (typeChance > 30 && typeChance <= 40) {
                        return ChatColor.DARK_AQUA + "Staunch";
                    } else if (typeChance > 40 && typeChance <= 50) {
                        return ChatColor.DARK_AQUA + "Unswerving";
                    } else if (typeChance > 50 && typeChance <= 60) {
                        return ChatColor.DARK_AQUA + "Resolute";
                    } else if (typeChance > 60 && typeChance <= 70) {
                        return ChatColor.DARK_AQUA + "Ardent";
                    } else if (typeChance > 70 && typeChance <= 80) {
                        return ChatColor.DARK_AQUA + "Unflinching";
                    } else if (typeChance > 80 && typeChance <= 100) {
                        return ChatColor.DARK_AQUA + "Unyielding";
                    }
                } else if (categoryChance == 2) {
                    //Negative Defence
                    if (typeChance < 10) {
                        return ChatColor.DARK_RED + "Decrepit"; //50% Debuff
                    } else if (typeChance > 10 && typeChance <= 20) {
                        return ChatColor.DARK_RED + "Perfidious";
                    } else if (typeChance > 20 && typeChance <= 30) {
                        return ChatColor.DARK_RED + "Fickle";
                    } else if (typeChance > 30 && typeChance <= 40) {
                        return ChatColor.DARK_RED + "Anemic";
                    } else if (typeChance > 40 && typeChance <= 50) {
                        return ChatColor.DARK_RED + "Fragile";
                    } else if (typeChance > 50 && typeChance <= 60) {
                        return ChatColor.DARK_RED + "Frail";
                    } else if (typeChance > 60 && typeChance <= 70) {
                        return ChatColor.DARK_RED + "Sluggish";
                    } else if (typeChance > 70 && typeChance <= 80) {
                        return ChatColor.DARK_RED + "Flimsy";
                    } else if (typeChance > 80 && typeChance <= 100) {
                        return ChatColor.DARK_RED + "Weak"; //10% Debuff
                    }
                }

            }
        }

        return "None";
    }
    //CHECK PREFIX
    public static double checkPrefix(String prefix) {
        double addedPercent = 0;

        prefix = org.bukkit.ChatColor.stripColor(prefix);
        String[] split = prefix.split(" ");

        prefix = split[0];
        prefix = prefix.replaceAll("\\s", "");
        plugin.getServer().getConsoleSender().sendMessage(prefix);

        //POSITIVE PREFIXES
        if (prefix.equals("Cruel")) {
            addedPercent = 50.0;
        } else if (prefix.equals("Vicious")) {
            addedPercent = 45.0;
        } else if (prefix.equals("Deadly")) {
            addedPercent = 40.0;
        } else if (prefix.equals("Brutal")) {
            addedPercent = 35.0;
        } else if (prefix.equals("Savage")) {
            addedPercent = 30.0;
        } else if (prefix.equals("Mericless")) {
            addedPercent = 25.0;
        } else if (prefix.equals("Ferocious")) {
            addedPercent = 20.0;
        } else if (prefix.equals("Jagged")) {
            addedPercent = 15.0;
        } else if (prefix.equals("Massive")) {
            addedPercent = 10.0;

            //ARMOR
        } else if (prefix.equals("Adamant")) {
            addedPercent = 50.0;
        } else if (prefix.equals("Steadfast")) {
            addedPercent = 45.0;
        } else if (prefix.equals("Devout")) {
            addedPercent = 40.0;
        } else if (prefix.equals("Staunch")) {
            addedPercent = 35.0;
        } else if (prefix.equals("Unswerving")) {
            addedPercent = 30.0;
        } else if (prefix.equals("Resolute")) {
            addedPercent = 25.0;
        } else if (prefix.equals("Ardent")) {
            addedPercent = 20.0;
        } else if (prefix.equals("Unflinching")) {
            addedPercent = 15.0;
        } else if (prefix.equals("Unyielding")) {
            addedPercent = 10.0;

            //NEGATIVE PREFIXES
            //WEAPON
        } else if (prefix.equals("Corroded")) {
            addedPercent = -50.0;
        } else if (prefix.equals("Eroded")) {
            addedPercent = -45.0;
        } else if (prefix.equals("Gnawed")) {
            addedPercent = -40.0;
        } else if (prefix.equals("Obtuse")) {
            addedPercent = -35.0;
        } else if (prefix.equals("Filthy")) {
            addedPercent = -30.0;
        } else if (prefix.equals("Jaded")) {
            addedPercent = -25.0;
        } else if (prefix.equals("Sleazy")) {
            addedPercent = -20.0;
        } else if (prefix.equals("Blunt")) {
            addedPercent = -15.0;
        } else if (prefix.equals("Dull")) {
            addedPercent = -10.0;

            //ARMOR
        } else if (prefix.equals("Decrepit")) {
            addedPercent = -50.0;
        } else if (prefix.equals("Perfidious")) {
            addedPercent = -45.0;
        } else if (prefix.equals("Fickle")) {
            addedPercent = -40.0;
        } else if (prefix.equals("Anemic")) {
            addedPercent = -35.0;
        } else if (prefix.equals("Fragile")) {
            addedPercent = -30.0;
        } else if (prefix.equals("Frail")) {
            addedPercent = -25.0;
        } else if (prefix.equals("Sluggish")) {
            addedPercent = -20.0;
        } else if (prefix.equals("Flimsy")) {
            addedPercent = -15.0;
        } else if (prefix.equals("Weak")) {
            addedPercent = -10.0;
        }

        return addedPercent;
    }



    //GENERATION
    public static ItemStack generateDrop(Player player, int Level) {
        int tier = generateTier(Level);

        int chanceType = (int)(Math.random() * 99 + 1);
        String itemCategory;
        String itemType = "";
        String materialType = "";
        String nameTag = "";
        ChatColor colorTag = ChatColor.WHITE;
        ChatColor prefixColor = ChatColor.GRAY;
        //Stats
        String defence = "0";

        if (chanceType <= 50) {
            itemCategory = "Weapon";
        } else {
            itemCategory = "Armor";
        }

        //FORCE
        itemCategory = "Weapon";

        if (tier == 1) {
            if (itemCategory.equals("Weapon")) {
                materialType = "Wooden";
            } else if (itemCategory.equals("Armor")) {
                materialType = "Leather";
            }
            colorTag = ChatColor.WHITE;
        } else if (tier == 2) {
            if (itemCategory.equals("Weapon")) {
                materialType = "Stone";
            } else if (itemCategory.equals("Armor")) {
                materialType = "Chainmail";
            }
            colorTag = ChatColor.GREEN;
        } else if (tier == 3) {
            materialType = "Iron";
            colorTag = ChatColor.BLUE;
        } else if (tier == 4) {
            materialType = "Golden";
            colorTag = ChatColor.YELLOW;
        } else if (tier == 5) {
            materialType = "Diamond";
            colorTag = ChatColor.GOLD;
        } else if (tier == 6) {
            materialType = "Netherite";
            colorTag = ChatColor.DARK_PURPLE;
        }

        if (itemCategory.equals("Weapon")) {
            int weaponChance = (int)(Math.random() * 99 + 1);

            if (weaponChance <= 16) {
                itemType = "Sword";
                nameTag = "Blade";

            } else if (weaponChance > 16 && weaponChance <= 32) {
                itemType = "Shovel";
                nameTag = "Mace";

            } else if (weaponChance > 32 && weaponChance <= 48) {
                itemType = "Axe";
                nameTag = "Battle Axe";
            } else if (weaponChance > 48 && weaponChance <= 64) {
                itemType = "Hoe";
                nameTag = "Scythe";

            } else if (weaponChance > 64 && weaponChance <= 80) {
                itemType = "Bow";
                nameTag = "Bow";

            } else if (weaponChance > 80 && weaponChance <= 90) {
                itemType = "Crossbow";
                nameTag = "Crossbow";
            } else if (weaponChance > 90 && weaponChance <= 100) {
                itemType = "Trident";
                nameTag = "Spear";
            }

        } else if (itemCategory.equals("Armor")) {
            int armorChance = (int)(Math.random() * 125 + 1);

            if (armorChance <= 25) {
                itemType = "Helmet";
                nameTag = "Helmet";
            } else if (armorChance > 25 && armorChance <= 50) {
                itemType = "Chestplate";
                nameTag = "Platebody";
            } else if (armorChance > 50 && armorChance <= 75) {
                itemType = "Leggings";
                nameTag = "Platelegs";
            } else if (armorChance > 75 && armorChance <= 100) {
                itemType = "Boots";
                nameTag = "Boots";
            } else if (armorChance > 100 && armorChance <= 125) {
                itemType = "Shield";
                nameTag = "Shield";
            }
        }

        List<Component> compList = new ArrayList<Component>();
        compList.add(Component.text(prefixColor + "Tier: " + colorTag + tier));

        Material itemDrop = Material.AIR;
        ItemStack item = new ItemStack(itemDrop, 1);
        ItemMeta itemMeta = item.getItemMeta();
        String prefix = materialType;
        String hasPrefix = "False";

        if (tier >= 3) {
            String pre = generatePrefix(itemCategory);

            if (pre.equals("None")) {
                prefix = colorTag + materialType;
            } else {
                prefix = pre + " " + colorTag + materialType;
                hasPrefix = "True";
            }
        } else {
            prefix = materialType;
        }

        String tagText = prefix + " " + nameTag;

        //Give these items directly as they have no materialType
        if (itemCategory.equals("Weapon")) {

            if (nameTag.equals("Bow")) {
                itemDrop = Material.BOW;
                item = new ItemStack(itemDrop, 1);
                itemMeta = item.getItemMeta();

            } else if (nameTag.equals("Crossbow")) {
                itemDrop = Material.CROSSBOW;
                item = new ItemStack(itemDrop, 1);
                itemMeta = item.getItemMeta();

            } else if (nameTag.equals("Spear")) {
                itemDrop = Material.TRIDENT;
                item = new ItemStack(itemDrop, 1);
                itemMeta = item.getItemMeta();
            } else {
                itemDrop = Material.valueOf(materialType.toUpperCase()+"_"+itemType.toUpperCase());
                item = new ItemStack(itemDrop, 1);
                itemMeta = item.getItemMeta();
            }

            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER);
            itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);

            if (hasPrefix.equals("True")) {
                compList.add(Component.text(prefixColor +"Damage: " + ChatColor.AQUA + generateMinMax(itemCategory, nameTag, prefix, tier)));

            } else {
                compList.add(Component.text(prefixColor +"Damage: " + ChatColor.AQUA + generateMinMax(itemCategory, nameTag, "False", tier)));
            }


            if (nameTag.equals("Blade")) {
                compList.add(Component.text(prefixColor + "Attack Speed: " + ChatColor.AQUA + "1.6"));
            } else if (nameTag.equals("Mace")) {
                compList.add(Component.text(prefixColor + "Attack Speed: " + ChatColor.AQUA + "1"));
            } else if (nameTag.equals("Battle Axe")) {
                compList.add(Component.text(prefixColor + "Attack Speed: " + ChatColor.AQUA + "0.8"));
            } else if (nameTag.equals("Scythe")) {
                compList.add(Component.text(prefixColor + "Attack Speed: " + ChatColor.AQUA + "2.4"));
            } else if (nameTag.equals("Trident")) {
                compList.add(Component.text(prefixColor + "Attack Speed: " + ChatColor.AQUA + "1.25"));
            }

            itemMeta.displayName(Component.text(tagText));

            //Merge Together the previous component list with the effects list
            List<Component> compList2 = generateMultipleEffects(itemCategory, tier, nameTag);
            List<Component> newCompList = Stream.concat(compList.stream(), compList2.stream())
                    .collect(Collectors.toList());

            newCompList.add(Component.text(ChatColor.DARK_GRAY + "Rev: " + ChatColor.DARK_GRAY + plugin.version));

            itemMeta.lore(compList);
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(itemMeta);

        } else if (itemCategory.equals("Armor")) {
            Random random = new Random();
            int valueType = random.nextInt(2 + 1 - 1) + 1; //Defence or Evasion
            int doubleValueChance = random.nextInt(100 + 1 - 1) + 1; //Defence or Evasion
            String defenceText = "Defence:";

            if (valueType == 1) {
                defenceText = "Defence: ";
            } else {
                defenceText = "Evasion: ";
            }


            if (!nameTag.equals("Shield")) {
                if (hasPrefix.equals("True")) {
                    if (doubleValueChance <= multiDefenceChance) {
                        defence = generateMinMax(itemCategory, nameTag, prefix, tier);
                        int evasion = Integer.parseInt(generateMinMax(itemCategory, nameTag, prefix, tier)) / 3;

                        compList.add(Component.text(prefixColor + "Defence: " + ChatColor.AQUA + defence));
                        compList.add(Component.text(prefixColor + "Evasion: " + ChatColor.AQUA + evasion));
                    } else {
                        if (defenceText.equals("Defence: ")) {
                            defence = generateMinMax(itemCategory, nameTag, prefix, tier);
                            compList.add(Component.text(prefixColor + defenceText + ChatColor.AQUA + defence));
                        } else {
                            //Evasion
                            int evasion = Integer.parseInt(generateMinMax(itemCategory, nameTag, prefix, tier)) / 3;
                            compList.add(Component.text(prefixColor + defenceText + ChatColor.AQUA + evasion));
                        }
                    }
                } else {
                    if (doubleValueChance <= multiDefenceChance) {
                        defence = generateMinMax(itemCategory, nameTag, "False", tier);
                        int evasion = Integer.parseInt(generateMinMax(itemCategory, nameTag, "False", tier)) / 3;

                        compList.add(Component.text(prefixColor + "Defence: " + ChatColor.AQUA + defence));
                        compList.add(Component.text(prefixColor + "Evasion: " + ChatColor.AQUA + evasion));
                    } else {
                        if (defenceText.equals("Defence: ")) {
                            defence = generateMinMax(itemCategory, nameTag, "False", tier);
                            compList.add(Component.text(prefixColor + defenceText + ChatColor.AQUA + defence));
                        } else {
                            int newDefence = Integer.parseInt(generateMinMax(itemCategory, nameTag, "False", tier)) / 3;
                            compList.add(Component.text(prefixColor + defenceText + ChatColor.AQUA + newDefence));
                        }
                    }
                }
            }

            if (nameTag.equals("Shield")) {
                compList.add(Component.text(prefixColor + "Block Chance: " + ChatColor.AQUA + randomValue(nameTag, tier)));

                itemDrop = Material.SHIELD;
                item = new ItemStack(itemDrop, 1);
                itemMeta = item.getItemMeta();
                itemMeta.setUnbreakable(false);
                itemMeta.displayName(Component.text(tagText));

                compList.add(Component.text(ChatColor.DARK_GRAY + "Rev: " + ChatColor.DARK_GRAY + plugin.version));
                itemMeta.lore(compList);
            } else {
                itemDrop = Material.valueOf(materialType.toUpperCase()+"_"+itemType.toUpperCase());
                item = new ItemStack(itemDrop, 1);
                itemMeta = item.getItemMeta();
            }

            //Merge Together the previous component list with the effects list
            if (!nameTag.equals("Shield")) {
                List<Component> compList2 =generateMultipleEffects(itemCategory, tier, nameTag);
                List<Component> newCompList = Stream.concat(compList.stream(), compList2.stream())
                        .collect(Collectors.toList());

                newCompList.add(Component.text(ChatColor.DARK_GRAY + "Rev: " + ChatColor.DARK_GRAY + plugin.version));
                itemMeta.lore(newCompList);
            }

            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

            itemMeta.displayName(Component.text(tagText));
            item.setItemMeta(itemMeta);
        }

        return item;
    }

}
