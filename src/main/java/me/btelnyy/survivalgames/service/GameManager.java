package me.btelnyy.survivalgames.service;

import me.btelnyy.survivalgames.SurvivalGames;
import me.btelnyy.survivalgames.constants.ConfigData;
import me.btelnyy.survivalgames.constants.Globals;
import me.btelnyy.survivalgames.misc.GameState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.ObjectInputFilter;

import static net.kyori.adventure.text.format.TextColor.color;

public class GameManager
{
    public static int gameTimerProcessId;

    public static int gameSeconds = 0;

    public static int gameRunnerProcessId;

    public static boolean hasGameStarted = false;

    public static boolean gameOver = false;

    public static boolean getPvpActive()
    {
        return gameSeconds >= ConfigData.getInstance().peaceTimeSeconds;
    }

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
        if(gameSeconds >= ConfigData.getInstance().borderShrinkTime)
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
                }
                if(getGameState() == GameState.SuddenDeath)
                {
                    component = "Sudden Death! Playing for: ";
                    currentColor = TextColor.color(255, 0, 0);
                    timeLeftText = text;
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

    public static void onTriggerSuddenDeath()
    {

    }

    public static void onTriggerPvp()
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            player.sendMessage(Utils.colored("&eWarning: The world border is shrinking! Run!"));
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
            world.getWorldBorder().setSize(ConfigData.getInstance().borderMinimumSize, ConfigData.getInstance().borderShrinkTime);
        }
        hasGameStarted = true;
    }

    public static void winGame()
    {
        Bukkit.getScheduler().cancelTask(gameTimerProcessId);
        gameOver = true;
    }

    public static void endGame()
    {
        hasGameStarted = false;
        Bukkit.getScheduler().cancelTask(gameTimerProcessId);
    }
}
