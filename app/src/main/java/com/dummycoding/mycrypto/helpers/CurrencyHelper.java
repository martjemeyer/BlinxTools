package com.dummycoding.mycrypto.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyHelper {

    public static String roundBpi(double number, boolean showExtraDecimals) {
        BigDecimal bd = new BigDecimal(Double.toString(number));
        int places = showExtraDecimals ? 4 : 2;
        if (number < 1) {
            char[] numberArray = Double.toString(number).toCharArray();
            for (int i = 2; i < numberArray.length - 1; i++) {
                if (numberArray[i] != '0') {
                    places = i;
                    break;
                }
            }
        }
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.toString();
    }

    public static String roundBtc(double number) {
        BigDecimal bd = new BigDecimal(Double.toString(number));
        int places = 6;
        if (number < 1) {
            char[] numberArray = Double.toString(number).toCharArray();
            for (int i = 2; i < numberArray.length - 1; i++) {
                if (numberArray[i] != '0') {
                    places = i;
                    break;
                }
            }
        }
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.toString();
    }

    public static String removeTrailingZeros(String input) {
        BigDecimal stripedVal = new BigDecimal(input).stripTrailingZeros();
        return stripedVal.toPlainString();
    }
}
