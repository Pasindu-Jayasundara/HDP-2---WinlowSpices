package com.example.winloadmin;

import static android.view.Gravity.START;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.winloadmin.dto.CustomerDTO;
import com.example.winloadmin.dto.OrderDTO;
import com.example.winloadmin.dto.OrderItemDTO;
import com.example.winloadmin.dto.ProductDTO;
import com.example.winloadmin.dto.UserDTO;
import com.example.winloadmin.nav.AdminFragment;
import com.example.winloadmin.nav.BannerFragment;
import com.example.winloadmin.nav.DashboardFragment;
import com.example.winloadmin.nav.OrderFragment;
import com.example.winloadmin.nav.ProductFragment;
import com.example.winloadmin.nav.UserFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static UserDTO userDTO;
    public static int orderCount;

    List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
    public static List<OrderDTO> orderDTOList = new ArrayList<>();
    public static List<CustomerDTO> customerDTOList = new ArrayList<>();
    public static List<ProductDTO> productDTOList = new ArrayList<>();

    public static FragmentManager fragmentManager;
//    public static PackageManager packageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        packageManager = getPackageManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            getWindow().getInsetsController().hide(WindowInsets.Type.systemBars());
            getWindow().getInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        fragmentManager = getSupportFragmentManager();

        // menu
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.inflateMenu(R.menu.navigation_menu);

        // intent
        Intent intent = getIntent();
        if(intent.hasExtra("user")){
            userDTO = new Gson().fromJson(intent.getStringExtra("user"), UserDTO.class);

            ImageView profileImg = findViewById(R.id.imageView6);
            Glide.with(this)
                    .load(Uri.parse(userDTO.getPhoto_url()))
                    .placeholder(AppCompatResources.getDrawable(this,R.drawable.user))
                    .circleCrop()
                    .error(AppCompatResources.getDrawable(this,R.drawable.user))
                    .into(profileImg);
        }

        // menu
        ImageView menu = findViewById(R.id.imageView7);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // default select menu - dashboard
        navigationView.getMenu().getItem(0).setChecked(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new DashboardFragment(orderItemDTOList))
                .setReorderingAllowed(true)
                .commit();

        // change fragment
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                Fragment fragment = null;
                int index = 0;

                if (item.getItemId() == R.id.productFragment) {

                    fragment = new ProductFragment();
                    index = 1;

                }
                if(item.getItemId() == R.id.dashboardFragment){

                    fragment = new DashboardFragment(orderItemDTOList);
                    index = 0;

                }
                if(item.getItemId() == R.id.orderFragment){

                    fragment = new OrderFragment(orderDTOList);
                    index = 2;

                }
                if(item.getItemId() == R.id.adminFragment) {

                    fragment = new AdminFragment();
                    index = 3;
                }
                if(item.getItemId() == R.id.userFragment) {

                    fragment = new UserFragment();
                    index = 4;
                }
                if(item.getItemId() == R.id.bannerFragment) {

                    fragment = new BannerFragment();
                    index = 5;
                }



                if(fragment!=null){

                    item.setChecked(true);
                    navigationView.getMenu().getItem(index).setChecked(true);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, fragment)
                            .setReorderingAllowed(true)
                            .commit();
                }

                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.sms_permission_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.sms_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }

    }

}