package com.federicoberon.estilocafe.datasource.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.federicoberon.estilocafe.model.ProductEntity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface ProductsDao extends BaseDao<ProductEntity>{

    @Query("SELECT * FROM products WHERE category = :cat")
    Flowable<List<ProductEntity>> getAllProductsByCategory(String cat);

    @Query("SELECT * FROM products WHERE name like :query OR description like :query")
    Flowable<List<ProductEntity>> getFilteredProducts(String query);

    @Query("SELECT * FROM products WHERE category = :cat AND (name like :query OR description like :query)")
    Flowable<List<ProductEntity>> getFilteredProductsByCategory(String query, String cat);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<List<Long>> insertProducts(List<ProductEntity> productList);

    @Query("SELECT * FROM products")
    Flowable<List<ProductEntity>> getAllProducts();

    @Query("SELECT count(*) FROM products")
    Flowable<Long> getProductsCount();

    @Query("SELECT * FROM products WHERE id IN (:ids)")
    Flowable<List<ProductEntity>> getCartProducts(ArrayList<Long> ids);

    @Query("SELECT DISTINCT category FROM products")
    Flowable<List<String>> getAllCategories();

    @Query("SELECT DISTINCT idFirebase FROM products")
    Flowable<List<String>> getAllProductsIds();
    /* 0 (false) and 1 (true). */
}