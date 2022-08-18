package com.federicoberon.estilocafe;

import com.federicoberon.estilocafe.model.ProductEntity;

public class TestDataHelper {

    public static final String CAT1 = "Categoria 1";
    public static final String CAT2 = "Categoria 2";
    public static final String CAT3 = "Categoria 3";
    public static final String CAT4 = "Categoria 4";

    //@NonNull Long id, String idFirebase, String name, String rating, float price, String images,
    // String description, String category, String offer

    public static final ProductEntity PRODUCT_1 = new ProductEntity(1L, "firebaseid1", "Product name 1", "5.0",
            100, null, "Descripcion del producto 1", CAT1, "");

    public static final ProductEntity PRODUCT_2 = new ProductEntity(2L, "firebaseid2", "Product name 2", "5.0",
            200, null, "Descripcion del producto 2", CAT2, "");

    public static final ProductEntity PRODUCT_3 = new ProductEntity(3L, "firebaseid3", "Product name 3", "5.0",
            300, null, "Descripcion del producto 3", CAT3, "");

    public static final ProductEntity PRODUCT_4 = new ProductEntity(4L, "firebaseid4", "Product name 4", "5.0",
            400, null, "Descripcion del producto 4", CAT4, "");
}
