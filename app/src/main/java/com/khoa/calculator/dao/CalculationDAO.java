package com.khoa.calculator.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.khoa.calculator.model.Calculation;

import java.util.List;

@Dao
public interface CalculationDAO {
    @Query("Select * from calculation")
    List<Calculation> getCalculationList();

    @Insert
    void insertCalculation(Calculation calculation);

    @Query("Delete from calculation")
    void clear();
}
