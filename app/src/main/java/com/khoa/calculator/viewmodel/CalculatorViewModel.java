package com.khoa.calculator.viewmodel;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.khoa.calculator.dao.CalReponsitory;
import com.khoa.calculator.model.Calculation;
import com.khoa.calculator.utils.Calculator;
import com.khoa.calculator.view.AdapterHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CalculatorViewModel extends ViewModel {

    public Calculator mCalculator;
    private CalReponsitory mCalReponsitory;
    public MutableLiveData<String> mResult;
    public MutableLiveData<String> mExpression;
    public MutableLiveData<String> mNotification;
    public MutableLiveData<ArrayList<Calculation>> mHistoryList;
    public AdapterHistory adapterHistory;
    public ObservableInt SET_FLAG;

    public void init(Context context) {
        mCalReponsitory = new CalReponsitory(context);
        mHistoryList = new MutableLiveData<>(new ArrayList<Calculation>());
        mResult = new MutableLiveData<>();
        mExpression = new MutableLiveData<>("");
        mNotification = new MutableLiveData<>("");
        mCalculator = new Calculator(this);
        adapterHistory = new AdapterHistory(getItemListener());
        SET_FLAG = new ObservableInt(View.GONE);

        loadHistory();
    }

    public void loadHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Calculation> list = (ArrayList<Calculation>) mCalReponsitory.getCalculationList();
                if (mHistoryList == null) mHistoryList.postValue(new ArrayList<Calculation>());
                mHistoryList.postValue(list);
            }
        }).start();
    }

    public void addToNodeList(View view) {
        TextView txt = (TextView) view;
        String input = (String) txt.getText();
        mCalculator.addToNodeList(input);
    }

    public void showExpression(String string) {
        mExpression.setValue(string);
    }

    public void showResult(ArrayList<String> nodeList, String result) {
        Calculation cal = new Calculation((ArrayList<String>) nodeList.clone(), result, System.currentTimeMillis());
        mCalReponsitory.insertCalculation(cal);
        adapterHistory.insertCal(cal);
        mResult.setValue(result);
    }

    public void showNotification(String string) {
        mNotification.setValue(string);
    }

    public AdapterHistory.ItemClickListener getItemListener() {
        return new AdapterHistory.ItemClickListener() {
            @Override
            public void onClick(String result) {
                mCalculator.nodeList.clear();
                mCalculator.nodeList.add(result);
                mExpression.setValue(result);
            }
        };
    }

    public void longClickDelete() {
        int size = mCalculator.nodeList.size();
        if (size > 0) {
            mCalculator.nodeList.remove(size - 1);
            mExpression.setValue(mCalculator.showCalculation());
        }
    }

    public void onClickDelete() {
        mCalculator.addToNodeList("Del");
    }

    public void onClickClear() {
        mHistoryList.setValue(new ArrayList<Calculation>());
        mCalReponsitory.clearCalculation();
    }

    public void setSET_FLAG(boolean visiable){
        int v = visiable ? View.VISIBLE : View.GONE;
        SET_FLAG.set(v);
    }

    public String getTime() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return df.format(date);
    }

}
