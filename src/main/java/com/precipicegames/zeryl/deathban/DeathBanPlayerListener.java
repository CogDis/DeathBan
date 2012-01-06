package com.precipicegames.zeryl.deathban;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;

/**
 *
 * @author Zeryl
 */
public class DeathBanPlayerListener extends PlayerListener {

    private final DeathBan plugin;

    public DeathBanPlayerListener(DeathBan instance) {
        plugin = instance;
    }

    @Override
    public void onPlayerPreLogin(PlayerPreLoginEvent event) {
        if(plugin.config.isSet("players." + event.getName() + ".death")) {
            Timestamp unban = plugin.getUnban(event.getName());

            Date date = new Date();
            Timestamp now = new Timestamp(date.getTime());
            
            System.out.println("Comparing " + now.toString() + " to " + unban.toString());
            if(now.after(unban)) {
                plugin.config.set("players." + (String) event.getName() + ".death", null);
                return;
            }
            event.setResult(PlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("Sorry, you've died, you can login at " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(unban));
        }
    }
}