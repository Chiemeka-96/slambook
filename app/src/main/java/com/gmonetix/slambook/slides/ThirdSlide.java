package com.gmonetix.slambook.slides;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmonetix.slambook.R;

public class ThirdSlide extends Fragment {

    public ThirdSlide() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.welcome_slide3, container, false);
    }

}
