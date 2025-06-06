package com.gabriaum.devroom.listener;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.Account;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.concurrent.CompletableFuture;

public class AccountListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        CompletableFuture.runAsync(() -> {
            Player player = event.getPlayer();
            Account account = MarketMain.getInstance().getAccountData().getAccount(player.getUniqueId());
            if (account == null) {
                MarketMain.sendDebug("Creating new account for player: " + player.getName());
                account = MarketMain.getInstance().getAccountData().register(player.getUniqueId(), player.getName());
            }

            MarketMain.getInstance().getAccountController().put(player.getUniqueId(), account);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        MarketMain.getInstance().getAccountController().remove(player.getUniqueId());
        MarketMain.sendDebug("Removed account for player: " + player.getName());
    }
}