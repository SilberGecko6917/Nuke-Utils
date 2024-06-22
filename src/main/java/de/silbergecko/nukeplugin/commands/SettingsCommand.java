package de.silbergecko.nukeplugin.commands;

import de.silbergecko.nukeplugin.Main;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;
    private final List<String> booleanOptions = Arrays.asList("true", "false");

    public SettingsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            String key = args[0];
            String value = args[1];

            switch (key.toLowerCase()) {
                case "material":
                    Material material = Material.getMaterial(value.toUpperCase());
                    if (material != null && material.isBlock() && material.isItem() && material != Material.AIR && material != Material.VOID_AIR) {
                        String oldString = plugin.getConfig().getString("material", "TNT");
                        Material oldMaterial = Material.getMaterial(oldString.toUpperCase());
                        if (oldMaterial == null) {
                            oldMaterial = Material.TNT;
                        }
                        plugin.getConfig().set("material", value.toUpperCase());
                        sender.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4The material has been changed from §7§l" + oldMaterial + " §4to §7§l" + value.toUpperCase());
                    } else {
                        sender.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4Invalid material. Usage: §7/nuke-settings material <MATERIAL_NAME>");
                    }
                    break;
                case "power":
                    try {
                        int power = Integer.parseInt(value);
                        if (power >= 4 && power <= 150) {
                            int oldPower = plugin.getConfig().getInt("power", 4);
                            plugin.getConfig().set("power", power);
                            sender.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4The power has been changed from §7§l" + oldPower + " §4to §7§l" + value);
                        } else {
                            sender.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4Invalid power. Power must be between 4 and 150. Usage: §7/nuke-settings power <4-150>");
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4Invalid power. Power must be a number. Usage: §7/nuke-settings power <4-150>");
                    }
                    break;
                case "warning":
                case "destroy_bedrock":
                    if (booleanOptions.contains(value.toLowerCase())) {
                        boolean oldBoolean = plugin.getConfig().getBoolean(key, false);
                        plugin.getConfig().set(key, Boolean.parseBoolean(value));
                        sender.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4The " + key + " has been changed from §7§l" + oldBoolean + " §4to §7§l" + value);
                    } else {
                        sender.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4Invalid value. Must be either 'true' or 'false'. Usage: §7/nuke-settings " + key + " <true|false>");
                    }
                    break;
                case "cooldown":
                    try {
                        int cooldown = Integer.parseInt(value);
                        if (cooldown >= 0 && cooldown <= 500) {
                            int oldCooldown = plugin.getConfig().getInt("cooldown", 60);
                            plugin.getConfig().set("cooldown", cooldown);
                            sender.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4The cooldown has been changed from §7§l" + oldCooldown + " §4to §7§l" + value);
                        } else {
                            sender.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4Invalid cooldown. Cooldown must be between 0 and 500. Usage: §7/nuke-settings cooldown <0-500>");
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4Invalid cooldown. Cooldown must be a number. Usage: §7/nuke-settings cooldown <0-500>");
                    }
                    break;
            }

            plugin.saveConfig();
            return true;
        }
        sender.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4Usage: §7/nuke-settings <material|power|warning|cooldown|destroy_bedrock> <value>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("material", "power", "warning", "cooldown", "destroy_bedrock"), new ArrayList<>());
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "material":
                    return StringUtil.copyPartialMatches(args[1], Arrays.stream(Material.values()).filter(Material::isBlock).filter(Material::isItem).filter(material -> material != Material.AIR && material != Material.VOID_AIR && material != Material.CAVE_AIR).map(Material::name).collect(Collectors.toList()), new ArrayList<>());
                case "warning":
                case "destroy_bedrock":
                    return StringUtil.copyPartialMatches(args[1], booleanOptions, new ArrayList<>());
            }
        }

        return null;
    }
}