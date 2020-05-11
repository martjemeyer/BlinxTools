package com.dummycoding.mycrypto.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dummycoding.mycrypto.markets.MarketsFragment;
import com.dummycoding.mycrypto.owned_currencies.OwnedTokensFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class ViewPagerAdapter extends FragmentStateAdapter {

    ArrayList<Fragment> fragments = new ArrayList<Fragment>( Arrays.asList(MarketsFragment.newInstance(), OwnedTokensFragment.newInstance()) );

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
