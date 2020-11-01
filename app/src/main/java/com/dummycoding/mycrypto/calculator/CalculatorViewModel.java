package com.dummycoding.mycrypto.calculator;

import android.app.Application;

import androidx.annotation.NonNull;

import com.dummycoding.mycrypto.common.BaseViewModel;
import com.dummycoding.mycrypto.helpers.CurrencyHelper;
import com.dummycoding.mycrypto.models.CombinedResultWrapper;
import com.dummycoding.mycrypto.models.OutputModel;
import com.dummycoding.mycrypto.models.bitblinx.ActiveCurrencies;
import com.dummycoding.mycrypto.models.bitblinx.Result;
import com.dummycoding.mycrypto.models.coindesk.BpiCurrency;
import com.dummycoding.mycrypto.models.coindesk.CurrentPrice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

public class CalculatorViewModel extends BaseViewModel {

    private Result gtftaGtplusPair;
    private boolean inputBoxIsGtfta;

    enum SelectedInputBox {
        TOP,
        MIDDLE,
        BOTTOM
    }

    private BehaviorSubject<OutputModel> outputStream = BehaviorSubject.createDefault(new OutputModel("0", 1));
    private BehaviorSubject<String> topStream = BehaviorSubject.createDefault("0");
    private BehaviorSubject<String> middleStream = BehaviorSubject.createDefault("0");
    private BehaviorSubject<String> bottomStream = BehaviorSubject.createDefault("0");
    private BehaviorSubject<Boolean> progressStream = BehaviorSubject.createDefault(false);
    private BehaviorSubject<String> directPairIdentifierStream = BehaviorSubject.createDefault("");
    private BehaviorSubject<String> directPairOutputStream = BehaviorSubject.createDefault("0");

    private CompositeDisposable disposeBag = new CompositeDisposable();

    private List<String> adapterList = new ArrayList<>();
    private Map<String, Result> tokens = new HashMap<>();

    private SelectedInputBox selectedInputBox = SelectedInputBox.TOP;
    private String mActiveValue = "0";
    private int topSelectedIndex = -1;
    private int middleSelectedIndex = -1;
    private boolean isTokenPair = false;

    void setTopSelectedIndex(int topSelectedIndex) {
        this.topSelectedIndex = topSelectedIndex;
        checkForTokenPair();
        calculateOtherCurrencies(mActiveValue);
    }

    void setMiddleSelectedIndex(int middleSelectedIndex) {
        this.middleSelectedIndex = middleSelectedIndex;
        checkForTokenPair();
        calculateOtherCurrencies(mActiveValue);
    }

    private void checkForTokenPair() {
        String top = getTokenFromApdapter(topSelectedIndex);
        String middle = getTokenFromApdapter(middleSelectedIndex);
        if (top == null || middle == null) {
            isTokenPair = false;
        } else isTokenPair = top.equals("GTFTA") && middle.equals("GTPLUS")
                || top.equals("GTPLUS") && middle.equals("GTFTA");

        if (!isTokenPair) {
            showTokenPair("");
        }
    }

    private void showTokenPair(String value) {
        directPairIdentifierStream.onNext(value);
    }

    public CalculatorViewModel(@NonNull Application application) {
        super(application);
        updateLatestTokenValues();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposeBag.clear();
    }

    void setSelectedInputBox(SelectedInputBox box) {
        selectedInputBox = box;
    }

    private Result getTopToken() {
        String tokenString = getTokenFromApdapter(topSelectedIndex);
        if (tokenString == null) { return null; }

        return tokens.get(tokenString);
    }

    private Result getMiddleToken() {
        String tokenString = getTokenFromApdapter(middleSelectedIndex);
        if (tokenString == null) { return null; }

        return tokens.get(tokenString);
    }

    private String getTokenFromApdapter(int index) {
        if (adapterList.size() -1 >= topSelectedIndex && index != -1) {
            return adapterList.get(index);
        }
        return null;
    }

    void buttonClicked(String currentText, String addition, int cursorPosition) {
        StringBuilder str = new StringBuilder(currentText);

        if (addition.equals(".") && str.indexOf(".") != -1) {
            return;
        } else if (addition.equals("0") && cursorPosition == 0
                && (currentText.equals("0")
                || (str.indexOf(".") != -1 && str.indexOf(".") != 0))) {
            return;
        }

        if (cursorPosition >= currentText.length()) {
            str.append(addition);
        } else {
            str.insert(cursorPosition, addition);
        }

        if (str.length() > 1 && str.charAt(0) == '0' && str.charAt(1) != '.') {
            str.deleteCharAt(0);
            outputStream.onNext(new OutputModel(str.toString(), 1));
            calculateOtherCurrencies(str.toString());
            return;
        }

        outputStream.onNext(new OutputModel(str.toString(), cursorPosition + 1));

        calculateOtherCurrencies(str.toString());
    }

    private Double parseDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (Exception ex) {
            Timber.e(ex, "calculateOtherCurrencies ");
            return 0d;
        }
    }

    private void calculateOtherCurrencies(String inputString) {
        mActiveValue = inputString;
        if (inputString.length() == 0) {
            return;
        }

        double input = parseDouble(inputString);
        if (input == 0) {
            return;
        }

        switch (selectedInputBox) {
            case TOP:
                double btcValueTop = getBtcValue(SelectedInputBox.TOP, input);
                calculateAndUpdateMiddleBox(btcValueTop, input);
                calculateAndUpdateBottomBox(btcValueTop, input);
                break;
            case MIDDLE:
                double btcValueMiddle = getBtcValue(SelectedInputBox.MIDDLE, input);
                calculateAndUpdateTopBox(btcValueMiddle, input);
                calculateAndUpdateBottomBox(btcValueMiddle, input);
                break;
            case BOTTOM:
                double btcValueBottom = getBtcValue(SelectedInputBox.BOTTOM, input);
                calculateAndUpdateTopBox(btcValueBottom, input);
                calculateAndUpdateMiddleBox(btcValueBottom, input);
                break;
        }

        if (isTokenPair) {
            calculateAndUpdateTokenPair();
        }
    }

    private void calculateAndUpdateTokenPair() {
        Result topToken = getTopToken();
        Result middleToken = getMiddleToken();

        if (gtftaGtplusPair == null) {
            updateGtftaGtplusPair();
            return;
        }

        double ratio = parseDouble(gtftaGtplusPair.getLast());

        if (topToken == null || middleToken == null)
            return;

        double activeValue = parseDouble(mActiveValue);

        if (activeValue == 0 || ratio == 0) {
            return;
        }
        double result = 0;
        inputBoxIsGtfta = false;

        switch (selectedInputBox) {
            case TOP:
                inputBoxIsGtfta = topToken.getSymbol().equals("GTFTA/BTC");
                    result = inputBoxIsGtfta ? activeValue * ratio : activeValue / ratio;
                    break;
            case MIDDLE:
                inputBoxIsGtfta = middleToken.getSymbol().equals("GTFTA/BTC");
                result = inputBoxIsGtfta ? activeValue * ratio : activeValue / ratio;
                break;
            case BOTTOM:
                showTokenPair("");
                return;
        }

        directPairOutputStream.onNext(CurrencyHelper.roundBtc(result));

        StringBuilder builder = new StringBuilder();
        if (inputBoxIsGtfta) {
            builder.append("GTPLUS");
            builder.append(" (");
            builder.append(ratio);
            builder.append(" : 1");
            builder.append(")");
        } else {
            builder.append("GTFTA");
            builder.append(" (");
            builder.append("1 : ");
            builder.append(ratio);
            builder.append(")");
        }
        showTokenPair(builder.toString());;
    }

    private double getBtcValue(SelectedInputBox inputBox, double amount) {

        switch (inputBox) {
            case TOP:
                Result topToken = getTopToken();
                if (topToken == null) {
                    if (topSelectedIndex == 0) { // BTC
                        return 1;
                    }
                    return 0;
                } else {
                    return parseDouble(topToken.getLast());
                }
            case MIDDLE:
                Result middleToken = getMiddleToken();
                if (middleToken == null) {
                    if (middleSelectedIndex == 0) { // BTC
                        return 1;
                    }
                    return 0;
                } else {
                    return parseDouble(middleToken.getLast());
                }
            case BOTTOM:
                double btcInBpi = getCompositionRoot().getRepository().getBtcValueForPreferredCurrency();
                return 1 / btcInBpi;
        }
        return 0;
    }

    private void calculateAndUpdateTopBox(double btc, double amount) {
        if (btc == 0) {
            return;
        }
        if (topSelectedIndex == 0) {
            String output = CurrencyHelper.removeTrailingZeros(CurrencyHelper.roundBtc(btc * amount));
            topStream.onNext(output);
        }

        Result token = getTopToken();
        if (token == null) { return; }

        double btcValue = parseDouble(token.getLast());
        if (btcValue != 0) {
            double result = (btc * amount) / btcValue;
            String output = CurrencyHelper.removeTrailingZeros(CurrencyHelper.roundBtc(result));
            topStream.onNext(output);
        }
    }

    private void calculateAndUpdateMiddleBox(double btc, double amount) {
        if (btc == 0) {
           return;
        }
        if (middleSelectedIndex == 0) {

            String output = CurrencyHelper.removeTrailingZeros(CurrencyHelper.roundBtc(btc * amount));
            middleStream.onNext(output);
        }

        Result token = getMiddleToken();
        if (token == null) { return; }

        double btcValue = parseDouble(token.getLast());
        if (btcValue != 0) {
            double result = (btc * amount) / btcValue;
            String output = CurrencyHelper.removeTrailingZeros(CurrencyHelper.roundBtc(result));
            middleStream.onNext(output);
        }
    }

    private void calculateAndUpdateBottomBox(double btc, double amount) {
        if (btc == 0) {
            return;
        }
        double bpiValue = getCompositionRoot().getRepository().getBtcValueForPreferredCurrency();
        if (bpiValue == 0) {return;}
        double result = btc * amount * bpiValue;
        String output = CurrencyHelper.removeTrailingZeros(CurrencyHelper.roundBpi(result, true));
        bottomStream.onNext(output);
    }

    void clearClicked() {
        mActiveValue = "0";
        topStream.onNext("0");
        middleStream.onNext("0");
        bottomStream.onNext("0");
        outputStream.onNext(new OutputModel("0", 1));
        showTokenPair("");
    }

    Observable<OutputModel> getOutputStream() {
        return outputStream.hide();
    }

    Observable<String> getTopStream() {
        return topStream.hide();
    }

    Observable<String> getMiddleStream() {
        return middleStream.hide();
    }

    Observable<String> getBottomStream() {
        return bottomStream.hide();
    }

    Observable<Boolean> getProgressStream() {
        return progressStream.hide();
    }

    Observable<String> getDirectPairIdentifierStream() {
        return directPairIdentifierStream.hide();
    }

    Observable<String> getDirectPairOutputStream() {
        return directPairOutputStream.hide();
    }


    Single<List<String>> getBtcPairs() {
        return getCompositionRoot().getRepository().getAllBtcPairs()
                .toFlowable()
                .flatMapIterable(result -> result)
                .map(result -> result.substring(0, result.indexOf("/")))
                .toList()
                .map(result -> {
                    result.add(0, "BTC");
                    result.add(result.size() > 3 ? 3 : 1, "GTFTA-MA");
                    adapterList = result;
                    return result;
                })
                .subscribeOn(Schedulers.io());
    }

    private void updateLatestTokenValues() {
        disposeBag.add(
        getCompositionRoot().getRepository().getAllBtcPairsComplete()
                .subscribeOn(Schedulers.io())
                .toFlowable()
                .flatMapIterable(result -> result)
                .toMap(result -> result.getSymbol().substring(0, result.getSymbol().indexOf("/")), result -> result)
                .subscribe(result -> {
                    tokens = result;

                    if (tokens.size() != 0) {
                        Result gtfta = tokens.get("GTFTA");
                        if (gtfta != null) {
                            double value = parseDouble(gtfta.getLast());
                            String valueString = Double.toString(value != 0 ? value * 50 : 0);
                            tokens.put("GTFTA-MA", new Result("GTFTA/BTC", valueString));
                        }

                        calculateOtherCurrencies(mActiveValue);
                    }

                }, throwable -> Timber.e(throwable, "updateLatestTokenValues, "))
        );

        if (isTokenPair) {
            updateGtftaGtplusPair();
        }
    }

    private void updateGtftaGtplusPair() {
        disposeBag.add(
                getCompositionRoot().getRepository().getTokenBySymbol("GTFTA/GTPLUS")
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result.size() != 0) {
                                gtftaGtplusPair = result.get(0);
                                calculateOtherCurrencies(mActiveValue);
                            }

                        }, throwable -> Timber.e(throwable, "updateLatestTokenValues, "))
        );
    }

    void getLatestData() {
        disposeBag.add(
        Single.zip(getActiveCurrencies(), getActiveTokenPairs(), CombinedResultWrapper::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(wrapper -> progressStream.onNext(true))
                .doFinally(() -> {
                    progressStream.onNext(false);;
                })
                .subscribe(resultWrapper -> {
                    getCompositionRoot().getRepository().setBtcValueForPreferredCurrency(resultWrapper.getBpi().getRateFloat());
                    getCompositionRoot().getRepository().storeLatestBitBlinxData(resultWrapper.getResult())
                            .subscribeOn(Schedulers.io())
                            .subscribe(() -> {
                                updateLatestTokenValues();
                            }, throwable -> Timber.e(throwable, "getLatestData: "));

                }, throwable -> Timber.e(throwable, "getLatestData: "))
        );
    }

    private Single<BpiCurrency> getActiveCurrencies() {
        String preferredCurrency = getCompositionRoot().getRepository().getPreferredCurrency();
        return getCompositionRoot().getFetchPricesUseCase().getCurrentPrice(preferredCurrency)
                .map(CurrentPrice::getBpiCurrencies)
                .map(bpi -> Objects.requireNonNull(bpi.get(preferredCurrency)));
    }

    private Single<List<Result>> getActiveTokenPairs() {
        List<String> favorites = getCompositionRoot().getRepository().getFavorites();

        return getCompositionRoot().getFetchActiveCurrenciesUseCase().getActivePairs()
                .subscribeOn(Schedulers.io())
                .map(ActiveCurrencies::getResult)
                .flattenAsFlowable(list -> list)
                .map(result -> {
                    if (favorites.contains(result.getSymbol())) {
                        result.setFavorited(true);
                    }
                    return result;
                })
                .toList();
    }

}
