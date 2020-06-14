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

        binding.poweredByCoinDesk.setOnClickListener(v -> binding.poweredByCoinDesk.setMovementMethod(LinkMovementMethod.getInstance()));
        binding.poweredByBitBlinx.setOnClickListener(v -> binding.poweredByBitBlinx.setMovementMethod(LinkMovementMethod.getInstance()));
    }

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

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}