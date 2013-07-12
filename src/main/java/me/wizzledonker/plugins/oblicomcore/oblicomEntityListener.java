//Listener for /wanted command (C) WizzleDonker and Oblicom
package me.wizzledonker.plugins.oblicomcore;

import java.util.Random;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class oblicomEntityListener extends EntityListener {
    private static OblicomCore plugin;
    
    public oblicomEntityListener(OblicomCore instance) {
        plugin = instance;
    }
    
    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        Entity killed = event.getEntity();
        if (!(killed instanceof Player)) {
            return;
        }
        Player killedPlayer = (Player) killed;
        Entity killer = killedPlayer.getKiller();
        if (killer instanceof Player) {
            Player pkiller = (Player) killer;
            Random rand = new Random();
            if (plugin.wanted.isInList(killedPlayer)) {
                plugin.wanted.removeFromList(killedPlayer);
            }
            if (pkiller.hasPermission("oblicom.wanted.exempt")) {
                return;
            }
            if (rand.nextBoolean()) {
                plugin.wanted.addToList(pkiller, "murder");
            } else {
                plugin.wanted.addToList(pkiller, "manslaughter");
            }
        }
    }
    
}
