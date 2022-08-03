package com.federicoberon.estilocafe.ui.home;

import android.content.Context;
import android.util.Log;

import com.federicoberon.estilocafe.BaseViewModel;
import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.model.ProductEntity;
import com.federicoberon.estilocafe.repository.AuthRepository;
import com.federicoberon.estilocafe.repository.ProductsRepository;
import com.federicoberon.estilocafe.repository.ProfileImageRepository;
import com.federicoberon.estilocafe.utils.CompressorBitmapImage;
import com.federicoberon.estilocafe.utils.StorageUtil;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Singleton
public class HomeViewModel extends BaseViewModel {

    /**
     * selected product fields
     */
    private long product_id;
    private String product_firebaseId;
    private String product_name;
    private String product_cat;
    private String product_desc;
    private float product_price;
    private String product_rating;
    private String product_offer;
    private ArrayList<String> product_images;

    private String mAbsolutePhotoPath;
    private File mPhotoFile;
    private boolean mChangeProfileImage;
    private ProductsRepository mProductsRepository;
    private  String selectedCategory;
    // id, cantidad
    private HashMap<Long, Integer> carrito;
    private float total;

    @Inject
    public HomeViewModel(AuthRepository authRepository,
                         ProfileImageRepository profileImageRepository,
                         ProductsRepository productsRepository) {
        super(authRepository, profileImageRepository);
        mProductsRepository = productsRepository;
        selectedCategory = null;
        carrito = new HashMap<>();
        total = 0;
        product_images = new ArrayList<>();
    }

    public float getTotal(){
        return total;
    }

    public int getCartCount(){
        return carrito.size();
    }

    public void addToCart(Long id, float price){
        if(carrito.containsKey(id))
            carrito.put(id,carrito.get(id)+1);
        else
            carrito.put(id,1);

        total += price;
    }

    public void removeFromCart(Long id, float price){
        if(carrito.containsKey(id)){
            if (carrito.get(id)>1)
                carrito.put(id,carrito.get(id)-1);
            else
                carrito.remove(id);
            total = total>=price?total-price:0;
            Log.w("MIO", "TOTALLLLL::::: " + total);
        }
    }

    public void setAbsolutePhotoPath(String mAbsolutePhotoPath) {
        this.mAbsolutePhotoPath = mAbsolutePhotoPath;
    }

    public File getPhotoFile() {
        return mPhotoFile;
    }

    public boolean isChangeProfileImage() {
        return mChangeProfileImage;
    }

    public void setChangeProfileImage(boolean mChangeProfileImage) {
        this.mChangeProfileImage = mChangeProfileImage;
    }

    public UploadTask save(Context context, File file, String uid) {
        return mProfileImageRepository.save(context, file, uid);
    }

    public void saveProfileImageToLocalStorage(String path, EstiloCafeApplication app) {
        StorageUtil.saveProfileImageToLocalStorage(getUid() + "_profile.jpg",
                CompressorBitmapImage.getBitmap(app, path, 500, 500));
    }

    public StorageReference getStorage() {
        return mProfileImageRepository.getStorage();
    }

    public void setPhotoFile(File mPhotoFile) {
        this.mPhotoFile = mPhotoFile;
    }

    public String getAbsolutePhotoPath() {
        return mAbsolutePhotoPath;
    }


    ///// collection operations
    public Query getAll() {
        return mProductsRepository.getRemoteProducts();
    }

    ///// room operations
    public Maybe<List<Long>> insertAllProducts(ArrayList<ProductEntity> productList) {
        return mProductsRepository.insertAllProducts(productList);
    }

    // called from main recyclerview
    public Flowable <List<ProductEntity>> getProductsLocallyByCat(String cat) {
        return mProductsRepository.getProductsByCategory(cat);
    }

    // caler  from searchfrag,emt
    public Flowable <List<ProductEntity>> getFilteredProducts(String query, String cat) {
        if(query==null || query.isEmpty())
            return getProductsLocallyByCat(cat);
        else {
            String newQuery = "%".concat(query).concat("%");
            if (cat != null)
                return mProductsRepository.getFilteredProductsByCategory(newQuery, cat);
            else
                return mProductsRepository.getFilteredProducts(newQuery);
        }
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    /**
     * product detail methods
     */


    public String getProduct_firebaseId() {
        return product_firebaseId;
    }

    public void setProduct_FirebaseId(String product_id) {
        this.product_firebaseId = product_id;
    }

    public long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_cat() {
        return product_cat;
    }

    public void setProduct_cat(String product_cat) {
        this.product_cat = product_cat;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public float getProduct_price() {
        return product_price;
    }

    public void setProduct_price(float product_price) {
        this.product_price = product_price;
    }

    public String getProduct_rating() {
        return product_rating;
    }

    public void setProduct_rating(String product_rating) {
        this.product_rating = product_rating;
    }

    public void setProduct_offer(String product_offer) {
        this.product_offer = product_offer;
    }

    public String getProduct_offer() {
        return product_offer;
    }

    public ArrayList<String> getProduct_images() {
        return product_images;
    }

    public void setProduct_images(String product_images) {
        this.product_images = new ArrayList<>(Arrays.asList(product_images.split(",")));
    }

    public int getProductCount(long id) {
        if(!carrito.containsKey(id))
            return 0;
        else
            return carrito.get(id);
    }

    public Flowable<List<ProductEntity>> getCartProducts() {
        return mProductsRepository.getCartProducts(new ArrayList<>(carrito.keySet()));
    }

    public HashMap<Long, Integer> getCart() {
        return carrito;
    }

    public Flowable<List<String>> getAllCategories() {
        return mProductsRepository.getAllCategories();
    }

    public void emptyCart() {
        carrito = new HashMap<>();
        total = 0;
    }
}
