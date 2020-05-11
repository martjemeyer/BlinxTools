package com.dummycoding.mycrypto.common;

import androidx.fragment.app.Fragment;

import com.dummycoding.mycrypto.MyCrypto;
import com.dummycoding.mycrypto.di.CompositionRoot;

public class BaseFragment extends Fragment {
    public CompositionRoot getCompositionRoot() {
        return ((MyCrypto)getActivity().getApplication()).getCompositionRoot();
    }
}
