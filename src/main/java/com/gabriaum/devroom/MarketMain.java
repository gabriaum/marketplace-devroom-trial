package com.gabriaum.devroom;

import com.gabriaum.devroom.backend.database.DatabaseCredential;
import com.gabriaum.devroom.backend.database.mongodb.MongoConnection;
import com.gabriaum.devroom.command.MarketCommand;
import com.gabriaum.devroom.domain.controller.ProductController;
import com.gabriaum.devroom.domain.inventory.impl.DefaultMarketInventory;
import com.gabriaum.devroom.util.ConfigUtil;
import com.gabriaum.devroom.util.command.CommandFramework;
import com.google.gson.Gson;
import lombok.Getter;
import me.devnatan.inventoryframework.ViewFrame;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class MarketMain extends JavaPlugin {
    @Getter
    private static MarketMain instance;

    public static final Gson GSON = new Gson();

    private ConfigUtil messages;
    private ConfigUtil inventory;
    private MongoConnection mongoConnection;
    private ProductController productController;
    private CommandFramework commandFramework;
    private ViewFrame viewFrame;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        instance = this;
        messages = new ConfigUtil(this, "messages.yml");
        inventory = new ConfigUtil(this, "inventory.yml");
    }

    @Override
    public void onEnable() {
        try {
            this.mongoConnection = new MongoConnection(new DatabaseCredential(
                    getConfig().getString("mongodb.hostname"),
                    getConfig().getString("mongodb.username"),
                    getConfig().getString("mongodb.password"),
                    getConfig().getString("mongodb.database"),
                    getConfig().getInt("mongodb.port", 27017)
            ));
            mongoConnection.connect();
        } catch (Exception ex) {
            getServer().shutdown();
            throw new RuntimeException("Failed to connect to MongoDB", ex);
        }

        this.productController = new ProductController();
        handleListeners(

        );

        this.commandFramework = new CommandFramework(this);
        commandFramework.registerCommands(
                new MarketCommand()
        );

        this.viewFrame = ViewFrame.create(this).with(
                new DefaultMarketInventory()
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
