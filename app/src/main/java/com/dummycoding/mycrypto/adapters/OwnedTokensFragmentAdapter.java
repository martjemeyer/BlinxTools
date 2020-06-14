package com.dummycoding.mycrypto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dummycoding.mycrypto.R;
import com.dummycoding.mycrypto.databinding.ListItemOwnedTokenFragmentBinding;
import com.dummycoding.mycrypto.helpers.CurrencyHelper;
import com.dummycoding.mycrypto.models.OwnedToken;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class OwnedTokensFragmentAdapter extends RecyclerView.Adapter<OwnedTokensFragmentAdapter.OwnedTokensFragmentHolder> {

    private Context mContext;
    private List<OwnedToken> mOwnedTokens;
    private double mBtcInCurrency = -1;
    private String mPreferredCurrency = "";
    private OwnedTokensAdapterCallback mCallback;

    public OwnedTokensFragmentAdapter(Context context, List<OwnedToken> ownedTokens, OwnedTokensAdapterCallback callback) {
        mContext = context;
        mOwnedTokens = ownedTokens;
        mCallback = callback;
    }

    @NonNull
    @Override
    public OwnedTokensFragmentAdapter.OwnedTokensFragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemOwnedTokenFragmentBinding binding = ListItemOwnedTokenFragmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OwnedTokensFragmentHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnedTokensFragmentAdapter.OwnedTokensFragmentHolder holder, int position) {
        OwnedToken ownedToken = mOwnedTokens.get(position);
        holder.setDetails(ownedToken);
        holder.binding.card.setOnClickListener(v -> handleOnCardClicked(position));
    }

    private void handleOnCardClicked(int position) {
        OwnedToken token = mOwnedTokens.get(position);
        mCallback.updateOwnedToken(token);
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

    class OwnedTokensFragmentHolder extends RecyclerView.ViewHolder {
        ListItemOwnedTokenFragmentBinding binding;

        OwnedTokensFragmentHolder(ListItemOwnedTokenFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setDetails(OwnedToken ownedToken) {
            NumberFormat nf = new DecimalFormat("##.###");
            String some = String.format(mContext.getString(R.string.chosen_currency),
                    nf.format(ownedToken.getTokenAmount()), ownedToken.getToken());

            double totalTokenInBFiat = ownedToken.getToken().equals("BTC") ? ownedToken.getTokenAmount() * mBtcInCurrency : ownedToken.getTokenAmount() * ownedToken.getTokenInBtc() * mBtcInCurrency;
            String someOther = String.format(mContext.getString(R.string.chosen_currency_value),
                    CurrencyHelper.round(totalTokenInBFiat), mPreferredCurrency);

            binding.pair.setText(ownedToken.getToken());
            binding.amountDetail.setText(nf.format(ownedToken.getTokenAmount()));
            binding.priceInCurrency.setText(String.format(mContext.getString(R.string.total_value_in), mPreferredCurrency));
            binding.priceInCurrencyDetail.setText(CurrencyHelper.round(totalTokenInBFiat));
            binding.priceInBtc.setText(String.format(mContext.getString(R.string.total_value_in), "BTC"));
            binding.priceInBtcDetail.setText(CurrencyHelper.round(ownedToken.getTokenInBtc() * ownedToken.getTokenAmount()));
            /*
            NumberFormat nf = new DecimalFormat("##.###");
            token.setText(String.format(mContext.getString(R.string.chosen_currency),
                    nf.format(ownedToken.getTokenAmount()), ownedToken.getToken()));

            double totalTokenInBFiat = ownedToken.getToken().equals("BTC") ? ownedToken.getTokenAmount() * mBtcInCurrency : ownedToken.getTokenAmount() * ownedToken.getTokenInBtc() * mBtcInCurrency;
            tokenValue.setText(String.format(mContext.getString(R.string.chosen_currency_value),
                    CurrencyHelper.round(totalTokenInBFiat), mPreferredCurrency));

             */
        }
    }
}
