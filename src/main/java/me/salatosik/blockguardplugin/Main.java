package me.salatosik.blockguardplugin;

import me.salatosik.blockguardplugin.commands.DisableAddingBlocksCommand;
import me.salatosik.blockguardplugin.commands.BlockPickerCommand;
import me.salatosik.blockguardplugin.commands.GuardRemoverCommand;
import me.salatosik.blockguardplugin.listeners.PlayerBlockInteractionListener;
import me.salatosik.blockguardplugin.listeners.PlayerGuardRemoverListener;
import me.salatosik.blockguardplugin.listeners.PlayerMagicStickListener;
import me.salatosik.blockguardplugin.util.GeneralDatabase;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private GeneralDatabase database;

    @Override
    public void onEnable() {
        if(getConfig().get("databaseFileName") == null) getConfig().set("databaseFileName", "salatosik-plugin-database.db");
        saveConfig();

        File databaseFile = new File(getDataFolder(), (String) getConfig().get("databaseFileName"));
        database = new GeneralDatabase(databaseFile);
        if(!database.verifyConnection()) {
            getLogger().log(Level.OFF, "The database could not be loaded. The plugin will be disabled.");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        getLogger().info("Database loaded! Path: \"" + databaseFile.getAbsolutePath() + "\", do not forget that the name of the database can be changed in \"config.yml\"");

        DisableAddingBlocksCommand disableAddingBlocksCommand = new DisableAddingBlocksCommand();
        PlayerBlockInteractionListener playerBlockInteractionListener = new PlayerBlockInteractionListener(database, this, disableAddingBlocksCommand);

        getServer().getPluginManager().registerEvents(playerBlockInteractionListener, this);
        getServer().getPluginManager().registerEvents(new PlayerMagicStickListener(playerBlockInteractionListener.getAllPlayerBlocks(), this), this);
        getServer().getPluginManager().registerEvents(new PlayerGuardRemoverListener(playerBlockInteractionListener.getAllPlayerBlocks(), playerBlockInteractionListener.getRemovedPlayerBlocks()), this);

        getServer().getPluginCommand("give-block-picker").setExecutor(new BlockPickerCommand());
        getServer().getPluginCommand("give-guard-remover").setExecutor(new GuardRemoverCommand());
        getServer().getPluginCommand("disable-block-adding").setExecutor(disableAddingBlocksCommand);
        getServer().getPluginCommand("disable-block-adding").setTabCompleter((commandSender, command, s, strings) -> Arrays.asList("enable", "disable"));
    }

    @Override
    public void onDisable() {
        database.closeConnection();
        getLogger().info("The database closed!\nBye-bye ^_^");
    }
}