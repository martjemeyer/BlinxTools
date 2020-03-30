package com.dummycoding.blinxtools.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dummycoding.blinxtools.R;
import com.dummycoding.blinxtools.pojos.bitblinx.Result;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class BitBlinxMainAdapter extends RecyclerView.Adapter<BitBlinxMainAdapter.MainHolder> {

    private Context context;
    private List<Result> pairs;

    public BitBlinxMainAdapter(Context context, List<Result> pairs) {
        this.context = context;
        this.pairs = pairs;
    }

    @NonNull
    @Override
    public MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new MainHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainHolder holder, int position) {
        Result result = pairs.get(position);
        holder.setDetails(result);
    }

    @Override
    public int getItemCount() {
        return pairs.size();
    }

    public void updateAdapter(List<Result> results) {
        this.pairs = results;
        notifyDataSetChanged();
    }

    class MainHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardView;
        private ImageView gainIcon;
        private TextView gainText;
        private TextView pair;
        private TextView lastPriceDetailsBtc, highPriceDetailsBtc, lowPriceDetailsBtc;
        private TextView lastPriceDetailsCurrency;

        MainHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card);
            gainIcon = itemView.findViewById(R.id.gainIcon);
            gainText = itemView.findViewById(R.id.gain);
            pair = itemView.findViewById((R.id.pair));
            lastPriceDetailsBtc = itemView.findViewById(R.id.lastPriceDetailsBtc);
            highPriceDetailsBtc = itemView.findViewById(R.id.highPriceDetailsBtc);
            lowPriceDetailsBtc = itemView.findViewById(R.id.lowPriceDetailsBtc);
            lastPriceDetailsCurrency = itemView.findViewById(R.id.lastPriceDetailsCurrency);
        }

        void setDetails(Result result) {
            pair.setText(result.symbol);

            boolean negativeChange = result.priceChange.contains("-");
            int gainColor = ContextCompat.getColor(context, negativeChange ? R.color.red : R.color.green);
            cardView.setStrokeColor(gainColor);
            gainIcon.setImageResource(negativeChange ? R.drawable.menu_down : R.drawable.menu_up);
            ImageViewCompat.setImageTintList(gainIcon, ColorStateList.valueOf(gainColor));
            gainText.setText(result.priceChange + "%");
            gainText.setTextColor(gainColor);

            lastPriceDetailsBtc.setText(result.last + " BTC");
            highPriceDetailsBtc.setText(result.high + " BTC");
            lowPriceDetailsBtc.setText(result.low + " BTC");
            lastPriceDetailsCurrency.setText(Float.parseFloat(result.last) * 1000 + " EUR");
        }
    }
}