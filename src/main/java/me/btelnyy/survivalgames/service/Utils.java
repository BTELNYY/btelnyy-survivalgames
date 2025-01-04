package me.btelnyy.survivalgames.service;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Utils {
    /*
    Allows you to use colours in messages like
    "&cHello!"
    Which would be red
     */
    public static String coloured(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String colored(String str) {
        return coloured(str);
    }

    public static Player tryGetAttacker(EntityDamageByEntityEvent event)
    {
        if(event.getDamager() instanceof Player attacker)
        {
            return attacker;
        }
        if(event.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player shooter)
        {
            return shooter;
        }
        if(event.getDamager() instanceof TNTPrimed tnt && tnt.getSource() instanceof Player igniter)
        {
            return igniter;
        }
        return null;
    }

    public static String buildMessage(String[] parts, boolean ignorefirst) {
        String message = "";
        if(ignorefirst){
            String[] yourArray = Arrays.copyOfRange(parts, 1, parts.length);
            for(String part : yourArray){
                message += part + " ";
            }
        }else{
            for (String part : parts) {
                message += part + " ";
            }
        }
        return message;
    }
}
