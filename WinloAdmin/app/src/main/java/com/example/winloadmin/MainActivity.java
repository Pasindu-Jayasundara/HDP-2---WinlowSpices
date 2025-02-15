package com.example.winloadmin;

import static android.view.Gravity.START;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.winloadmin.nav.DashboardFragment;
import com.example.winloadmin.nav.ProductFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

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

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.inflateMenu(R.menu.navigation_menu);

        navigationView.getMenu().getItem(0).setChecked(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new DashboardFragment())
                .setReorderingAllowed(true)
                .commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

                if (item.getItemId() == R.id.productFragment) {
                    item.setChecked(true);
                    navigationView.getMenu().getItem(1).setChecked(true);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new ProductFragment())
                            .setReorderingAllowed(true)
                            .commit();
                }

                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });




    }
}