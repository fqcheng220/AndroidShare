package com.fqcheng220.android.androidshare.dialog;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fqcheng220.android.androidshare.R;

import java.util.ArrayList;
import java.util.List;

public class DialogActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        showFragment1();
    }

    private void showOrCreateFragment(int containerId, String tag, String fragmentCls) {
        if (TextUtils.isEmpty(tag) && TextUtils.isEmpty(fragmentCls))
            return;
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> allList = fragmentManager.getFragments();
        List<Fragment> sameList = new ArrayList<>();
        Fragment fragmentFound = null;
        for (Fragment fragment : allList) {
            if (fragment != null && fragment.getView() != null && fragment.getView().getId() == containerId && tag.equals(fragment.getTag())) {
                if (!fragmentCls.equals(fragment.getClass().getCanonicalName())) {
                    sameList.add(fragment);
                } else {
                    fragmentFound = fragment;
                }
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fragment : sameList) {
            fragmentTransaction.hide(fragment);
        }
        if (fragmentFound != null) {
            fragmentTransaction.hide(fragmentFound);
        } else {
            fragmentTransaction.add(containerId, Fragment.instantiate(this, fragmentCls));
        }
        fragmentTransaction.commit();
    }

    public void showFragment1() {
        showOrCreateFragment(R.id.fl_container, "Fragment1",Fragment1.class.getCanonicalName());
    }

    public void showFragment2() {
        showOrCreateFragment(R.id.fl_container, "Fragment2",Fragment2.class.getCanonicalName());
    }

    public void replaceFragment2() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_container, Fragment.instantiate(this, Fragment2.class.getCanonicalName()), "Fragment2");
        fragmentTransaction.commit();
    }

}
