package com.fqcheng220.android.androidshare.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fqcheng220.android.androidshare.R;

public class Fragment1 extends Fragment {
    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog1,container,false);
        ((TextView)view.findViewById(R.id.tv_switch)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogActivity activity = (DialogActivity)getActivity();
                if(activity != null){
                    activity.replaceFragment2();
                }
            }
        });
        ((TextView)view.findViewById(R.id.tv_show_dialog)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        ((TextView)view.findViewById(R.id.tv_finish)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        ((TextView)view.findViewById(R.id.tv_show_dialog_delay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showDialog();
                    }
                },10);
            }
        });

        ((TextView)view.findViewById(R.id.tv_finish)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

        ((TextView)view.findViewById(R.id.tv_show_dialog_and_finish)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                mActivity.finish();
            }
        });
        return view;
    }

    private void showDialog(){
        new AlertDialog.Builder(mActivity).setTitle("title").show();
    }
}
