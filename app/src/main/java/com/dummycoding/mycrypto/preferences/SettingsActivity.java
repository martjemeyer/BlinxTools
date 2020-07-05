package com.dummycoding.mycrypto.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.dummycoding.mycrypto.R;
import com.dummycoding.mycrypto.common.BaseActivity;
import com.dummycoding.mycrypto.data.network.Repository;
import com.dummycoding.mycrypto.models.coindesk.CurrencyCountryWrapper;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        private ListPreference mListPreference;
        private CompositeDisposable subscriptions = new CompositeDisposable();
        private Repository mRepository;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            mListPreference = getPreferenceManager().findPreference(getString(R.string.preferred_currency_key));
            if (mListPreference != null) {
                mListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    mListPreference.setSummary(newValue.toString());
                    return true;
                });
            }

            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            if (context instanceof SettingsActivity) {
                mRepository = ((SettingsActivity) context).getCompositionRoot().getRepository();
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            subscriptions.clear();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStart() {
            super.onStart();
            if (mRepository != null) {
                getListPreferencesFromDb();
            }
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        private void getListPreferencesFromDb() {
                        subscriptions.add(
                    Flowable.zip(mRepository.getCoinDeskCurrencyStringsFlowable(), mRepository.getCoinDeskCountryStringsFlowable(), CurrencyCountryWrapper::new)
                            .map(wrapper -> {
                                CharSequence[] countries = wrapper.getCountriesList().toArray(new CharSequence[wrapper.getCountriesList().size()]);
                                CharSequence[] currencies = wrapper.getCurrenciesList().toArray(new CharSequence[wrapper.getCountriesList().size()]);
                                return new CurrencyCountryWrapper(currencies, countries);
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(wrapper -> mListPreference.setEnabled(false))
                            .subscribe(wrapper -> {
                                mListPreference.setEnabled(true);
                                if (wrapper.getCurrencies().length != 0) {
                                    mListPreference.setEntries(wrapper.getCountries());
                                    mListPreference.setEntryValues(wrapper.getCurrencies());
                                    mListPreference.setValue(mRepository.getPreferredCurrency());
                                    mListPreference.setSummary(mRepository.getPreferredCurrency());
                                } else {
                                    setListDefaultValue();
                                }
                            }, throwable -> {
                                setListDefaultValue();
                                Timber.e(throwable, "getListPreferencesFromDb: ");
                            })
            );
        }

        private void setListDefaultValue() {
            mListPreference.setEnabled(true);
            mListPreference.setEntries(new CharSequence[]{"Euro"});
            mListPreference.setEntryValues(new CharSequence[]{"EUR"});
            mListPreference.setDefaultValue("EUR");
            mListPreference.setSummary("Euro");
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getResources().getString(R.string.dark_mode_key))) {
                boolean darkMode = sharedPreferences.getBoolean(key, true);
                AppCompatDelegate.setDefaultNightMode(darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            }
            if (key.equals(getResources().getString(R.string.follow_system_setting_key))) {
                boolean darkMode = sharedPreferences.getBoolean(key, false);
                if (darkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    SettingsActivity activity = (SettingsActivity) getActivity();
                    if (activity != null) {
                        boolean darkModeSwitch = ((SettingsActivity) getActivity()).getCompositionRoot().getRepository().isDarkMode();
                        AppCompatDelegate.setDefaultNightMode(darkModeSwitch ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            }
        }
    }
}