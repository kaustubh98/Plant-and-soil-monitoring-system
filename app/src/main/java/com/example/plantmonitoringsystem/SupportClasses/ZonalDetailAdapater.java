package com.example.plantmonitoringsystem.SupportClasses;

import com.example.plantmonitoringsystem.Fragments.FragmentHumidity;
import com.example.plantmonitoringsystem.Fragments.FragmentLight;
import com.example.plantmonitoringsystem.Fragments.FragmentMoisture;
import com.example.plantmonitoringsystem.Fragments.FragmentTemperature;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ZonalDetailAdapater extends FragmentPagerAdapter {

    String Zone;

    public ZonalDetailAdapater(FragmentManager fm, String zone) {
        super(fm);
        Zone = zone;
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
                return "Temperature";
            case 1:
                return "Humidity";
            case 2:
                return "Soil Moisture";
            case 3:
                return "Light Intensity";
        }

        return null;
    }
}
