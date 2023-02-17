package me.salatosik.blockguardplugin;

import fr.minuskube.inv.InventoryManager;
import me.salatosik.blockguardplugin.commands.client.MagicItemCommands;
import me.salatosik.blockguardplugin.commands.client.MyBlocksCommand;
import me.salatosik.blockguardplugin.commands.client.RemoveBlockGuardCommand;
import me.salatosik.blockguardplugin.commands.client.tab.RemoveBlockGuardCompleter;
import me.salatosik.blockguardplugin.commands.server.ChangePluginLangCommand;
import me.salatosik.blockguardplugin.commands.server.tab.ChangePluginLangCommandTabCompleter;
import me.salatosik.blockguardplugin.core.Database;
import me.salatosik.blockguardplugin.core.LocalizationManager;
import me.salatosik.blockguardplugin.listeners.PlayerBlockInteractionListener;
import me.salatosik.blockguardplugin.listeners.item.PlayerGuardAdditionListener;
import me.salatosik.blockguardplugin.listeners.item.PlayerGuardRemoverListener;
import me.salatosik.blockguardplugin.listeners.item.PlayerMagicStickListener;
import me.salatosik.blockguardplugin.core.DatabaseCleaner;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Vars.init(getConfig());

        if(Vars.getMaximumProtectedBlocks() <= 0) {
            getPluginLoader().disablePlugin(this);
            getLogger().warning("The plugin will not work properly because the number in the parameter \"maximum-protected-block\" is less than or equal to zero.");
            return;
        }

        File databaseFile = new File(getDataFolder(), (String) getConfig().get("database-name-file"));
        database = new Database(databaseFile);
        if(!database.verifyConnection()) {
            getLogger().log(Level.OFF, "The database could not be loaded. The plugin will be disabled.");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        InventoryManager inventoryManager = new InventoryManager(this);
        inventoryManager.init();

        try {
            localizationManager = new LocalizationManager(this);
        } catch(Exception exception) {
            exception.printStackTrace();
            getPluginLoader().disablePlugin(this);
            return;
        }

        getLogger().info("Database loaded! Path: \"" + databaseFile.getAbsolutePath() + "\", do not forget that the name of the database can be changed in \"config.yml\"");

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new DatabaseCleaner(database, getServer().getWorlds()), 0, 80);

        getServer().getPluginManager().registerEvents(new PlayerBlockInteractionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMagicStickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerGuardRemoverListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerGuardAdditionListener(), this);

        MagicItemCommands magicItemCommands = new MagicItemCommands();

        getServer().getPluginCommand("give-guard").setExecutor(magicItemCommands);
        getServer().getPluginCommand("give-guard").setExecutor(magicItemCommands);
        getServer().getPluginCommand("give-guard").setExecutor(magicItemCommands);
        getServer().getPluginCommand("give-guard").setTabCompleter((commandSender, command, s, strings) -> Arrays.asList("addition", "picker", "remover"));

        getServer().getPluginCommand("remove-block-guard").setExecutor(new RemoveBlockGuardCommand());
        getServer().getPluginCommand("remove-block-guard").setTabCompleter(new RemoveBlockGuardCompleter());

        getServer().getPluginCommand("my-blocks").setExecutor(new MyBlocksCommand());

        getServer().getPluginCommand("change-plugin-language").setExecutor(new ChangePluginLangCommand());
        getServer().getPluginCommand("change-plugin-language").setTabCompleter(new ChangePluginLangCommandTabCompleter());
    }

    @Override
    public void onDisable() {
        if(database != null) database.closeConnection();
        getLogger().info("The database closed! Bye-bye ^_^");
    }

    private static Database database;
    private static LocalizationManager localizationManager;

    public static Database getDatabase() {
        return database;
    }

    public static LocalizationManager getLocalizationManager() {
        return localizationManager;
    }
}