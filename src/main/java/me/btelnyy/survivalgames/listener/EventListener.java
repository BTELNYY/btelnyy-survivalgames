package me.btelnyy.survivalgames.listener;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.btelnyy.survivalgames.service.GameManager;
import me.btelnyy.survivalgames.service.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.btelnyy.survivalgames.SurvivalGames;
import me.btelnyy.survivalgames.constants.ConfigData;
import me.btelnyy.survivalgames.service.file_manager.Configuration;
import me.btelnyy.survivalgames.service.file_manager.FileID;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;

public class EventListener implements Listener
{
    private static final Configuration language = SurvivalGames.getInstance().getFileManager().getFile(FileID.LANGUAGE).getConfiguration();
    
    public static ConfigData configData = SurvivalGames.getInstance().getConfigData();

    @EventHandler
    public void OnChat(PlayerChatEvent event)
    {
        if(GameManager.hasGameStarted && !ConfigData.getInstance().allowChatInMatch && !(event.getPlayer().hasPermission("btelnyy.survivalgames.chatingame") || event.getPlayer().isOp()))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Utils.colored("&cChat is disabled."));
        }
    }

    @EventHandler
    public void OnPlayerDamage(EntityDamageByEntityEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
        {
            return;
        }
        if(!GameManager.hasGameStarted)
        {
            return;
        }
        if(GameManager.getPvpActive())
        {
            return;
        }
        if(event.getDamager() instanceof Player attacker)
        {
            event.setCancelled(true);
            return;
        }
        if(event.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player)
        {
            event.setCancelled(true);
            return;
        }
        if(event.getDamager() instanceof TNTPrimed tnt && tnt.getSource() instanceof Player)
        {
            event.setCancelled(true);
            return;
        }
    }
}
