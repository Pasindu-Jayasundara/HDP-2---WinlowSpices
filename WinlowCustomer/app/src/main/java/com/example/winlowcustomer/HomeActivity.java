package com.example.winlowcustomer;

import static com.example.winlowcustomer.MainActivity.language;
import static com.example.winlowcustomer.modal.SetUpLanguage.setAppLanguage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.winlowcustomer.dto.BannerDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.modal.AddressHandling;
import com.example.winlowcustomer.modal.MainLoadData;
import com.example.winlowcustomer.modal.NetworkConnection;
import com.example.winlowcustomer.modal.HomeRecyclerViewAdapter;
import com.example.winlowcustomer.modal.SetUpLanguage;
import com.example.winlowcustomer.modal.Translate;
import com.example.winlowcustomer.modal.callback.GetAddressCallback;
import com.example.winlowcustomer.modal.callback.TranslationCallback;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.search.SearchBar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.model.CarouselType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    ArrayList<ProductDTO> productDTOArrayList;
    public static ArrayList<ProductDTO> productDTOArrayListOriginal;
    ArrayList<BannerDTO> bannerArrayList;
    HashSet<String> categoryHashSet;
    //    public static String language;
    final boolean[] isFirstTime = {true};
    public static boolean once;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (language != null) {
            setAppLanguage(HomeActivity.this, language);
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // bottom navigation
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, BottomNavigationFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        productDTOArrayList = new ArrayList<>();
        productDTOArrayListOriginal = new ArrayList<>();
        Log.i("ccccc",new Gson().toJson(productDTOArrayList));

        // data load
        loadData();


        // check network connection
        NetworkConnection.register(getApplicationContext());

        // click search
        SearchBar searchbar = findViewById(R.id.searchBar);
        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeActivity(SearchProductActivity.class);

            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);

        // load profile image
        loadProfileImage(sharedPreferences);

        // click profile image
        ImageView profileImage = findViewById(R.id.imageView);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userJson = sharedPreferences.getString("user", null);
                if (userJson == null) {
                    changeActivity(LoginActivity.class);
                } else {
                    changeActivity(AccountActivity.class);
                }

            }
        });

        // click location
        TextView selectLocation = findViewById(R.id.textView4);
        AddressHandling.loadAddress(getApplicationContext(), new GetAddressCallback() {
            @Override
            public void onAddressLoaded(List<String> addressList) {

                if (addressList.contains(getString(R.string.checkout_select_address))) {
                    List<String> list = new ArrayList<>();
                    list.add(getString(R.string.checkout_select_address));
                    list.add(getString(R.string.select_address));
                    addressList.removeAll(list);
                }
                if (addressList.isEmpty()) {
                    selectLocation.setText(getString(R.string.checkout_select_address));
                    return;
                }

                selectLocation.setText(addressList.get(0));

            }
        });
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
                String userJson = sharedPreferences.getString("user", null);
                if (userJson == null) {
                    changeActivity(LoginActivity.class);
                } else {
                    changeActivity(AddressActivity.class);
                }
            }
        });

        // change category
        ChipGroup categoryChipGroup = findViewById(R.id.categoryChipGroup);
        categoryChipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                changeProductOrder(checkedIds);

            }
        });

        // refresh
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                MainLoadData.mainLoadData(productDTOArrayList, bannerArrayList, categoryHashSet, HomeActivity.this);
                loadData();

                RecyclerView recyclerView = findViewById(R.id.recyclerView2);
                recyclerView.getAdapter().notifyDataSetChanged();


                swipeRefreshLayout.setRefreshing(false);
            }
        });

        registerProductListener(productDTOArrayList,bannerArrayList,categoryHashSet);

    }

    private void registerProductListener(ArrayList<ProductDTO> productDTOArrayList, ArrayList<BannerDTO> bannerArrayList, HashSet<String> categoryHashSet) {

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("product")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null){
                            Log.e("firestore error", error.getMessage());
                            return;
                        }

                        if (value != null){

                            for (DocumentChange dc : value.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:{

                                        QueryDocumentSnapshot document = dc.getDocument();
                                        Log.d("FirestoreListener", "Document added: " + document.toString());
                                        ProductDTO newProduct = document.toObject(ProductDTO.class);
                                        Log.d("FirestoreListener", "Document added 2: " + gson.toJson(productDTOArrayList));

                                        newProduct.setId(document.getId());
                                        if(!dc.getDocument().getId().contains("product/")){
                                            newProduct.setReferencePath("product/"+dc.getDocument().getId());
                                        }else{
                                            newProduct.setReferencePath(dc.getDocument().getId());
                                        }

                                        boolean exists = false;
                                        for (ProductDTO product : productDTOArrayList) {
                                            Log.i("ccccc",new Gson().toJson(product));

                                            if (product.getId().equals(newProduct.getId())) {
                                                exists = true;
                                                break;
                                            }
                                        }

                                        if (!exists) {
                                            categoryHashSet.add(newProduct.getCategory());
                                            productDTOArrayList.add(newProduct);
                                        }

                                        String categoryJson = gson.toJson(categoryHashSet);
                                        String productJson = gson.toJson(productDTOArrayList);
                                        editor.putString("category", categoryJson);
                                        editor.putString("product", productJson);
                                        editor.apply();
                                        break;

                                    }

                                    case MODIFIED:{

                                        String modifiedId = dc.getDocument().getId();

                                        for (int i = 0; i < productDTOArrayList.size(); i++) {

                                            if (productDTOArrayList.get(i).getId().equals(modifiedId)) {

                                                QueryDocumentSnapshot document = dc.getDocument();

//                                                Log.d("FirestoreListener", "Document modified: " + gson.toJson(dc.getDocument()));
                                                Log.d("FirestoreListener", "Document modified: " + document.toString());

                                                ProductDTO object = dc.getDocument().toObject(ProductDTO.class);
                                                object.setId(document.getId());
                                                if(!object.getId().contains("product/")){
                                                    object.setReferencePath("product/"+document.getId());
                                                }else{
                                                    object.setReferencePath(document.getId());
                                                }

                                                Log.d("FirestoreListener", "Document modified 2: " + gson.toJson(object));

                                                productDTOArrayList.set(i,object );

                                                break;
                                            }
                                        }

//                                        Set<ProductDTO> set = new HashSet<>(productDTOArrayListOriginal);
//                                        set.addAll(productDTOArrayList);
//                                        productDTOArrayListOriginal.clear();
//                                        productDTOArrayListOriginal.addAll(set);

                                        String productJson = gson.toJson(productDTOArrayList);
//                                        String productOriginalJson = gson.toJson(productDTOArrayListOriginal);

                                        editor.putString("product", productJson);
//                                        editor.putString("productDTOArrayListOriginal", productOriginalJson);
                                        editor.apply();


                                        break;

                                    }

                                    case REMOVED:{

                                        String removedId = dc.getDocument().getId();
                                        productDTOArrayList.removeIf(product -> product.getId().equals(removedId));
                                        Log.d("FirestoreListener", "Document removed: " + removedId);

                                        String productJson = gson.toJson(productDTOArrayList);

                                        editor.putString("product", productJson);
                                        editor.apply();

                                        break;

                                    }
                                }

                            }

                            loadData();

                        }

                    }
                });

        db.collection("banner")
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("firestore error", error.getMessage());
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            for (DocumentSnapshot document : value.getDocuments()) {
                                if (document.exists()) {
                                    List<String> bannerList = (List<String>) document.get("path");

                                    if (bannerList != null) {
                                        bannerArrayList.clear(); // Clear old data

                                        // Create a single BannerDTO object and add the full list
                                        BannerDTO bannerDTO = new BannerDTO(bannerList);
                                        bannerArrayList.add(bannerDTO);

                                        // Save banner data to SharedPreferences
                                        String bannerString = gson.toJson(bannerArrayList);

                                        editor.putString("banner", bannerString);
                                        editor.apply();

                                        // Update UI if necessary
                                    }
                                }
                            }

                            loadData();
                        }
                    }
                });

    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
        loadProfileImage(sharedPreferences);

        AddressHandling.loadAddress(getApplicationContext(), new GetAddressCallback() {
            @Override
            public void onAddressLoaded(List<String> addressList) {

                TextView selectLocation = findViewById(R.id.textView4);

                if (addressList.contains(getString(R.string.checkout_select_address))) {
                    List<String> list = new ArrayList<>();
                    list.add(getString(R.string.checkout_select_address));
                    list.add(getString(R.string.select_address));
                    addressList.removeAll(list);
                }
                if (addressList.isEmpty()) {
                    selectLocation.setText(getString(R.string.checkout_select_address));
                    return;
                }

                selectLocation.setText(addressList.get(0));

            }
        });
    }

    private void loadProfileImage(SharedPreferences sharedPreferences) {

        String userJson = sharedPreferences.getString("user", null);
        if (userJson != null) {

            UserDTO userDTO = new Gson().fromJson(userJson, UserDTO.class);
            if (userDTO.getProfile_image() != null) {
                ImageView img = findViewById(R.id.imageView);

                Glide.with(HomeActivity.this)
                        .load(Uri.parse(userDTO.getProfile_image()))
                        .circleCrop()
                        .placeholder(R.drawable.empty_profile_2)
                        .error(R.drawable.empty_profile_2)
                        .into(img);

            }

        }

    }

    private void loadData() {

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);

        String category = sharedPreferences.getString("category", null);
        String product = sharedPreferences.getString("product", null);
        String banner = sharedPreferences.getString("banner", null);

        if (category != null) {
            Type listType = new TypeToken<HashSet<String>>() {
            }.getType();
            categoryHashSet = gson.fromJson(category, listType);

            ChipGroup chipGroup = findViewById(R.id.categoryChipGroup);

            loadCategories(categoryHashSet, chipGroup, this);
        }

        if (product != null) {

            Type listType = new TypeToken<ArrayList<ProductDTO>>() {
            }.getType();
            productDTOArrayList = gson.fromJson(product, listType);
            productDTOArrayListOriginal = gson.fromJson(product, listType);

            Log.i("ccccc",new Gson().toJson(productDTOArrayList));


            sharedPreferences.edit().putString("productDTOArrayListOriginal", gson.toJson(productDTOArrayListOriginal)).apply();

            RecyclerView recyclerView = findViewById(R.id.recyclerView2);
            if(recyclerView.getLayoutManager()==null){
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
                recyclerView.setLayoutManager(gridLayoutManager);
            }
            if(recyclerView.getAdapter()==null){
                HomeRecyclerViewAdapter homeRecyclerViewAdapter = new HomeRecyclerViewAdapter(productDTOArrayList, HomeActivity.this);
                recyclerView.setAdapter(homeRecyclerViewAdapter);
            }
            recyclerView.getAdapter().notifyDataSetChanged();
        }

        if (banner != null) {

            Type listType = new TypeToken<ArrayList<BannerDTO>>() {
            }.getType();
            bannerArrayList = gson.fromJson(banner, listType);
            loadBanner();
        }

    }

    public void loadCategories(HashSet<String> categoryHashSet, ChipGroup chipGroup, Activity activity) {
        chipGroup.removeAllViews();

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
        language = sharedPreferences.getString("language", "");

        if (!language.equals("en")) {

            for (String category : categoryHashSet) {
                Translate.translateText(category, language, new TranslationCallback() {
                    @Override
                    public void onSuccess(String translatedText) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadCategory(translatedText, chipGroup, activity);

                            }
                        });

                    }

                    @Override
                    public void onFailure(String errorMessage) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadCategory(category, chipGroup, activity);
                            }
                        });

                    }
                });
            }

        } else {
            for (String category : categoryHashSet) {

                Chip chip = new Chip(new ContextThemeWrapper(activity, com.mobven.progress.R.style.Widget_MaterialComponents_Chip_Filter));
                chip.setText(category);
                chip.setCheckable(true);
                chip.setClickable(true);

                chip.setId(View.generateViewId());

                if (isFirstTime[0]) {
                    chip.setChecked(true);
                    isFirstTime[0] = false;
                    chipGroup.removeAllViews();
                } else {
                    chip.setChecked(false);
                }

                chipGroup.addView(chip);

            }
            isFirstTime[0] = true;

        }


    }

    private void loadCategory(String category, ChipGroup chipGroup, Activity activity) {

        Chip chip = new Chip(new ContextThemeWrapper(activity, com.mobven.progress.R.style.Widget_MaterialComponents_Chip_Filter));
        chip.setText(category);
        chip.setCheckable(true);
        chip.setClickable(true);

        chip.setId(View.generateViewId());

        if (isFirstTime[0]) {
            chip.setChecked(true);
            isFirstTime[0] = false;
            chipGroup.removeAllViews();
        } else {
            chip.setChecked(false);
        }

        chipGroup.addView(chip);
    }

    private void changeProductOrder(List<Integer> checkedIds) {
        Chip chip = findViewById(checkedIds.get(0));
        String chipTxt = chip.getText().toString();

        if (chipTxt.equals(getString(R.string.category_1))) {// all
            productDTOArrayList = new ArrayList<>(productDTOArrayListOriginal);
        } else {
            List<ProductDTO> reorderedList = new ArrayList<>();
            Iterator<ProductDTO> iterator = productDTOArrayList.iterator();

            while (iterator.hasNext()) {
                ProductDTO productDTO = iterator.next();
                if (productDTO.getCategory().equals(chipTxt)) {
                    reorderedList.add(productDTO);
                    iterator.remove(); // Safe removal using iterator
                }
            }
            productDTOArrayList.addAll(0, reorderedList); // Add at the beginning
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void loadBanner() {

        ImageCarousel imageCarousel = findViewById(R.id.imageCarousel);

        if (bannerArrayList.isEmpty()) {
            imageCarousel.setVisibility(View.GONE);
            return;
        }
        List<CarouselItem> carouselItemList = new ArrayList<>();

        BannerDTO dto = bannerArrayList.get(0);
        for (String imagePath : dto.getImagePathList()) {

            CarouselItem carouselItem = new CarouselItem(imagePath);
            carouselItemList.add(carouselItem);
        }

        imageCarousel.setAutoPlay(true);
        imageCarousel.setCarouselType(CarouselType.SHOWCASE);
        imageCarousel.setData(carouselItemList);

    }

    private void changeActivity(Class<?> activityName) {

        Intent intent = new Intent(HomeActivity.this, activityName);
        startActivity(intent);

    }

}