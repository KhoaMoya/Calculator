package com.khoa.calculator.dao;

import android.content.Context;

import com.khoa.calculator.model.Calculation;

import java.util.List;

public class CalReponsitory {
    public CalculationDAO mCalDAO;

    public CalReponsitory(Context context) {
        CalculationDatabase db = CalculationDatabase.getInstance(context);
        mCalDAO = db.calDao();
    }

    public List<Calculation> getCalculationList(){
        return mCalDAO.getCalculationList();
    }

    public void insertCalculation(final Calculation calculation){
        CalculationDatabase.dbWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mCalDAO.insertCalculation(calculation);
            }
        });

    }

    public void clearCalculation(){
        CalculationDatabase.dbWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mCalDAO.clear();
            }
        });

    }
}
