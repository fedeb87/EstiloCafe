package com.federicoberon.estilocafe.datasource.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.federicoberon.estilocafe.model.OrderEntity;
import com.federicoberon.estilocafe.model.ProductEntity;
import com.federicoberon.estilocafe.model.ProductModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface ProductsDao extends BaseDao<OrderEntity>{

    @Query("SELECT * FROM orders")
    Flowable<List<OrderEntity>> getAllOrders();

    @Query("SELECT * FROM products WHERE category = :cat")
    Flowable<List<ProductEntity>> getAllProductsByCategory(String cat);

    @Query("SELECT * FROM products WHERE name like :query OR description like :query")
    Flowable<List<ProductEntity>> getFilteredProducts(String query);

    @Query("SELECT * FROM products WHERE category = :cat AND (name like :query OR description like :query)")
    Flowable<List<ProductEntity>> getFilteredProductsByCategory(String query, String cat);

    @Query("SELECT q.* FROM orders q WHERE q.id = :id")
    Flowable<OrderEntity> getOrderById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<List<Long>> insertProducts(List<ProductEntity> productList);

    @Query("SELECT * FROM products")
    Flowable<List<ProductEntity>> getAllProducts();

    @Query("SELECT count(*) FROM products")
    Flowable<Long> getProductsCount();

    @Query("SELECT count(*) FROM orders")
    int getOrdersCount();

    @Query("SELECT * FROM products WHERE id IN (:ids)")
    Flowable<List<ProductEntity>> getCartProducts(ArrayList<Long> ids);

    @Query("SELECT DISTINCT category FROM products")
    Flowable<List<String>> getAllCategories();

    @Query("SELECT DISTINCT idFirebase FROM products")
    Flowable<List<String>> getAllProductsIds();
    /* 0 (false) and 1 (true). */
}