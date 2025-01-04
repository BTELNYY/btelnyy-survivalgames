package me.btelnyy.survivalgames.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.btelnyy.survivalgames.service.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import me.btelnyy.survivalgames.SurvivalGames;
import me.btelnyy.survivalgames.constants.ConfigData;
import me.btelnyy.survivalgames.constants.Globals;
import me.btelnyy.survivalgames.service.Utils;

public class StartGame implements CommandExecutor
{
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board;
    {
        assert manager != null;
        board = manager.getNewScoreboard();
    }

    ConfigData configData = SurvivalGames.getInstance().getConfigData();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg2, String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage(Utils.colored("&cError: &7You must be a player to run this command."));
            return true;
        }
        if(GameManager.hasGameStarted)
        {
            sender.sendMessage(Utils.colored("&cError: &7The game has already started."));
            return true;
        }
        Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), new Runnable()
        {
            @Override
            public void run()
            {

            }
        }, 0, 20);
        GameManager.startGame();
        return true;
    }
}
