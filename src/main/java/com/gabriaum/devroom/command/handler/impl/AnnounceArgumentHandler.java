package com.gabriaum.devroom.command.handler.impl;

import com.gabriaum.devroom.command.handler.ArgumentHandler;
import com.gabriaum.devroom.util.command.CommandArgs;
import org.bukkit.entity.Player;

public class AnnounceArgumentHandler implements ArgumentHandler {
    @Override
    public String getName() {
        return "announce";
    }

    @Override
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();
        if (args.length < 2) {
            player.sendMessage("§cUsage: /" + commandArgs.getLabel() + " announce <price>");
            return;
        }


    }
}
