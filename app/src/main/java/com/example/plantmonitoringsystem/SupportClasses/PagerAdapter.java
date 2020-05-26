package com.example.plantmonitoringsystem.SupportClasses;

import android.content.Context;

import com.example.plantmonitoringsystem.Fragments.FragmentAverage;
import com.example.plantmonitoringsystem.Fragments.FragmentDisease;
import com.example.plantmonitoringsystem.Fragments.FragmentZonal;
import com.example.plantmonitoringsystem.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new FragmentAverage();
            case 1:
                return new FragmentZonal();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Average";
            case 1:
                return "Zones";
        }

        return null;
    }
}
