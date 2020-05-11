package com.dummycoding.mycrypto.owned_currencies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dummycoding.mycrypto.databinding.FragmentOwnedTokensBinding;

public class OwnedTokensFragment extends Fragment {

    private FragmentOwnedTokensBinding binding;
    private OwnedTokensViewModel mViewModel;


    public static OwnedTokensFragment newInstance() {
        return new OwnedTokensFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOwnedTokensBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OwnedTokensViewModel.class);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    
}
