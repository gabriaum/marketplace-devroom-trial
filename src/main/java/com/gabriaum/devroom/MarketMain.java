package com.gabriaum.devroom;

import com.gabriaum.devroom.backend.data.impl.AccountDataImpl;
import com.gabriaum.devroom.backend.data.impl.ProductDataImpl;
import com.gabriaum.devroom.backend.data.impl.TransactionDataImpl;
import com.gabriaum.devroom.backend.database.DatabaseCredential;
import com.gabriaum.devroom.backend.database.mongodb.MongoConnection;
import com.gabriaum.devroom.command.BlackMarketCommand;
import com.gabriaum.devroom.command.MarketCommand;
import com.gabriaum.devroom.command.SellCommand;
import com.gabriaum.devroom.command.TransactionsCommand;
import com.gabriaum.devroom.domain.controller.AccountController;
import com.gabriaum.devroom.domain.controller.ProductController;
import com.gabriaum.devroom.domain.inventory.impl.ConfirmTransactionInventory;
import com.gabriaum.devroom.domain.inventory.impl.BlackMarketInventory;
import com.gabriaum.devroom.domain.inventory.impl.DefaultMarketInventory;
import com.gabriaum.devroom.domain.inventory.impl.TransactionInventory;
import com.gabriaum.devroom.listener.AccountListener;
import com.gabriaum.devroom.util.ConfigUtil;
import com.gabriaum.devroom.util.command.CommandFramework;
import com.google.gson.Gson;
import lombok.Getter;
import me.devnatan.inventoryframework.ViewFrame;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class MarketMain extends JavaPlugin {
    @Getter
    private static MarketMain instance;

    public static final Gson GSON = new Gson();

    @Getter
    private static Economy economy = null;

    private ConfigUtil messages;
    private ConfigUtil inventory;
    private MongoConnection mongoConnection;
    private AccountDataImpl accountData;
    private ProductDataImpl productData;
    private TransactionDataImpl transactionData;
    private AccountController accountController;
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
        setupEconomy();

        try {
            this.mongoConnection = new MongoConnection(new DatabaseCredential(
                    getConfig().getString("mongodb.hostname"),
                    getConfig().getString("mongodb.username"),
                    getConfig().getString("mongodb.password"),
                    getConfig().getString("mongodb.database"),
                    getConfig().getInt("mongodb.port", 27017)
            ));
            mongoConnection.connect();
            this.accountData = new AccountDataImpl(mongoConnection);
            this.productData = new ProductDataImpl(mongoConnection);
            this.transactionData = new TransactionDataImpl(mongoConnection);
        } catch (Exception ex) {
            getServer().shutdown();
            throw new RuntimeException("Failed to connect to MongoDB", ex);
        }

        this.accountController = new AccountController();
        this.productController = new ProductController();
        this.productController.addAll(productData.getProducts());
        handleListeners(
                new AccountListener()
        );

        this.commandFramework = new CommandFramework(this);
        commandFramework.registerCommands(
                new MarketCommand(), new SellCommand(), new TransactionsCommand(),
                new BlackMarketCommand()
        );

        this.viewFrame = ViewFrame.create(this).with(
                new TransactionInventory(), new DefaultMarketInventory(), new BlackMarketInventory(),
                new ConfirmTransactionInventory()
        ).register();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault plugin not found! Economy features will be disabled.");
            return false;
        }
        var rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().warning("No economy provider found! Economy features will be disabled.");
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
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
