package com.dummycoding.mycrypto.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.dummycoding.mycrypto.R;
import com.dummycoding.mycrypto.common.BaseActivity;
import com.dummycoding.mycrypto.data.network.Repository;
import com.dummycoding.mycrypto.databinding.ActivityMainBinding;
import com.dummycoding.mycrypto.models.OwnedToken;
import com.dummycoding.mycrypto.preferences.SettingsActivity;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

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

        if (!getCompositionRoot().getRepository().isDisclaimerShown()) {
            getCompositionRoot().getRepository().setDisclaimerShown();
            showDisclaimer();
        }

        Repository repo = getCompositionRoot().getRepository();
        if (!repo.btcFixPassed()){
            btxFix(repo);
        }
    }

    private void btxFix(Repository repo) {
        repo.getBtcOwnedTokensSingle()
                .subscribeOn(Schedulers.io())
                .subscribe(tokens -> {

                    for (OwnedToken token: tokens) {
                        token.setTokenInBtc(1);
                    }
                    if (tokens.size() != 0) {
                        repo.insertOwnedTokens(tokens)
                                .subscribeOn(Schedulers.io())
                                .subscribe(() -> {
                                }, throwable -> Timber.e(throwable, "btxFix: "));
                    }
                    repo.setBtcFixPassed();
                }, throwable -> Timber.e(throwable, "editOwnedToken: "));
    }

    private void showDisclaimer() {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Disclaimer")
                .setMessage("This app shows and uses the available crypto pairs from BitBlinx using its LAST PRICE property only.\nFurthermore, the BTC/EURS pair is used to calculate the Euro price of all xxx/BTC pairs.\n\nUSE THIS TOOL ONLY FOR ESTIMATIONS!\n\n" +
                        "Buying and selling has a direct effect on the market price, so expect prices to rise or drop when doing (big) orders.")
                .addButton("UNDERSTOOD", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                    dialog.dismiss();
                    showCoinDeskChoice();
                });

        builder.show();
    }

    private void showCoinDeskChoice() {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        builder.setTitle("Choose BPI currency provider");
        builder.setMessage("For all non-Euro currencies like USD, AUD, SEK, etc. the app uses CoinDesk automatically to calculate to those currencies.\n\n" +
                "However, if you use any other Exchange then BitBlinx to trade BTC to Euro or vv, my personal experience is that CoinDesk BTC/EUR rates are closer to those other exchanges rates.\n" +
                "But please do your own research on which values to use to get the best experience using this app!\n\n" +
                "You can change this provider anytime in the settings view.\n\n" +
                "Use:");
        builder.setSingleChoiceItems(new String[]{"BitBlinx", "CoinDesk"}, 0, (dialogInterface, index) -> {
            if (index == 0) {
                getCompositionRoot().getRepository().setUseCoinDesk(false);
            } else {
                getCompositionRoot().getRepository().setUseCoinDesk(true);
            }
        });
        builder.addButton("DONE", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
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