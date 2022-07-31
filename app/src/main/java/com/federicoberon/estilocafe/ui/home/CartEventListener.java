package com.federicoberon.estilocafe.ui.home;

public interface CartEventListener {
    void addToCart(Long id, float price);
    void removeFromCart(Long id, float price);
    int getProductCount(Long id);
}
