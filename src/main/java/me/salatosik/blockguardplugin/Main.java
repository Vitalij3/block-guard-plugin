package me.salatosik.blockguardplugin;

import fr.minuskube.inv.InventoryManager;
import me.salatosik.blockguardplugin.commands.MagicItemCommands;
import me.salatosik.blockguardplugin.commands.MyBlocksCommand;
import me.salatosik.blockguardplugin.commands.RemoveBlockGuardCommand;
import me.salatosik.blockguardplugin.commands.tab.RemoveBlockGuardCompleter;
import me.salatosik.blockguardplugin.core.Database;
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
        saveDefaultConfig();
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

        inventoryManager = new InventoryManager(this);
        inventoryManager.init();

        getLogger().info("Database loaded! Path: \"" + databaseFile.getAbsolutePath() + "\", do not forget that the name of the database can be changed in \"config.yml\"");

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new DatabaseCleaner(database, getServer().getWorlds()), 0, 80);

        PlayerBlockInteractionListener playerBlockInteractionListener = new PlayerBlockInteractionListener(database);

        getServer().getPluginManager().registerEvents(playerBlockInteractionListener, this);
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
    }

    @Override
    public void onDisable() {
        if(database != null) database.closeConnection();
        getLogger().info("The database closed! Bye-bye ^_^");
    }

    private static InventoryManager inventoryManager;
    private static Database database;

    public static InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public static Database getDatabase() {
        return database;
    }
}