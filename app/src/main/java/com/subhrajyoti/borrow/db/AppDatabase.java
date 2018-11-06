package com.subhrajyoti.borrow.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.subhrajyoti.borrow.db.model.BorrowModel;
import com.subhrajyoti.borrow.db.model.BorrowModelDao;

@Database(entities = {BorrowModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {


    static final String DATABASE_NAME = "demo-db";


    public abstract BorrowModelDao getBorrowDao();

}
