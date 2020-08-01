package com.dummycoding.mycrypto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dummycoding.mycrypto.R;
import com.dummycoding.mycrypto.helpers.CurrencyHelper;
import com.dummycoding.mycrypto.models.OwnedToken;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class OwnedTokensAdapter extends RecyclerView.Adapter<OwnedTokensAdapter.OwnedTokensHolder> {

    private Context mContext;
    private List<OwnedToken> mOwnedTokens;
    private OwnedTokensAdapterCallback mActivityCallBack;
    private double mBtcInCurrency = -1;
    private String mPreferredCurrency = "";

    public OwnedTokensAdapter(Context context, List<OwnedToken> ownedTokens) {
        mContext = context;
        mOwnedTokens = ownedTokens;
    }

    @NonNull
    @Override
    public OwnedTokensHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.owned_token_item, parent, false);
        return new OwnedTokensHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnedTokensHolder holder, int position) {
        OwnedToken ownedToken = mOwnedTokens.get(position);
        holder.setDetails(ownedToken);
    }

    @Override
    public int getItemCount() {
        return mOwnedTokens.size();
    }

    public void updateAdapter(List<OwnedToken> ownedTokens, double btcInCurrency, String preferredCurrency) {
        mOwnedTokens = ownedTokens;
        mBtcInCurrency = btcInCurrency;
        mPreferredCurrency = preferredCurrency;
        notifyDataSetChanged();
    }

    public void refreshWithUpdatedBtcCurrency(double btcInCurrency) {
        mBtcInCurrency = btcInCurrency;
        notifyDataSetChanged();
    }

    class OwnedTokensHolder extends RecyclerView.ViewHolder {

        private TextView token;
        private TextView tokenValue;

        OwnedTokensHolder(@NonNull View itemView) {
            super(itemView);

            token = itemView.findViewById(R.id.chosenToken);
            tokenValue = itemView.findViewById(R.id.chosenTokenValue);
        }

        void setDetails(OwnedToken ownedToken) {
            NumberFormat nf = new DecimalFormat("##.###");
            token.setText(String.format(mContext.getString(R.string.chosen_currency),
                    nf.format(ownedToken.getTokenAmount()), ownedToken.getToken()));

            boolean isBtc = ownedToken.getToken().equals("BTC");

            double totalTokenInBFiat = isBtc
                    ? ownedToken.getTokenAmount() * mBtcInCurrency
                    : ownedToken.getTokenAmount() * ownedToken.getTokenInBtc() * mBtcInCurrency;
            tokenValue.setText(String.format(mContext.getString(R.string.chosen_currency_value), (isBtc
                    ? CurrencyHelper.roundBtc(totalTokenInBFiat)
                    : CurrencyHelper.roundBpi(totalTokenInBFiat, false)), mPreferredCurrency));
        }
    }
}
