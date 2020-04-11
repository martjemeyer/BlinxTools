package com.dummycoding.blinxtools.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dummycoding.blinxtools.R;
import com.dummycoding.blinxtools.helpers.CurrencyHelper;
import com.dummycoding.blinxtools.models.OwnedToken;

import java.util.List;
import java.util.Random;

public class OwnedTokensAdapter extends RecyclerView.Adapter<OwnedTokensAdapter.OwnedTokensHolder> {

    private Context mContext;
    private List<OwnedToken> mOwnedTokens;
    private OwnedTokensAdapterCallback mActivityCallBack;

    public OwnedTokensAdapter(Context context, List<OwnedToken> ownedTokens, OwnedTokensAdapterCallback callBack) {
        mContext = context;
        mOwnedTokens = ownedTokens;
        mActivityCallBack = callBack;
    }

    @NonNull
    @Override
    public OwnedTokensHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.owned_currency_item, parent, false);
        return new OwnedTokensHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnedTokensHolder holder, int position) {
        OwnedToken ownedToken = mOwnedTokens.get(position);
        holder.setDetails(ownedToken);

        holder.editTokenButton.setOnClickListener(v -> handleEditButtonClicked(position));
    }

    private void handleEditButtonClicked(int position) {
        OwnedToken ownedToken = mOwnedTokens.get(position);
        Random r = new Random();
        int low = 100;
        int high = 10000;
        int result = r.nextInt(high-low) + low;
        ownedToken.setTokenAmount(result);
        mActivityCallBack.updateOwnedToken(ownedToken);
    }

    @Override
    public int getItemCount() {
        return mOwnedTokens.size();
    }

    public void updateAdapter(List<OwnedToken> ownedTokens) {
        mOwnedTokens = ownedTokens;
        notifyDataSetChanged();
    }

    class OwnedTokensHolder extends RecyclerView.ViewHolder {

        private ImageButton editTokenButton;
        private TextView token;
        private TextView tokenValue;

        OwnedTokensHolder(@NonNull View itemView) {
            super(itemView);

            editTokenButton = itemView.findViewById(R.id.removeTokenButton);
            token = itemView.findViewById(R.id.chosenToken);
            tokenValue = itemView.findViewById(R.id.chosenTokenValue);
        }

        void setDetails(OwnedToken ownedToken) {
            token.setText(String.format(mContext.getString(R.string.chosen_currency),
                    ownedToken.getTokenAmount(), ownedToken.getToken()));

            double totalTokenInBFiat = ownedToken.getTokenAmount() * ownedToken.getTokenInBtc() * ownedToken.getBtcInFiat();
            tokenValue.setText(String.format(mContext.getString(R.string.chosen_currency_value),
                    CurrencyHelper.round(totalTokenInBFiat), ownedToken.getFiatCurrency()));
        }
    }
}
