package com.precipicegames.zeryl.deathban;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.configuration.file.FileConfiguration;

public class DeathBan extends JavaPlugin {

    public FileConfiguration config;
    private final DeathBanPlayerListener playerListener = new DeathBanPlayerListener(this);
    private final DeathBanEntityListener entityListener = new DeathBanEntityListener(this);
    private File configFile = new File(this.getDataFolder(), "config.yml");

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdf = this.getDescription();
        buildConfig();
        pm.registerEvent(Event.Type.PLAYER_PRELOGIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);

        System.out.println(pdf.getName() + " is now enabled");
    }

    public void onDisable() {
        PluginDescriptionFile pdf = this.getDescription();
        saveConfig();
        System.out.println(pdf.getName() + " is now disbled");
    }

    private void buildConfig() {
        config = getConfig();

        if (!config.isSet("time")) {
            config.set("time", 60 * 60 * 24 * 4 * 1000);
        }
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("dban")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("pardon") && (sender.isOp() || !(sender instanceof Player))) {
                    if (this.config.isSet("players." + (String) args[1] + ".death")) {
                        this.config.set("players." + (String) args[1] + ".death", null);
                        this.config.set((String) args[1], null);
                        sender.sendMessage("Player " + (String) args[1] + " has been pardoned;");
                        saveConfig();
                    } else {
                        sender.sendMessage("Player is not banned.");
                    }
                } else if ((args[0].equalsIgnoreCase("check")) && args[1].length() > 1) {
                    Long death = this.config.getLong("players." + (String) args[1] + ".death");
                    if (death == null || death == 0) {
                        sender.sendMessage("This player has no current death ban");
                        return true;
                    }

                    //When can we unban
                    Date date = new Date();
                    Timestamp now = new Timestamp(date.getTime());
                    Timestamp unbantime = this.getUnban((String) args[1]);

                    if (now.after(unbantime)) {
                        this.config.set("players." + (String) args[1] + ".death", null);
                        sender.sendMessage("Player has no current death ban.");
                        return true;
                    }
                    String pretty = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(unbantime);
                    sender.sendMessage("This player has a ban set to expire on " + pretty);
                    return true;

                }
            }
        }
        return true;
    }

    public Timestamp getUnban(String playerName) {
        if (this.config.isSet("players." + playerName + ".death")) {
            Long death = this.config.getLong("players." + playerName + ".death");
            Long time = this.config.getLong("time");

            //When can we unban
            Long unbanlong = death + time;

            Timestamp unbantime = new Timestamp(unbanlong);
            return unbantime;
        }
        return null;
    }
}