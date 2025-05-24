package com.gabriaum.devroom.command.handler;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.util.ConfigUtil;
import com.gabriaum.devroom.util.command.CommandArgs;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public interface ArgumentHandler {
    String getName();
    void execute(CommandArgs commandArgs);

    default ConfigUtil getMessages() {
        return MarketMain.getInstance().getMessages();
    }

    default boolean hasPermission(CommandSender sender) {
        FileConfiguration config = MarketMain.getInstance().getConfig();
        String permission = config.getString("permission.command." + getName(), "gabriaum.marketplace." + getName());
        String adminPermission = config.getString("permission.admin", "gabriaum.marketplace.admin");
        return sender.hasPermission(permission) || sender.isOp() || sender.hasPermission(adminPermission);
    }
}