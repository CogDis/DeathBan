package com.precipicegames.zeryl.deathban;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

/**
 *
 * @author Zeryl
 */
public class DeathBanEntityListener extends EntityListener {
    
    private final DeathBan plugin;

    public DeathBanEntityListener(DeathBan instance) {
        plugin = instance;
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            String deathnode = "players." + player.getName() + ".death";
            Long dlong = new Date().getTime();
            plugin.config.set(deathnode, dlong);
            plugin.saveConfig();
            
            Timestamp unbantime = plugin.getUnban(player.getName().toString());
            String pretty = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(unbantime);
            player.kickPlayer("Sorry, you've died, you can rejoin on " + pretty);
        }
    }
}
