package com.dummycoding.blinxtools.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dummycoding.blinxtools.R;
import com.dummycoding.blinxtools.helpers.CurrencyHelper;
import com.dummycoding.blinxtools.models.bitblinx.Result;
import com.google.android.material.card.MaterialCardView;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public class BitBlinxMainAdapter extends RecyclerView.Adapter<BitBlinxMainAdapter.MainHolder> {

    private Context mContext;
    private List<Result> mPairs;
    private SharedPreferences mSharedPreferences;
    private boolean mShow24HighLow;

    public BitBlinxMainAdapter(Context context, List<Result> pairs) {
        mContext = context;
        mPairs = pairs;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    @Override
    public MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new MainHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainHolder holder, int position) {
        Result result = mPairs.get(position);
        holder.setDetails(result);
    }

    @Override
    public int getItemCount() {
        return mPairs.size();
    }

    public void updateAdapter(List<Result> results) {
        mPairs = results;
        mShow24HighLow = mSharedPreferences.getBoolean(mContext.getString(R.string.high_low_key_24h), false);
        notifyDataSetChanged();
    }

    class MainHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardView;
        private ImageView gainIcon;
        private TextView gainText;
        private TextView pair;
        private TextView highPriceBtc, lowPriceBtc;
        private TextView lastPriceDetailsBtc, highPriceDetailsBtc, lowPriceDetailsBtc;
        private TextView lastPriceDetailsCurrency;

        MainHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card);
            gainIcon = itemView.findViewById(R.id.gainIcon);
            gainText = itemView.findViewById(R.id.gain);
            pair = itemView.findViewById((R.id.pair));
            lowPriceBtc = itemView.findViewById(R.id.lowPrice);
            highPriceBtc = itemView.findViewById(R.id.highPrice);
            lastPriceDetailsBtc = itemView.findViewById(R.id.lastPriceDetailsBtc);
            highPriceDetailsBtc = itemView.findViewById(R.id.highPriceDetailsBtc);
            lowPriceDetailsBtc = itemView.findViewById(R.id.lowPriceDetailsBtc);
            lastPriceDetailsCurrency = itemView.findViewById(R.id.lastPriceDetailsCurrency);
        }

        void setDetails(Result result) {
            pair.setText(result.getSymbol());
            String rightPair = result.getSymbol().substring(result.getSymbol().lastIndexOf("/") + 1);
            boolean btcPair = rightPair.contains("BTC");

            boolean negativeChange = result.getPriceChange().contains("-");
            int gainColor = ContextCompat.getColor(mContext, negativeChange ? R.color.error : R.color.green);

            cardView.setStrokeColor(gainColor);
            gainIcon.setImageResource(negativeChange ? R.drawable.menu_down : R.drawable.menu_up);
            ImageViewCompat.setImageTintList(gainIcon, ColorStateList.valueOf(gainColor));
            gainText.setText(result.getPriceChange() + "%");
            gainText.setTextColor(gainColor);

            String bitBlinxPair = CurrencyHelper.removeTrailingZeros(result.getLast()) + " " + (btcPair ? "BTC" : rightPair);
            lastPriceDetailsBtc.setText(bitBlinxPair);

            float btcValuePreferredCurrency = mSharedPreferences.getFloat(mContext.getString(R.string.btc_value_for_preferred_currency_key), -1);

            if (btcPair && btcValuePreferredCurrency != -1){
                String preferredCurrency = mSharedPreferences.getString(mContext.getString(R.string.preferred_currency_key), "EUR");

                float valueInCurrency = Float.parseFloat(result.getLast()) * btcValuePreferredCurrency;
                String valueInCurrencyString = CurrencyHelper.round(valueInCurrency);

                lastPriceDetailsCurrency.setText(valueInCurrencyString + " " + preferredCurrency);
                lastPriceDetailsCurrency.setVisibility(View.VISIBLE);
            } else {
                lastPriceDetailsCurrency.setVisibility(View.GONE);
            }

            if (mShow24HighLow) {
                highPriceDetailsBtc.setText(CurrencyHelper.removeTrailingZeros(result.getHigh()) + " " + (btcPair ? "BTC" : rightPair));
                lowPriceDetailsBtc.setText(CurrencyHelper.removeTrailingZeros(result.getLow()) + " " + (btcPair ? "BTC" : rightPair));
            }
            setHighLowVisibility();
        }

        void setHighLowVisibility() {
            highPriceBtc.setVisibility(mShow24HighLow ? View.VISIBLE : View.GONE);
            lowPriceBtc.setVisibility(mShow24HighLow ? View.VISIBLE : View.GONE);
            highPriceDetailsBtc.setVisibility(mShow24HighLow ? View.VISIBLE : View.GONE);
            lowPriceDetailsBtc.setVisibility(mShow24HighLow ? View.VISIBLE : View.GONE);
        }
    }
}