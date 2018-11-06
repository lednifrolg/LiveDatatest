package com.subhrajyoti.borrow.listItems;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;

import com.subhrajyoti.borrow.db.AppDatabase;
import com.subhrajyoti.borrow.db.DatabaseCreator;
import com.subhrajyoti.borrow.db.model.BorrowModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BorrowedListViewModel extends AndroidViewModel {

    private LiveData<List<BorrowModel>> itemAndPersonList;
    private MutableLiveData<List<BorrowModel>> itemAndPersonListMutable = new MutableLiveData<>();
    private static final MutableLiveData ABSENT = new MutableLiveData();
    private AppDatabase appDatabase;

    private ExecutorService executorService;

    {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    public BorrowedListViewModel(Application application) {
        super(application);
        executorService = Executors.newSingleThreadExecutor();

    }

    public void sortData() {
        if(itemAndPersonList.getValue() != null) {
            Collections.sort(itemAndPersonList.getValue(), new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    BorrowModel p1 = (BorrowModel) o1;
                    BorrowModel p2 = (BorrowModel) o2;
                    return p1.getPersonName().compareToIgnoreCase(p2.getPersonName());
                }
            });
        }
    }

    public LiveData<List<BorrowModel>> getProducts() {
        if(itemAndPersonList == null || itemAndPersonList.getValue() == null) {
            loadProducts();
        }
        return itemAndPersonList;
    }

    private void loadProducts() {
        final DatabaseCreator databaseCreator = DatabaseCreator.getInstance(this.getApplication());

        final LiveData<Boolean> databaseCreated = databaseCreator.isDatabaseCreated();

        itemAndPersonList = Transformations.switchMap(databaseCreated,
                new Function<Boolean, LiveData<List<BorrowModel>>>() {

                    @Override public LiveData<List<BorrowModel>> apply(Boolean isDbCreated) {
                        if (!isDbCreated) { // Not needed here, but watch out for null
                            //noinspection unchecked
                            return ABSENT;
                        } else {
                            //noinspection ConstantConditions
                            appDatabase = databaseCreator.getDatabase();
                            return appDatabase.getBorrowDao().getAllBorrowedItems();
                        }
                    }
                });

        itemAndPersonListMutable.setValue(itemAndPersonList.getValue());

        databaseCreator.createDb(this.getApplication());
    }


    public void deleteItem(BorrowModel post) {
        executorService.execute(() -> appDatabase.getBorrowDao().deleteBorrow(post));
    }


}
