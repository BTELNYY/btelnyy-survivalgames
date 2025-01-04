package me.btelnyy.survivalgames.constants;

import me.btelnyy.survivalgames.service.file_manager.Configuration;

public class ConfigData
{
    private static ConfigData instance;

    public int peaceTimeSeconds = 1800;

    public int borderShrinkTime = 1800;

    public int borderMinimumSize = 1;

    public boolean showTimer = true;

    public int borderStartSize = 6000;

    public boolean allowChatInMatch = false;

    public void load(Configuration config) {
        peaceTimeSeconds = config.getInt("peace_time", peaceTimeSeconds);
        borderShrinkTime = config.getInt("border_shrink_time", borderShrinkTime);
        borderMinimumSize = config.getInt("border_minimum_size", borderMinimumSize);
        showTimer = config.getBoolean("show_timer", showTimer);
        borderStartSize = config.getInt("border_start_size", borderStartSize);
        allowChatInMatch = config.getBoolean("allow_chat_in_match", allowChatInMatch);
        instance = this;
    }

    public static ConfigData getInstance(){
        return instance;
    }
}
