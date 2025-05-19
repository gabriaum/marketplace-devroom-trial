package com.gabriaum.devroom;

import com.gabriaum.devroom.domain.controller.ProductController;
import com.gabriaum.devroom.util.ConfigUtil;
import com.gabriaum.devroom.util.command.CommandFramework;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class MarketMain extends JavaPlugin {
    @Getter
    private static MarketMain instance;

    private ConfigUtil messages;
    private ConfigUtil inventory;
    private ProductController productController;
    private CommandFramework commandFramework;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        instance = this;
        messages = new ConfigUtil(this, "messages.yml");
        inventory = new ConfigUtil(this, "inventory.yml");
    }

    @Override
    public void onEnable() {
        this.productController = new ProductController();
        handleListeners(

        );

        this.commandFramework = new CommandFramework(this);
        commandFramework.registerCommands(

        );
    }

    protected void handleListeners(Object... objects) {
        for (Object object : objects)
            getServer().getPluginManager().registerEvents((Listener) object, this);
    }

    public static void sendDebug(String message) {
        if (getInstance().getConfig().getBoolean("debug-mode", false))
            getInstance().getLogger().info(message);
    }
}
