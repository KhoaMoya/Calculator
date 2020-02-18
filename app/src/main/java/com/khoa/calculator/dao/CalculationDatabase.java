package com.khoa.calculator.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.khoa.calculator.model.ArrayListConverters;
import com.khoa.calculator.model.Calculation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Calculation.class}, version = 2, exportSchema = false)
@TypeConverters({ArrayListConverters.class})
public abstract class CalculationDatabase extends RoomDatabase {

    private static String DB_Name = "calculation_db";
    private static CalculationDatabase intansce;
    public abstract CalculationDAO calDao();
    public static ExecutorService dbWriteExecutor = Executors.newFixedThreadPool(4);


    public static CalculationDatabase getInstance(Context context){
        if(intansce==null){
//            synchronized (CalculationDatabase.class){
                if(intansce == null) {
                    try {
                        intansce = Room.databaseBuilder(context.getApplicationContext(), CalculationDatabase.class, DB_Name).build();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
//        }
        return intansce;
    }
}
