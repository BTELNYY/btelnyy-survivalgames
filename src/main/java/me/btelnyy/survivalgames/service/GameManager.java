package me.btelnyy.survivalgames.service;

import me.btelnyy.survivalgames.SurvivalGames;
import me.btelnyy.survivalgames.constants.ConfigData;
import me.btelnyy.survivalgames.constants.Globals;
import me.btelnyy.survivalgames.misc.GameState;
import me.btelnyy.survivalgames.service.file_manager.Configuration;
import me.btelnyy.survivalgames.service.file_manager.FileID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.ObjectInputFilter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.kyori.adventure.text.format.TextColor.color;

public class GameManager
{
    private static final Configuration language = SurvivalGames.getInstance().getFileManager().getFile(FileID.LANGUAGE).getConfiguration();

    public static int gameTimerProcessId;

    public static int gameSeconds = 0;

    public static int gameRunnerProcessId;

    public static boolean hasGameStarted = false;

    public static boolean gameOver = false;

    public static boolean getPvpActive()
    {
        return gameSeconds >= ConfigData.getInstance().peaceTimeSeconds;
    }

    public static List<UUID> players = new ArrayList<>();

    public static GameState getGameState()
    {
        if(!hasGameStarted)
        {
            return GameState.NotStarted;
        }
        if(!getPvpActive())
        {
            return GameState.Peace;
        }
        if(gameSeconds >= ConfigData.getInstance().borderShrinkTime + ConfigData.getInstance().peaceTimeSeconds)
        {
            return GameState.SuddenDeath;
        }
        return GameState.War;
    }

    public static Runnable gameTimer = new Runnable()
    {
        @Override
        public void run()
        {
            int minutes = gameSeconds / 60;
            int seconds = gameSeconds % 60;
            String text = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
            int timeLeft = getPvpActive() ? ConfigData.getInstance().borderShrinkTime - (gameSeconds - ConfigData.getInstance().peaceTimeSeconds) : ConfigData.getInstance().peaceTimeSeconds - gameSeconds;
            int minutesLeft = timeLeft / 60;
            int secondsLeft = timeLeft % 60;
            String timeLeftText = String.format("%02d", minutesLeft) + ":" + String.format("%02d", secondsLeft);
            if(ConfigData.getInstance().showTimer && hasGameStarted)
            {
                String component = "Time until PvP is enabled: ";
                TextColor currentColor = TextColor.color(0, 255, 0);
                if(getGameState() == GameState.War)
                {
                    component = "Time until Sudden Death: ";
                    currentColor = TextColor.color(255, 204, 0);
                    onTriggerPvpLoop();
                }
                if(getGameState() == GameState.SuddenDeath)
                {
                    component = "Sudden Death! Playing for: ";
                    currentColor = TextColor.color(255, 0, 0);
                    timeLeftText = text;
                    onTriggerSuddenDeathLoop();
                }
                for(Player p : Bukkit.getOnlinePlayers())
                {
                    p.sendActionBar(
                            Component.text(component + timeLeftText, currentColor));
                }
            }
            if(gameSeconds == ConfigData.getInstance().borderShrinkTime + ConfigData.getInstance().peaceTimeSeconds)
            {
                onTriggerSuddenDeath();
            }
            if(gameSeconds == ConfigData.getInstance().peaceTimeSeconds)
            {
                onTriggerPvp();
            }
            gameSeconds++;
        }
    };

    public static void onTriggerSuddenDeathLoop()
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            if(player.getGameMode() != GameMode.SURVIVAL)
            {
                continue;
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
        }
    }

    public static void onTriggerSuddenDeath()
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            if(player.getGameMode() != GameMode.SURVIVAL)
            {
                continue;
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
            if(player.getLocation().getWorld().getEnvironment() == World.Environment.NETHER)
            {
                World world = Bukkit.getWorld(ConfigData.getInstance().worldName);
                if(world == null)
                {
                    world = Bukkit.getWorlds().get(0);
                }
                player.teleport(world.getSpawnLocation());
            }
        }
    }

    public static void onTriggerPvpLoop()
    {

    }

    public static void onTriggerPvp()
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            player.sendMessage(Utils.colored(language.getString("border_shrink_warning")));
        }
        for(World world : Bukkit.getWorlds())
        {
            world.getWorldBorder().setSize(ConfigData.getInstance().borderMinimumSize, ConfigData.getInstance().borderShrinkTime);
        }
    }

    public static void startGame()
    {
        gameTimerProcessId = Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), gameTimer, 0, 20).getTaskId();
        for(World world : Bukkit.getWorlds())
        {
            world.getWorldBorder().setSize(ConfigData.getInstance().borderStartSize, 0);
            world.getWorldBorder().setCenter(0, 0);
        }
        World world = Bukkit.getWorld(ConfigData.getInstance().worldName);
        if(world == null)
        {
            world = Bukkit.getWorlds().get(0);
        }
        for(Player player : Bukkit.getOnlinePlayers())
        {
            player.setWhitelisted(true);
            if(player.getGameMode() != GameMode.SPECTATOR)
            {
                player.setGameMode(GameMode.SURVIVAL);
            }
            player.teleport(world.getSpawnLocation());
            player.getInventory().addItem(new ItemStack(Material.OAK_BOAT, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ConfigData.getInstance().spawnEffectDuration * 20, 2, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ConfigData.getInstance().spawnEffectDuration * 20, 0, false, false ));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 60 * 20, 255, false, false));
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:overworld run spreadplayers {spawnX} {spawnZ} {midPoint} {maxPoint} false @a".replace("{spawnX}", String.valueOf(world.getSpawnLocation().getX())).replace("{spawnZ}", String.valueOf(world.getSpawnLocation().getZ())).replace("{midPoint}", String.valueOf((ConfigData.getInstance().borderStartSize / 4))).replace("{maxPoint}",String.valueOf(ConfigData.getInstance().borderStartSize / 2)));
        Bukkit.getServer().setWhitelist(true);
        hasGameStarted = true;
    }

    public static void winGame()
    {
        Bukkit.getScheduler().cancelTask(gameTimerProcessId);
        for(Player player : Bukkit.getOnlinePlayers())
        {
            if(player.getGameMode() == GameMode.SURVIVAL)
            {
                player.showTitle(new Title()
                {
                    @Override
                    public @NotNull Component title()
                    {
                        return Component.text(language.getString("victory_title"),TextColor.color(255, 215, 0));
                    }

                    @Override
                    public @NotNull Component subtitle()
                    {
                        return Component.text(language.getString("victory_subtitle"));
                    }

                    @Override
                    public @Nullable Times times()
                    {
                        return Times.times(Duration.ofSeconds(1), Duration.ofSeconds(10), Duration.ofSeconds(1));
                    }

                    @Override
                    public <T> @UnknownNullability T part(@NotNull TitlePart<T> part)
                    {
                        return null;
                    }
                });
            }
            else
            {
                player.showTitle(new Title()
                {
                    @Override
                    public @NotNull Component title()
                    {
                        return Component.text(language.getString("defeat_title"), TextColor.color(255, 0, 0));
                    }

                    @Override
                    public @NotNull Component subtitle()
                    {
                        return Component.text(language.getString("defeat_subtitle"));
                    }

                    @Override
                    public @Nullable Times times()
                    {
                        return Times.times(Duration.ofSeconds(1), Duration.ofSeconds(10), Duration.ofSeconds(1));
                    }

                    @Override
                    public <T> @UnknownNullability T part(@NotNull TitlePart<T> part)
                    {
                        return null;
                    }
                });
            }
        }
        gameOver = true;
    }

    public static void endGame()
    {
        hasGameStarted = false;
        Bukkit.getScheduler().cancelTask(gameTimerProcessId);
    }
}
