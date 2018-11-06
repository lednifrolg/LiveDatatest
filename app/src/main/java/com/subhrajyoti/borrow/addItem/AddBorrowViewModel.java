package com.subhrajyoti.borrow.addItem;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;

import com.subhrajyoti.borrow.db.AppDatabase;
import com.subhrajyoti.borrow.db.DatabaseCreator;
import com.subhrajyoti.borrow.db.model.BorrowModel;


public class AddBorrowViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    final DatabaseCreator databaseCreator = DatabaseCreator.getInstance(this.getApplication());

    public AddBorrowViewModel(Application application) {
        super(application);

        appDatabase = databaseCreator.getDatabase();

    }

    public void addBorrow(final BorrowModel borrowModel) {
        new addAsyncTask(appDatabase).execute(borrowModel);
    }

    private static class addAsyncTask extends AsyncTask<BorrowModel, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final BorrowModel... params) {
            db.getBorrowDao().addBorrow(params[0]);
            return null;
        }

    }
}
