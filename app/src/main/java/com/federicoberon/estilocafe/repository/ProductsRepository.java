package com.federicoberon.estilocafe.repository;

import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_CAT;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_NAME;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_TAGS;

import com.federicoberon.estilocafe.datasource.dao.OrdersDao;
import com.federicoberon.estilocafe.datasource.dao.ProductsDao;
import com.federicoberon.estilocafe.model.OrderEntity;
import com.federicoberon.estilocafe.model.ProductEntity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Singleton
public class ProductsRepository {
    private final ProductsDao mProductsDao;
    private final OrdersDao mOrdersDao;
    private final CollectionReference mProductCollection;

    @Inject
    public ProductsRepository(ProductsDao productsDao, OrdersDao ordersDao) {
        mProductsDao = productsDao;
        mOrdersDao = ordersDao;
        mProductCollection = FirebaseFirestore.getInstance().collection("Products");
    }

    public Query getRemoteProducts(){
        return mProductCollection.orderBy(PRODUCT_NAME, Query.Direction.DESCENDING).limit(50);
    }

    public Flowable<List<ProductEntity>> getAllProducts() {
        return mProductsDao.getAllProducts();
    }

    public Flowable<Long> getProductsCount() {
        return mProductsDao.getProductsCount();
    }

    public Flowable<List<ProductEntity>> getProductsByCategory(String cat) {
        return mProductsDao.getAllProductsByCategory(cat);
    }

    public Flowable<List<ProductEntity>> getFilteredProductsByCategory(String query, String cat) {
        return mProductsDao.getFilteredProductsByCategory(query, cat);
    }

    public Maybe<List<Long>> insertAllProducts(ArrayList<ProductEntity> productList) {
        return mProductsDao.insertProducts(productList);
    }

    public Flowable<List<ProductEntity>> getFilteredProducts(String query) {
        return mProductsDao.getFilteredProducts(query);
    }

    public Query getFilteredProductsWithCat(String[] query, String category) {
        if(query!=null)
            return mProductCollection.whereEqualTo(PRODUCT_CAT, category).whereArrayContains(PRODUCT_TAGS, Arrays.asList(query));
        else
            return mProductCollection.whereEqualTo(PRODUCT_CAT, category);
    }

    public Query getRemoteProductsByCategory(String category) {
        return mProductCollection.whereEqualTo(PRODUCT_CAT, category);
    }

    public Task<DocumentSnapshot> getPostById(String id) {
        return mProductCollection.document(id).get();
    }

    public Flowable<List<ProductEntity>> getCartProducts(ArrayList<Long> ids) {
        return mProductsDao.getCartProducts(ids);
    }

    public Flowable<List<String>> getAllCategories() {
        return mProductsDao.getAllCategories();
    }

    public Flowable<List<String>> getAllProductsIds() {
        return mProductsDao.getAllProductsIds();
    }

    public Flowable<List<OrderEntity>> getAllOrders() {
        return mProductsDao.getAllOrders();
    }

    public Maybe<Long> saveOrder(OrderEntity order) {
        return mOrdersDao.insert(order);
    }
}
