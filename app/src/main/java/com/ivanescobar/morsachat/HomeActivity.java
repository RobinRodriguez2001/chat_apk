package com.ivanescobar.morsachat;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ivanescobar.morsachat.fragments.ChatsFragment;
import com.ivanescobar.morsachat.fragments.FiltersFragment;
import com.ivanescobar.morsachat.fragments.HomeFragment;
import com.ivanescobar.morsachat.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectListener);
        openFragment(new HomeFragment());



    }

    public void openFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    if(item.getItemId() == R.id.itemHome){
                        openFragment(new HomeFragment());

                    } else if (item.getItemId() == R.id.itemChats) {
                        openFragment(new ChatsFragment());


                    } else if (item.getItemId() == R.id.itemProfile) {
                        openFragment(new ProfileFragment());
                    }

                    return true;
                }
            };
}