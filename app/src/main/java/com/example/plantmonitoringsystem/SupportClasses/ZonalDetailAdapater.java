package com.example.plantmonitoringsystem.SupportClasses;

import android.content.Context;

import com.example.plantmonitoringsystem.Fragments.FragmentHumidity;
import com.example.plantmonitoringsystem.Fragments.FragmentLight;
import com.example.plantmonitoringsystem.Fragments.FragmentMoisture;
import com.example.plantmonitoringsystem.Fragments.FragmentTemperature;
import com.example.plantmonitoringsystem.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ZonalDetailAdapater extends FragmentPagerAdapter {

    String Zone;
    Context context;

    public ZonalDetailAdapater(FragmentManager fm,Context context, String zone) {
        super(fm);
        Zone = zone;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new FragmentTemperature(Zone);
            case 1:
                return new FragmentHumidity(Zone);
            case 2:
                return new FragmentMoisture(Zone);
            case 3:
                return new FragmentLight(Zone);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return context.getResources().getString(R.string.Temperature);
            case 1:
                return context.getResources().getString(R.string.Humidity);
            case 2:
                return context.getResources().getString(R.string.Moisture);
            case 3:
                return context.getResources().getString(R.string.LightIntensity);
        }

        return null;
    }
}
