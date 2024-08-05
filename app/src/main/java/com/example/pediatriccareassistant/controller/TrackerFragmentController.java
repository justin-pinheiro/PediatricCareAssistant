package com.example.pediatriccareassistant.controller;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pediatriccareassistant.controller.firebasehandler.ChildHandler;
import com.example.pediatriccareassistant.model.Child;
import com.example.pediatriccareassistant.model.callback.ChildrenCallback;
import com.example.pediatriccareassistant.view.ChartsFragment;
import com.example.pediatriccareassistant.view.MeasurementFragment;

import java.util.ArrayList;

public class TrackerFragmentController
{
    public static final String MEASUREMENTS_TAB_TEXT = "Measurements";
    public static final String CHARTS_TAB_TEXT = "Growth charts";

    private ArrayList<Child> userChildren;
    private Child selectedChild;
    private final FragmentManager fragmentManager;
    private final ViewPager2 fragmentContainer;

    public TrackerFragmentController(FragmentManager fragmentManager, ViewPager2 fragmentContainer) {
        this.fragmentManager = fragmentManager;
        this.fragmentContainer = fragmentContainer;
    }

    public void onChildSelected(int position)
    {
        selectedChild = userChildren.get(position);
    }

    public interface OnChildSelectedListener {
        void onChildSelected(Child child);
    }

    /**
     * Retrieves the list of children from the database.
     *
     * @param callback The callback to handle the retrieved children or errors.
     */
    public void retrieveUserChildren(ChildrenCallback callback)
    {
        ChildHandler.getInstance().retrieveChildrenFromUser(AuthenticationController.getInstance().getUserUid(), new ChildrenCallback() {
            @Override
            public void onChildrenRetrieved(ArrayList<Child> children)
            {
                if (!children.isEmpty())
                {
                    selectedChild = children.get(0);

                    fragmentContainer.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            notifyChildDataFragment(selectedChild);
                        }
                    });
                }

                userChildren = children;

                callback.onChildrenRetrieved(children);
            }

            @Override
            public void onChildrenNotFound()
            {
                callback.onChildrenNotFound();
            }
        });
    }

    /**
     * Notifies the current fragment about the selected child.
     *
     * @param child The selected child to notify the fragment about.
     */
    public void notifyChildDataFragment(Child child)
    {
        if (child == null){
            throw new NullPointerException("Child cannot be null.");
        }

        Fragment currentFragment = fragmentManager.findFragmentByTag("f" + fragmentContainer.getCurrentItem());

        if (currentFragment instanceof MeasurementFragment)
        {
            ((MeasurementFragment) currentFragment).onChildSelected(child);
        }
        else if (currentFragment instanceof ChartsFragment)
        {
            ((ChartsFragment) currentFragment).onChildSelected(child);
        }
    }

    public ArrayList<Child> getChildren()
    {
        return userChildren;
    }

    public Child getSelectedChild() {
        return selectedChild;
    }

    public ArrayList<String> getChildrenNames()
    {
        ArrayList<String> names = new ArrayList<>();

        for (Child child: userChildren) {
            names.add(child.getName());
        }

        return names;
    }
}

