package me.btelnyy.survivalgames.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.ChatEvent;
import me.btelnyy.survivalgames.misc.CombatLoggerData;
import me.btelnyy.survivalgames.service.GameManager;
import me.btelnyy.survivalgames.service.Utils;
import me.btelnyy.survivalgames.service.file_manager.FileID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.btelnyy.survivalgames.SurvivalGames;
import me.btelnyy.survivalgames.constants.ConfigData;
import me.btelnyy.survivalgames.service.file_manager.Configuration;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class EventListener implements Listener
{
    private static final Configuration language = SurvivalGames.getInstance().getFileManager().getFile(FileID.LANGUAGE).getConfiguration();
    
    public static ConfigData configData = SurvivalGames.getInstance().getConfigData();

    public static HashMap<UUID, Integer> playerToLastDamageEvent = new HashMap<>();

    public static HashMap<UUID, CombatLoggerData> combatLoggers = new HashMap<>();

    @EventHandler
    public void onPlayerDamaged(EntityDamageByEntityEvent event)
    {
        if(!GameManager.hasGameStarted)
        {
            return;
        }
        if(!(event.getEntity() instanceof Player))
        {
            return;
        }
        combatLoggers.remove(event.getEntity().getUniqueId());
        Player attacker = Utils.tryGetAttacker(event);
        CombatLoggerData combatLoggerData = new CombatLoggerData(event.getDamage());
        if(attacker != null)
        {
            combatLoggerData = new CombatLoggerData(attacker.getUniqueId(), event.getDamage());
        }
        combatLoggers.put(event.getEntity().getUniqueId(), combatLoggerData);
        playerToLastDamageEvent.remove(event.getEntity().getUniqueId());
        playerToLastDamageEvent.put(event.getEntity().getUniqueId(), GameManager.gameSeconds);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if(!GameManager.hasGameStarted)
        {
            return;
        }
        if(!playerToLastDamageEvent.containsKey(event.getPlayer().getUniqueId()))
        {
            return;
        }
        int lastSecond = playerToLastDamageEvent.get(event.getPlayer().getUniqueId());
        //deal damage, player is combat logging
        if(lastSecond > GameManager.gameSeconds - configData.combatLoggerDamageTrackTime)
        {
            double damage;
            Player attacker;
            UUID defender = event.getPlayer().getUniqueId();
            if(combatLoggers.containsKey(event.getPlayer().getUniqueId()))
            {
                CombatLoggerData data = combatLoggers.get(event.getPlayer().getUniqueId());
                damage = data.wasPlayer ? data.damage * configData.combatLoggerDamageMultiplierPlayer : data.damage * configData.combatLoggerDamageMultiplierEnvironment;
                attacker = data.wasPlayer ? Bukkit.getPlayer(data.attacker) : event.getPlayer();
            }
            else
            {
                damage = 10d;
                attacker = event.getPlayer();
            }
            Runnable runnable = () ->
            {
                Player target = Bukkit.getPlayer(defender);
                if(target != null)
                {
                    target.damage(damage, attacker);
                    Bukkit.getServer().broadcastMessage(Utils.colored(language.getString("hurt_combat_log").replace("{player}", target.getName())));
                }
            };
            Bukkit.getScheduler().runTaskLater(SurvivalGames.getInstance(), runnable, 200L);
        }
    }

    @EventHandler
    public void onChat(ChatEvent event)
    {
        if(GameManager.hasGameStarted && !ConfigData.getInstance().allowChatInMatch && !(event.getPlayer().hasPermission("btelnyy.survivalgames.chatingame") || event.getPlayer().isOp()) && event.getPlayer().getGameMode() == GameMode.SPECTATOR)
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Utils.colored(language.getString("chat_disabled")));
            return;
        }
        if(!GameManager.hasGameStarted)
        {
            return;
        }
        Player sender = event.getPlayer();
        Collection<Player> whoGotIt = sender.getLocation().getNearbyPlayers(ConfigData.getInstance().chatRadius);
    }

    public static HashMap<UUID, Location> playerDeathPositions = new HashMap<>();

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent event)
    {
        if(!(event.getEntity() instanceof Player player))
        {
            return;
        }
        if(event.getFinalDamage() < player.getHealth())
        {
            return;
        }
        playerDeathPositions.remove(event.getEntity().getUniqueId());
        playerDeathPositions.put(event.getEntity().getUniqueId(), event.getEntity().getLocation());
        if(GameManager.hasGameStarted && Bukkit.getOnlinePlayers().stream().filter(x -> x.getGameMode() == GameMode.SURVIVAL).count() == 1)
        {
            GameManager.winGame();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        if(!GameManager.hasGameStarted)
        {
            return;
        }
        event.getPlayer().setGameMode(GameMode.SPECTATOR);
        Location destination = playerDeathPositions.get(event.getPlayer().getUniqueId());
        destination = destination == null ? event.getPlayer().getLocation() : destination;
        event.getPlayer().teleport(destination);
        event.getPlayer().showTitle(new Title()
        {
            @Override
            public @NotNull Component title()
            {
                return Component.text(language.getString("death_title"), TextColor.color(255, 0, 0));
            }

            @Override
            public @NotNull Component subtitle()
            {
                return Component.text(language.getString("death_subtitle"));
            }

            @Override
            public @Nullable Times times()
            {
                return Times.times(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(3));
            }

            @Override
            public <T> @UnknownNullability T part(@NotNull TitlePart<T> part)
            {
                return null;
            }
        });
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event)
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
        Player attacker = Utils.tryGetAttacker(event);
        if(attacker != null)
        {
            event.setCancelled(true);
        }
    }
}
