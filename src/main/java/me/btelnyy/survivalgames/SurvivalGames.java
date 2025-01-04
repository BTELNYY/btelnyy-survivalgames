package me.btelnyy.survivalgames;

import me.btelnyy.survivalgames.command.StartGame;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import me.btelnyy.survivalgames.constants.ConfigData;
import me.btelnyy.survivalgames.listener.EventListener;
import me.btelnyy.survivalgames.service.file_manager.Configuration;
import me.btelnyy.survivalgames.service.file_manager.FileID;
import me.btelnyy.survivalgames.service.file_manager.FileManager;

import java.util.logging.Level;

public class SurvivalGames extends JavaPlugin {

    // An instance of the plugin, so we don't need to make everything static
    private static SurvivalGames instance;

    private Configuration config;
    private ConfigData configData;
    private FileManager fileManager;

    @Override
    public void onEnable() {
        // Self-explanatory
        instance = this;

        // Generate files
        fileManager = new FileManager(this);
        fileManager.addFile(FileID.LANGUAGE, fileManager.create(null, "language.yml"));

        // Load config
        saveDefaultConfig();
        {
            // Load it into a more user-friendly object
            config = new Configuration(getConfig());

            // Cache it
            configData = new ConfigData();
            configData.load(config);
        }

        // Register commands
        registerCommandExecutor("startgame", new StartGame());

        // SnakeYAML fix
        Thread.currentThread().setContextClassLoader(this.getClassLoader());
        
        // Register events
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable()
    {

    }


    public void log(Level level, Object message){
        getLogger().log(level, message.toString());
    }

    @SuppressWarnings("unused")
    private void registerCommandExecutor(String commandName, CommandExecutor commandExecutor) {
        PluginCommand command = getCommand(commandName);

        /*
        If the command is null java will trigger an error any ways, the goal with this is to
        not trigger the error, so calling an explicit NullPointerError does not make things any better
         */
        if (command == null) {
            getLogger().severe("The command " + commandName + " could not be registered, please contact the plugin authors " + getDescription().getAuthors());
            return;
        }
        command.setExecutor(commandExecutor);
    }
    
    @SuppressWarnings("unused")
    private void registerCommandExecutor(String commandName, CommandExecutor commandExecutor, TabCompleter completer) {
        PluginCommand command = getCommand(commandName);

        /*
        If the command is null java will trigger an error any ways, the goal with this is to
        not trigger the error, so calling an explicit NullPointerError does not make things any better
         */
        if (command == null) {
            getLogger().severe("The command " + commandName + " could not be registered, please contact the plugin authors " + getDescription().getAuthors());
            return;
        }
        command.setTabCompleter(completer);
        command.setExecutor(commandExecutor);
    }

    public static SurvivalGames getInstance() {
        return instance;
    }

    public ConfigData getConfigData() {
        return configData;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
