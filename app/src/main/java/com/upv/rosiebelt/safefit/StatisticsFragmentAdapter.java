package com.upv.rosiebelt.safefit;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.upv.rosiebelt.safefit.fragments.StatisticsMonth;
import com.upv.rosiebelt.safefit.fragments.StatisticsWeek;
import com.upv.rosiebelt.safefit.fragments.StatisticsYear;

public class StatisticsFragmentAdapter extends FragmentPagerAdapter{
    private Context context;

    public StatisticsFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 2){
            return new StatisticsYear();
        }if(position == 1){
            return new StatisticsMonth();
        }else{
            return new StatisticsWeek();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Week";
            case 1:
                return "Month";
            case 2:
                return "Year";
            default:
                return null;

        }
    }
}
