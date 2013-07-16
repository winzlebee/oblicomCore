package com.oblicom.plugins.oblicomcore.entity;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

import com.oblicom.plugins.oblicomcore.event.citizen.CitizenStolenEvent;
import com.oblicom.plugins.oblicomcore.event.criminal.CriminalStealEvent;
import com.oblicom.plugins.oblicomcore.event.criminal.CriminalStealFailEvent;
import com.oblicom.plugins.oblicomcore.event.criminal.CriminalLockpickEvent;
import com.oblicom.plugins.oblicomcore.event.criminal.CriminalLockpickFailEvent;

public class Criminal extends Citizen {
    
    public Criminal(Player player) {
        super(player);
    }

    /**
     * Calculate the success % of steal
     * 
     * @return percent chance
     */        
    private int stealChance() {
        int bonusChance = (player.getEquipment().getBoots().getType() == Material.GOLD_BOOTS) ? 5 : 0;        
        return configuration.getInt("criminal.pickpocket.chance") + bonusChance;
    }
    
    /**
     * Condition to execute the steal.
     * 
     */    
    public boolean actionIsValidToSteal() {
        return player.isSneaking() && player.getItemInHand().getType() == Material.AIR;
    }    
    
    /**
     * Steal other citizen.
     * 
     * @param citizen the citizen to stolen
     */    
    public double steal(Citizen citizenVictim) {
        if (player.hasPermission("oblicom.pickpocket.steal")) {
            pluginManager.callEvent(new CriminalStealFailEvent(this, "without_permission"));
            return 0;
        }
        
        if (citizenVictim.player.hasPermission("oblicom.pickpocket.nosteal")) {
            pluginManager.callEvent(new CriminalStealFailEvent(this, "victim_sealed"));
            return 0;
        }

        if (player.getLevel() < configuration.getInt("criminal.pickpocket.experience")) {
            pluginManager.callEvent(new CriminalStealFailEvent(this, "without_experience"));
            return 0;
        }

        if (world.getStealer().isInList(this)) {
            pluginManager.callEvent(new CriminalStealFailEvent(this, "steal_recently"));
            return 0;
        }
        
        Random chance = new Random();
        
        if (chance.nextInt(100) >= stealChance()) {
            pluginManager.callEvent(new CriminalStealFailEvent(this, "victim_alerted"));
            pluginManager.callEvent(new CitizenStolenEvent(citizenVictim, this, true));
            
            player.damage(configuration.getInt("criminal.pickcpocket.damage"));
            
            wanted("pickpocketing");
            citizenVictim.addScore(configuration.getInt("criminal.pickcpocket.score"));

            return 0;
        }
        
        double stolenAmount = 0;
        
        if (citizenVictim.getMoney() > 0) {
            double amount = (new Random()).nextInt(configuration.getInt("criminal.pickpocket.max")) + 1;
            stolenAmount = citizenVictim.takeMoney(amount);

            giveMoney(stolenAmount);

            player.setLevel(player.getLevel() - configuration.getInt("criminal.pickpocket.experience"));
        }
        
        world.getStealer().addToList(this, configuration.getInt("criminal.pickpocket.time"));

        pluginManager.callEvent(new CriminalStealEvent(this, citizenVictim, stolenAmount));
        pluginManager.callEvent(new CitizenStolenEvent(citizenVictim, this, false));

        return stolenAmount;
    };

    /**
     * Calculate the success % of steal
     * 
     * @return percent chance
     */        
    private int lockpickChance() {
        int bonusChance = (player.getEquipment().getBoots().getType() == Material.GOLD_BOOTS) ? 5 : 0;        
        return configuration.getInt("criminal.pickpocket.chance") + bonusChance;
    }
    
    /**
     * Condition to execute the lockpick.
     * 
     */    
    public boolean actionIsValidToLockpick(Action action, Block block) {
        return action.equals(Action.RIGHT_CLICK_BLOCK) && (block.getType().equals(Material.CHEST) || block.getType().equals(Material.WOODEN_DOOR));
    }    
    
    /**
     * Lockpick chests and wooden doors
     * 
     * @param item to lockpick
     */    
    public boolean lockpick(Block item) {        
        if (player.hasPermission("oblicom.lockpick.pick")) {
            pluginManager.callEvent(new CriminalLockpickFailEvent(this, "without_permission"));
            return false;
        }

        ItemStack itemInHand = player.getItemInHand();
        
        if (itemInHand.getTypeId() != configuration.getInt("criminal.lockpick.item")) {
            pluginManager.callEvent(new CriminalLockpickFailEvent(this, "wrong_item"));
            return false;
        }
        
        if (player.getLevel() < configuration.getInt("criminal.lockpick.experience")) {
            pluginManager.callEvent(new CriminalLockpickFailEvent(this, "without_experience"));
            return false;
        }
        
        if (world.getLockpicker().isInList(this)) {
            pluginManager.callEvent(new CriminalLockpickFailEvent(this, "lockpick_recently"));
            return false;
        }

        Random chance = new Random();
        
        if (chance.nextInt(100) >= lockpickChance()) {
            pluginManager.callEvent(new CriminalLockpickFailEvent(this, "lockpick_fail"));
            
            player.damage(configuration.getInt("criminal.lockpick.damage"));
            
            int amountItensInHand = itemInHand.getAmount();
            
            if (amountItensInHand <= 1) {
                player.setItemInHand(null);
            } else {
                player.getItemInHand().setAmount(amountItensInHand - 1);
            }
            
            return false;
        }
        
        player.setLevel(player.getLevel() - configuration.getInt("criminal.lockpick.experience"));
        wanted("theft");
        addScore(configuration.getInt("criminal.lockpick.score"));
        
        world.getLockpicker().addToList(this, configuration.getInt("criminal.lockpick.time"));

        pluginManager.callEvent(new CriminalLockpickEvent(this));
        
        return true;
    };
}
