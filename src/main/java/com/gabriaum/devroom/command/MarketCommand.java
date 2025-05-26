package com.gabriaum.devroom.command;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.command.handler.ArgumentHandler;
import com.gabriaum.devroom.command.handler.impl.AnnounceArgumentHandler;
import com.gabriaum.devroom.command.handler.impl.OpenArgumentHandler;
import com.gabriaum.devroom.util.ConfigUtil;
import com.gabriaum.devroom.util.command.Command;
import com.gabriaum.devroom.util.command.CommandArgs;
import com.gabriaum.devroom.util.command.Completer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class MarketCommand {
    private final List<ArgumentHandler> argumentHandlers;

    public MarketCommand() {
        this.argumentHandlers = List.of(
                new OpenArgumentHandler(),
                new AnnounceArgumentHandler()
        );
    }

    @Command(name = "marketplace", aliases = {"mp"}, inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        ConfigUtil messages = MarketMain.getInstance().getMessages();
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();
        if (args.length < 1) {
            player.sendMessage(Objects.requireNonNull(messages.getString("command.default-usage")));
            return;
        }

        ArgumentHandler argumentHandler = getArgumentHandler(args[0].toLowerCase());
        if (argumentHandler == null) {
            player.sendMessage(Objects.requireNonNull(messages.getString("command.default-usage")));
            return;
        }

        if (!argumentHandler.hasPermission(player)) {
            player.sendMessage(Objects.requireNonNull(messages.getString("no-permission")));
            return;
        }

        argumentHandler.execute(commandArgs);
    }

    @Completer(name = "marketplace")
    public List<String> complete(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        if (args.length == 1) {
            return argumentHandlers.stream()
                    .map(ArgumentHandler::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }

    protected ArgumentHandler getArgumentHandler(String name) {
        return argumentHandlers.stream()
                .filter(handler -> handler.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
