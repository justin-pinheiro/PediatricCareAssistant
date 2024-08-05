package com.example.pediatriccareassistant.controller.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pediatriccareassistant.controller.TrackerFragmentController;
import com.example.pediatriccareassistant.model.Child;
import com.example.pediatriccareassistant.view.ChartsFragment;
import com.example.pediatriccareassistant.view.MeasurementFragment;

/**
 * Adapter for managing the fragments in a ViewPager for tracking measurements and charts.
 */
public class TrackerViewPagerAdapter extends FragmentStateAdapter {

    private TrackerFragmentController.OnChildSelectedListener childSelectedListener;
    private Child currentChild;

    /**
     * Constructs a new TrackerViewPagerAdapter with the specified fragment and listener.
     *
     * @param fragment The fragment that owns this adapter
     * @param listener The listener for child selection events
     */
    public TrackerViewPagerAdapter(@NonNull Fragment fragment, TrackerFragmentController.OnChildSelectedListener listener) {
        super(fragment);
        this.childSelectedListener = listener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                ChartsFragment chartsFragment = ChartsFragment.newInstance(currentChild);
                chartsFragment.reloadChart();
                return chartsFragment;
            default:
                return new MeasurementFragment();
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }

    public void setCurrentChild(Child child) {
        this.currentChild = child;
    }
}