package de.silbergecko.nukeplugin;

import de.silbergecko.nukeplugin.commands.SettingsCommand;
import de.silbergecko.nukeplugin.listener.ExplosionListeners;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    public Main() {
    }

    @Override
    public void onEnable() {

        String blockType = getConfig().getString("material", "TNT");
        Material material = Material.getMaterial(blockType.toUpperCase());

        if (material == null) {
            Bukkit.getConsoleSender().sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4§lError: Invalid material in config.yml");
            return;
        }

        // Plugin startup logic

        saveDefaultConfig();

        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("§8§m----------------------------------------");
        Bukkit.getConsoleSender().sendMessage("§7Plugin §8⊳ §4NukePlugin ");
        Bukkit.getConsoleSender().sendMessage("§7Developer §8⊳ §9SilberGecko ");
        Bukkit.getConsoleSender().sendMessage("§7Version §8⊳ §61.1 ");
        Bukkit.getConsoleSender().sendMessage("§8§m----------------------------------------");
        Bukkit.getConsoleSender().sendMessage(" ");

        Bukkit.getPluginManager().registerEvents(new ExplosionListeners(this), this);

        SettingsCommand settingsCommand = new SettingsCommand(this);
        Objects.requireNonNull(this.getCommand("nuke-settings")).setExecutor(settingsCommand);
        Objects.requireNonNull(this.getCommand("nuke-settings")).setTabCompleter(settingsCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}