package me.btelnyy.survivalgames.constants;

import me.btelnyy.survivalgames.service.file_manager.Configuration;

public class ConfigData
{
    private static ConfigData instance;

    public int peaceTimeSeconds = 1800;

    public int borderShrinkTime = 1800;

    public int borderMinimumSize = 15;

    public boolean showTimer = true;

    public int borderStartSize = 6000;

    public boolean allowChatInMatch = false;

    public double combatLoggerDamageMultiplierEnvironment = 1.5f;

    public double combatLoggerDamageMultiplierPlayer = 10f;

    public int combatLoggerDamageTrackTime = 10;

    public String worldName = "world";

    public int spawnEffectDuration = 300;

    public void load(Configuration config) {
        peaceTimeSeconds = config.getInt("peace_time", peaceTimeSeconds);
        borderShrinkTime = config.getInt("border_shrink_time", borderShrinkTime);
        borderMinimumSize = config.getInt("border_minimum_size", borderMinimumSize);
        showTimer = config.getBoolean("show_timer", showTimer);
        borderStartSize = config.getInt("border_start_size", borderStartSize);
        allowChatInMatch = config.getBoolean("allow_chat_in_match", allowChatInMatch);
        combatLoggerDamageMultiplierEnvironment = config.getDouble("combat_logger_damage_multiplier_environment", combatLoggerDamageMultiplierEnvironment);
        combatLoggerDamageMultiplierPlayer = config.getDouble("combat_logger_damage_multiplier_player", combatLoggerDamageMultiplierPlayer);
        combatLoggerDamageTrackTime = config.getInt("combat_logger_damage_track_time", combatLoggerDamageTrackTime);
        worldName = config.getString("world_name", worldName);
        spawnEffectDuration = config.getInt("spawn_effect_duration", spawnEffectDuration);
        instance = this;
    }

    public static ConfigData getInstance(){
        return instance;
    }
}
