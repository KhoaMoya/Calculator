package com.khoa.calculator.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.khoa.calculator.R;
import com.khoa.calculator.databinding.CalculatorFragmentBinding;
import com.khoa.calculator.model.Calculation;
import com.khoa.calculator.viewmodel.CalculatorViewModel;

import java.util.ArrayList;

public class CalculatorFragment extends Fragment {

    private CalculatorViewModel mViewModel;
    private CalculatorFragmentBinding mBinding;


    public static CalculatorFragment newInstance() {
        return new CalculatorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.calculator_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CalculatorViewModel.class);
        if(savedInstanceState==null) mViewModel.init(getContext());
        mBinding.setViewmodel(mViewModel);

        setupBinding();
    }


    public void setupBinding(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setStackFromEnd(true);
        mBinding.recyclerHistory.setLayoutManager(layoutManager);
        mBinding.recyclerHistory.setHasFixedSize(true);
        mBinding.recyclerHistory.setAdapter(mViewModel.adapterHistory);

        mViewModel.mNotification.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mBinding.txtNotification.setText(s);
                mBinding.txtNotification.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.toast));
            }
        });

        mViewModel.mResult.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.length()>15) mBinding.txtResult.setTextSize(30);
                else mBinding.txtResult.setTextSize(40);

                mBinding.txtResult.setText(s);
                mBinding.txtResult.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_appear));
                mBinding.recyclerHistory.scrollToPosition(mBinding.recyclerHistory.getAdapter().getItemCount()-1);
            }
        });

        mViewModel.mExpression.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.length()>15) mBinding.txtResult.setTextSize(30);
                else mBinding.txtResult.setTextSize(40);

                mBinding.txtResult.setText(s);
            }
        });

        mBinding.keyDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mViewModel.longClickDelete();
                return true;
            }
        });

        mViewModel.mHistoryList.observe(getViewLifecycleOwner(), new Observer<ArrayList<Calculation>>() {
            @Override
            public void onChanged(ArrayList<Calculation> calculations) {
                mViewModel.adapterHistory.setCalList(calculations);
                if(calculations.size()>0) mBinding.recyclerHistory.scrollToPosition(calculations.size()-1);
            }
        });
    }


}
