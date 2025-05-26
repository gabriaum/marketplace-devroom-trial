package com.gabriaum.devroom.account;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.sold.ProductSold;
import com.gabriaum.devroom.product.Product;
import lombok.Getter;

import java.util.*;

@Getter
public class Account {
    protected final UUID uniqueId;
    protected final String name;

    private final List<Product> advertisedProducts;
    private final Set<ProductSold> purchasedProducts;

    public Account(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.advertisedProducts = new ArrayList<>();
        this.purchasedProducts = new HashSet<>();
    }

    public void addAdvertisedProduct(Product product) {
        this.advertisedProducts.add(product);
        save("advertisedProducts");
    }

    public void removeAdvertisedProduct(Product product) {
        this.advertisedProducts.remove(product);
        save("advertisedProducts");
    }

    public void addPurchasedProduct(ProductSold productSold) {
        this.purchasedProducts.add(productSold);
        save("purchasedProducts");
    }

    public void removePurchasedProduct(ProductSold productSold) {
        this.purchasedProducts.remove(productSold);
        save("purchasedProducts");
    }

    protected void save(String... fields) {
        for (String field : fields) {
            MarketMain.getInstance().getAccountData().update(this, field);
        }
    }

    public static Account getAccount(UUID uniqueId) {
        return MarketMain.getInstance().getAccountController().get(uniqueId);
    }
}