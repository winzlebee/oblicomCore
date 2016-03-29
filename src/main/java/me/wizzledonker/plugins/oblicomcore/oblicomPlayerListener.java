/*
 * Oblicom player listener (C) WizzleDonker 2012
 */
package me.wizzledonker.plugins.oblicomcore;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class oblicomPlayerListener implements Listener {
    private static OblicomCore plugin;
    
    public oblicomPlayerListener(OblicomCore instance) {
        plugin = instance;
    }
    
    private Set<UUID> noLockPick = new HashSet<UUID>();
    private Set<UUID> noLocatorStick = new HashSet<UUID>();
    
    @EventHandler
    public void whenPlayerInteracts(PlayerInteractEntityEvent event) {
        Entity targetEntity = event.getRightClicked();
        Random r = new Random();
        
        if (targetEntity instanceof Player) {
            final Player thief = event.getPlayer();
            Player victim = (Player) event.getRightClicked();
            
            if (thief.isSneaking()) {
                if (thief.hasPermission("oblicom.pickpocket.steal")) {
                    if (victim.hasPermission("oblicom.pickpocket.nosteal")) {
                        thief.sendMessage(ChatColor.RED + "Thou shall not steal from this person!");
                        return;
                    }    
                    if (thief.getItemInHand().getType() == Material.AIR) {
                        if (thief.getLevel() < plugin.experience) {
                            thief.sendMessage(ChatColor.YELLOW + "You don't have enough experience to pickpocket! You need to be level " + plugin.experience);
                            return;
                        }
                        if (plugin.notAllowed.contains(thief.getUniqueId())) {
                            thief.sendMessage(ChatColor.DARK_RED + "Cool it! You can't steal that often.");
                            return;
                        }
                        if (r.nextInt(100) <= plugin.chance) {
                            plugin.pickPocket(thief, victim);
                        } else {
                            pickPocketAlert(thief, victim);
                        }
                        plugin.notAllowed.add(thief.getUniqueId());
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                public void run() {
                                    plugin.notAllowed.remove(thief.getUniqueId());
                                }
                        }, plugin.time*60*20L);
                        thief.setLevel(thief.getLevel() - plugin.experience);
                    }
                } else {
                    thief.sendMessage(ChatColor.DARK_GRAY + "You find yourself unable to steal money.");
                }
            }
            
        }
        
        
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (noLocatorStick.contains(player.getUniqueId())) {
                return;
            }
            if (player.hasPermission("oblicom.wanted.locator")) {
                if (player.getItemInHand().getType().equals(Material.STICK)) {
                    player.sendMessage(ChatColor.GOLD + "Follow the lightning for wanted players!");
                    for (Player play : plugin.getServer().getOnlinePlayers()) {
                        if (plugin.wanted.isInList(play.getName())) {
                            play.getWorld().strikeLightningEffect(play.getLocation());
                        }
                    }
                    noLocatorStick.add(player.getUniqueId());
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            noLocatorStick.remove(player.getUniqueId());
                        }
                    }, plugin.locator_time*20L);
                }
            }
        }
        if (!player.hasPermission("oblicom.lockpick.pick")) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType().equals(Material.CHEST)) {
            if (!event.isCancelled()) return;
            if (player.getItemInHand().getTypeId() != plugin.lockpick_item) {
                player.sendMessage("Wrong item for lock-picking! Use an " + Material.getMaterial(plugin.lockpick_item).name());
                return;
            }
            if (player.getLevel() < plugin.lockpick_experience) {
                player.sendMessage(ChatColor.YELLOW + "You don't have enough experience to steal! You need to be level " + plugin.lockpick_experience);
                return;
            }
            if (noLockPick.contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Cool it! You can't steal that often!");
                return;
            }
            Random rand = new Random();
            if (rand.nextInt(100) <= plugin.lockpick_chance) {
                player.sendMessage(ChatColor.DARK_GREEN + "Success! You have picked this lock. You have recieved a random item.");
                Chest chest = (Chest) block.getState();
                int num = chest.getInventory().getSize();
                
                boolean found = false;
                
                //Random item from the chest
                while (found = false) {
                    int randomItem = rand.nextInt(num);
                    if (chest.getInventory().getItem(randomItem).getType() != Material.AIR) {
                        player.getInventory().addItem(chest.getInventory().getItem(randomItem));
                        chest.getInventory().setItem(randomItem, null);
                        found = true;
                    }
                }
                
                plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "Fear spreads through the land as news of a lockpick arrives!");
                plugin.wanted.addToList(player.getName(), "theft", plugin.wanted_time_steal);
                plugin.scores.addScore(plugin.lockpick_score, player);
            } else {
                player.sendMessage(ChatColor.RED + "You fiddle with the lock and eventually fail, hurting your finger.");
                player.damage(plugin.lockpick_damage);
                if (player.getItemInHand().getAmount() <= 1) {
                    player.setItemInHand(null);
                } else {
                    player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                }
            }
            noLockPick.add(player.getUniqueId());
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        noLockPick.remove(player.getUniqueId());
                    }
            }, plugin.lockpick_time*60*20L);
            player.setLevel(player.getLevel() - plugin.lockpick_experience);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (plugin.jail.jailed.contains(player.getUniqueId())) {
            plugin.log("A player was jailed!");
            event.setRespawnLocation(plugin.jail_location);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        if (plugin.jail.jailed.contains(player.getUniqueId())) {
            player.sendMessage("You are jailed! You can't break blocks!");
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0].replaceFirst("/", "");

        if (plugin.getConfig().getStringList("jail.allowed-commands").contains(command)) {
            return;
        }
        
        if (plugin.jail.jailed.contains(player.getUniqueId())) {
            player.sendMessage("You are jailed! You can't use commands!");
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof Player) {
                Player damplayer = (Player) event.getEntity();
                Player player = (Player) event.getDamager();
                if (plugin.wanted.isInList(player.getName())) {
                    event.setDamage(event.getDamage()*plugin.wanted_player_nerf);
                }
                
                if (plugin.jail.jailed.contains(player.getUniqueId())) {
                    player.sendMessage("You are jailed! You can't hurt inmates!");
                    damplayer.sendMessage(ChatColor.RED + "You were abused by " + player.getName());
                    event.setCancelled(true);
                }
            }
        }
    }
    
    private void pickPocketAlert(Player thief, Player victim) {
        String alertMsg = plugin.humiliate_message;
        
        alertMsg = alertMsg.replace("%thief%", thief.getName());
        alertMsg = alertMsg.replace("%victim%", victim.getName());
        
        plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE + alertMsg);
        thief.damage(plugin.damage);
        plugin.wanted.addToList(thief.getName(), "pickpocketing", plugin.wanted_time_pickpocket);
        plugin.scores.addScore(plugin.score/2, victim);
    }
    
}
