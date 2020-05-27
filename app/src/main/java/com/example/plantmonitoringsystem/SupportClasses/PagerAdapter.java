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

    Context context;

    public PagerAdapter(FragmentManager fm, Context context) {

        super(fm);
        this.context = context;
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
                return context.getResources().getString(R.string.average);
            case 1:
                return context.getResources().getString(R.string.zones);
        }

        return null;
    }
}
