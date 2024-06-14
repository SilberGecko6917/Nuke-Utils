package de.silbergecko.nukeplugin.listener;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import de.silbergecko.nukeplugin.Main;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ExplosionListeners implements Listener {

    private final Main plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, BlockPlaceEvent> blockEvents = new HashMap<>();

    public ExplosionListeners(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();

        if (!player.hasPermission("nukeplugin.nuke")) {
            return;
        }

        String blockType = plugin.getConfig().getString("material", "TNT");
        Material material = Material.getMaterial(blockType.toUpperCase());
        int cooldown = plugin.getConfig().getInt("cooldown", 60);

        if (material == null) {
            Bukkit.getConsoleSender().sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4§lError: Invalid material in config.yml");
            return;
        }

        if (event.getBlock().getType() != material) {
            return;
        }

        final long finalCooldown = cooldown < 0 ? 0 : Math.min(cooldown, 3600);

        if (cooldowns.containsKey(playerID)) {
            long secondsLeft = ((cooldowns.get(playerID) / 1000) + finalCooldown) - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                player.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4You can't use this for another §7§l" + secondsLeft + "§4 seconds!");
                event.setCancelled(true);
                return;
            }
        }

        try {
            int rawPower = plugin.getConfig().getInt("power", 4);
            boolean warning = plugin.getConfig().getBoolean("warning", true);

            final int power = Math.max(1, Math.min(rawPower, 150));

            if (warning) {
                player.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4§lWarning: You are about to place a NUKE with power " + power);
                player.openInventory(createNukeConfirmInventory());
                blockEvents.put(playerID, event);
            } else {
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);
                cooldowns.put(playerID, System.currentTimeMillis());

                // Countdown before explosion
                List<Player> nearbyPlayers = getNearbyPlayers(event.getBlock().getLocation(), power + 10);
                for (int i = 5; i >= 0; i--) {
                    final int countdownNumber = i;

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (countdownNumber == 0) {
                            createCircularExplosion(event.getBlock(), power);
                            cooldowns.put(playerID, System.currentTimeMillis() + cooldown * 1000L);

                            for (Player nearbyPlayer : nearbyPlayers) {
                                nearbyPlayer.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4§lA nuke has exploded nearby!");
                            }
                        } else {
                            for (Player nearbyPlayer : nearbyPlayers) {
                                nearbyPlayer.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4§lNuke explosion in §7§l" + countdownNumber + "§4§l seconds!");
                            }
                        }
                    }, (5 - countdownNumber) * 20L);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("An error occurred while trying to create a nuke: " + e.getMessage());
        }
    }



    public Inventory createNukeConfirmInventory() {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, "§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4§lConfirm Nuke");

        ItemStack confirmItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirmItem.getItemMeta();

        if (confirmMeta != null) {
            confirmMeta.setDisplayName("§4§lConfirm Nuke");
            confirmItem.setItemMeta(confirmMeta);
        }

        inv.setItem(13, confirmItem);

        return inv;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4§lConfirm Nuke")) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        UUID playerID = player.getUniqueId();

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.RED_STAINED_GLASS_PANE) {
            player.closeInventory();
            player.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4§lNuke cancelled.");
            return;
        }

        int rawPower = plugin.getConfig().getInt("power", 4);
        final int power = Math.max(1, Math.min(rawPower, 150));

        player.closeInventory();

        BlockPlaceEvent blockEvent = blockEvents.get(playerID);
        if (blockEvent != null) {
            cooldowns.put(playerID, System.currentTimeMillis());

            List<Player> nearbyPlayers = getNearbyPlayers(blockEvent.getBlock().getLocation(), power + 10);
            for (int i = 5; i >= 0; i--) {
                final int countdownNumber = i;

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Player nearbyPlayer : nearbyPlayers) {
                        nearbyPlayer.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4§lNuke explosion in §7§l" + countdownNumber + "§4§l seconds!");
                    }
                }, (5 - countdownNumber) * 20L);
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                createCircularExplosion(blockEvent.getBlock(), power);
                cooldowns.put(playerID, System.currentTimeMillis());

                for (Player nearbyPlayer : nearbyPlayers) {
                    nearbyPlayer.sendMessage("§7[§x§4§F§0§0§0§0§lN§x§8§0§0§0§0§0§lU§x§B§0§0§0§0§0§lK§x§C§1§0§0§0§0§lE§x§D§2§0§0§0§0§lR§7] §4§lA nuke has exploded nearby!");
                }
            }, 100L);
        }
    }

    private List<Player> getNearbyPlayers(Location location, double radius) {
        List<Player> nearbyPlayers = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(location.getWorld()) &&
                    player.getLocation().distance(location) <= radius) {
                nearbyPlayers.add(player);
            }
        }

        return nearbyPlayers;
    }

    private void createCircularExplosion(Block block, int radius) {
        World world = block.getWorld();
        Location center = block.getLocation();
        boolean destroyBedrock = plugin.getConfig().getBoolean("destroy_bedrock", false);
        int explosionDamage = radius * 2;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (Math.sqrt(x * x + y * y + z * z) <= radius) {
                            Block blockToChange = world.getBlockAt(center.clone().add(x, y, z));
                            if (blockToChange.getType() != Material.VOID_AIR && (blockToChange.getType() != Material.BEDROCK || destroyBedrock)) {
                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    blockToChange.setType(Material.AIR);
                                    world.spawnParticle(Particle.EXPLOSION_LARGE, blockToChange.getLocation(), 1);
                                    world.playSound(blockToChange.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);

                                    for (Entity entity : world.getNearbyEntities(blockToChange.getLocation().add(0.5, 0.5, 0.5), 1, 1, 1)) {
                                        if (entity instanceof LivingEntity) {
                                            ((LivingEntity) entity).damage(explosionDamage);
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }
}

