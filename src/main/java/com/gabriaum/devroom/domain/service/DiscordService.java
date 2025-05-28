package com.gabriaum.devroom.domain.service;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.sold.ProductSold;
import com.gabriaum.devroom.product.Product;
import com.gabriaum.devroom.util.stack.ItemSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class DiscordService {
    private final MarketMain plugin = MarketMain.getInstance();

    public void send(Product product, Player buyer, double price) {
        FileConfiguration config = plugin.getConfig();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(config.getString("discord.webhook.url"));
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                final String formattedDate = sdf.format(product.getCreatedAt());

                final ItemStack item = ItemSerializer.read(product.getSerializedItem());
                if(item == null) {
                    Bukkit.getLogger().warning("ItemStack is null");
                    return;
                }

                final ItemMeta meta = item.getItemMeta();
                final String name = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().name();
                final int color = toDecimal(config.getString("discord.embed.color", "#FFFFFF"));

                final List<Map<?, ?>> fieldsList = config.getMapList("discord.embed.fields");
                final JsonArray fieldsArray = new JsonArray();

                for (Map<?, ?> field : fieldsList) {
                    final JsonObject fieldObject = new JsonObject();
                    fieldObject.addProperty("name", field.get("name").toString());
                    fieldObject.addProperty(
                            "value",
                            field.get("value").toString()
                                    .replace("{item}", name)
                                    .replace("{seller}", product.getAnnounceByName())
                                    .replace("{buyer}", buyer.getName())
                                    .replace("{price}", String.valueOf(price))
                                    .replace("{date}", formattedDate)
                    );
                    fieldObject.addProperty("inline", Boolean.parseBoolean(field.get("inline").toString()));

                    fieldsArray.add(fieldObject);
                }

                JsonObject embed = new JsonObject();
                embed.addProperty("title", config.getString("discord.embed.title", "Market Alert"));
                embed.addProperty("description", String.join("\n", config.getStringList("discord.embed.description")));
                embed.addProperty("color", color);

                JsonObject footer = new JsonObject();
                footer.addProperty("text", config.getString("discord.embed.footer", "Marketplace System"));
                embed.add("footer", footer);

                embed.add("fields", fieldsArray);

                JsonObject payload = new JsonObject();
                payload.addProperty("username", config.getString("discord.embed.username", "Marketplace Bot"));

                JsonArray embedsArray = new JsonArray();
                embedsArray.add(embed);
                payload.add("embeds", embedsArray);

                try (OutputStream outputStream = connection.getOutputStream()) {
                    byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                    outputStream.write(input, 0, input.length);
                }

                connection.getResponseCode();

            } catch (Exception e) {
                Bukkit.getLogger().warning("Error sending webhook: " + e.getMessage());
            }
        });
    }

    private int toDecimal(String hex) {
        if (hex == null || !hex.matches("#?[0-9A-Fa-f]{6}")) {
            return 0xFFFFFF;
        }
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        return Integer.parseInt(hex, 16);
    }
}