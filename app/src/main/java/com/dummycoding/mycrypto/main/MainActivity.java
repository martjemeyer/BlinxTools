package com.dummycoding.mycrypto.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dummycoding.mycrypto.R;
import com.dummycoding.mycrypto.databinding.ActivityMainBinding;
import com.dummycoding.mycrypto.preferences.SettingsActivity;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.viewPager.setAdapter(new ViewPagerAdapter(this));

        new TabLayoutMediator(
                binding.tabs,
                binding.viewPager,
                (tab, position) -> tab.setText(getResources().getStringArray(R.array.fragments)[position])).attach();

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("        My Crypto");
        //Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setIcon(R.drawable.logo);

        binding.poweredByCoinDesk.setOnClickListener(v -> binding.poweredByCoinDesk.setMovementMethod(LinkMovementMethod.getInstance()));
        binding.poweredByBitBlinx.setOnClickListener(v -> binding.poweredByBitBlinx.setMovementMethod(LinkMovementMethod.getInstance()));
    }

  /*  private void setupSearchViewListener() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Timber.d(newText);
                return false;
            }
        });
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            openSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (binding.viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        }
        else {
            binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() - 1);
        }
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}