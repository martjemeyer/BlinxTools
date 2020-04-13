package com.dummycoding.mycrypto.models.coindesk;

import java.util.List;

public class CurrencyCountryWrapper {
    private List<String> currenciesList;
    private List<String> countriesList;
    private CharSequence[] currencies;
    private CharSequence[] countries;

    public CurrencyCountryWrapper(List<String> currenciesList, List<String> countriesList) {
        this.currenciesList = currenciesList;
        this.countriesList = countriesList;
    }

    public CurrencyCountryWrapper(CharSequence[] currencies, CharSequence[] countries) {
        this.currencies = currencies;
        this.countries = countries;
    }

    public List<String> getCurrenciesList() {
        return currenciesList;
    }

    public List<String> getCountriesList() {
        return countriesList;
    }

    public CharSequence[] getCurrencies() {
        return currencies;
    }

    public CharSequence[] getCountries() {
        return countries;
    }
}
