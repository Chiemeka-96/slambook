package com.gmonetix.slambook.slides;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmonetix.slambook.R;

public class SecondSlide extends Fragment {

    public SecondSlide() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.welcome_slide2, container, false);
    }

}
