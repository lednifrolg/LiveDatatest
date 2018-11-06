package com.subhrajyoti.borrow.listItems;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

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

    private Observer dbObserver = (Observer<List<BorrowModel>>) borrowModels -> itemAndPersonListMutable.postValue(borrowModels);

    private ExecutorService executorService;

    {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    public BorrowedListViewModel(Application application) {
        super(application);
        DatabaseCreator.getInstance(application).createDb(this.getApplication());

        executorService = Executors.newSingleThreadExecutor();

        itemAndPersonList = Transformations.switchMap(DatabaseCreator.getInstance(application).isDatabaseCreated(), new Function<Boolean, LiveData<List<BorrowModel>>>() {
            @Override
            public LiveData<List<BorrowModel>> apply(Boolean input) {
                if (input) {
                    return DatabaseCreator.getInstance(application).getDatabase().getBorrowDao().getAllBorrowedItems();
                } else {
                    return ABSENT;
                }
            }
        });

        itemAndPersonList.observeForever(dbObserver);
    }

    public void sortData() {
        if(itemAndPersonListMutable.getValue() != null) {
            List<BorrowModel> list = itemAndPersonListMutable.getValue();
            Collections.sort(list, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    BorrowModel p1 = (BorrowModel) o1;
                    BorrowModel p2 = (BorrowModel) o2;
                    return p1.getPersonName().compareToIgnoreCase(p2.getPersonName());
                }
            });

            itemAndPersonListMutable.setValue(list);
        }
    }

    public LiveData<List<BorrowModel>> getProducts() {
        return itemAndPersonListMutable;
    }


    public void deleteItem(BorrowModel post) {
        executorService.execute(() -> appDatabase.getBorrowDao().deleteBorrow(post));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        itemAndPersonList.removeObserver(dbObserver);
    }
}
