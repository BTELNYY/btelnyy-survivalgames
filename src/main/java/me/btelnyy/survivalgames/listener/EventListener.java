package me.btelnyy.survivalgames.listener;

import org.bukkit.event.Listener;

import me.btelnyy.survivalgames.SurvivalGames;
import me.btelnyy.survivalgames.constants.ConfigData;
import me.btelnyy.survivalgames.service.file_manager.Configuration;
import me.btelnyy.survivalgames.service.file_manager.FileID;

public class EventListener implements Listener {
    private static final Configuration language = SurvivalGames.getInstance().getFileManager().getFile(FileID.LANGUAGE).getConfiguration();
    
    public static ConfigData configData = SurvivalGames.getInstance().getConfigData();
}
