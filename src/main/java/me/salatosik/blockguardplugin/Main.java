package me.salatosik.blockguardplugin;

import me.salatosik.blockguardplugin.commands.DisableAddingBlocksCommand;
import me.salatosik.blockguardplugin.commands.MagicItemCommands;
import me.salatosik.blockguardplugin.listeners.PlayerBlockInteractionListener;
import me.salatosik.blockguardplugin.listeners.PlayerGuardAdditionListener;
import me.salatosik.blockguardplugin.listeners.PlayerGuardRemoverListener;
import me.salatosik.blockguardplugin.listeners.PlayerMagicStickListener;
import me.salatosik.blockguardplugin.util.DatabaseCleaner;
import me.salatosik.blockguardplugin.util.GeneralDatabase;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private GeneralDatabase database;
    private DatabaseCleaner databaseCleaner;

    // TODO реалізувати авто-очищення блоків

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        if(config.get("databaseFileName") == null) config.set("databaseFileName", "salatosik-plugin-database.db");
        if(config.get("allowsInWorlds.world") == null) config.set("allowsInWorlds.world", true);
        if(config.get("allowsInWorlds.nether") == null) config.set("allowsInWorlds.nether", false);
        if(config.get("allowsInWorlds.ender") == null) config.set("allowsInWorlds.ender", false);
        if(config.get("defaultValues.disableAutoAddBlockCommand") == null) config.set("defaultValues.disableAutoAddBlockCommand", true);
        saveConfig();

        Vars.init(config);

        File databaseFile = new File(getDataFolder(), (String) getConfig().get("databaseFileName"));
        database = new GeneralDatabase(databaseFile);
        if(!database.verifyConnection()) {
            getLogger().log(Level.OFF, "The database could not be loaded. The plugin will be disabled.");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        getLogger().info("Database loaded! Path: \"" + databaseFile.getAbsolutePath() + "\", do not forget that the name of the database can be changed in \"config.yml\"");

        databaseCleaner = new DatabaseCleaner(database, getServer().getWorlds());
//        databaseCleaner.runTaskTimer(this, 150, 150);

        DisableAddingBlocksCommand disableAddingBlocksCommand = new DisableAddingBlocksCommand();
        PlayerBlockInteractionListener playerBlockInteractionListener = new PlayerBlockInteractionListener(database, this, disableAddingBlocksCommand);

        getServer().getPluginManager().registerEvents(playerBlockInteractionListener, this);
        getServer().getPluginManager().registerEvents(new PlayerMagicStickListener(database, this), this);
        getServer().getPluginManager().registerEvents(new PlayerGuardRemoverListener(database), this);
        getServer().getPluginManager().registerEvents(new PlayerGuardAdditionListener(database), this);

        MagicItemCommands magicItemCommands = new MagicItemCommands();

        getServer().getPluginCommand("give-block-picker").setExecutor(magicItemCommands);
        getServer().getPluginCommand("give-guard-remover").setExecutor(magicItemCommands);
        getServer().getPluginCommand("give-guard-addition").setExecutor(magicItemCommands);

        getServer().getPluginCommand("disable-block-adding").setExecutor(disableAddingBlocksCommand);
        getServer().getPluginCommand("disable-block-adding").setTabCompleter((commandSender, command, s, strings) -> Arrays.asList("enable", "disable"));
    }

    @Override
    public void onDisable() {
        database.closeConnection();
        getLogger().info("The database closed!\nBye-bye ^_^");
    }
}