package com.example.weatherapp.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.weatherapp.fragments.DailyFragment;
import com.example.weatherapp.fragments.NowFragment;
import com.example.weatherapp.fragments.RadarFragment;

public class MyFragmentAdapter extends FragmentPagerAdapter {

    private String listTab[] = {"NOW", "8 DAYS", "RADAR"};

    private NowFragment nowFragment;
    private DailyFragment dailyFragment;
    private RadarFragment radarFragment;

    public MyFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
        nowFragment = new NowFragment();
        dailyFragment = new DailyFragment();
        radarFragment = new RadarFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return nowFragment;
        } else if (position == 1) {
            return dailyFragment;
        } else if (position == 2) {
            return radarFragment;
        }

        return null;
    }


    @Override
    public int getCount() {
        return listTab.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listTab[position];
    }
}
