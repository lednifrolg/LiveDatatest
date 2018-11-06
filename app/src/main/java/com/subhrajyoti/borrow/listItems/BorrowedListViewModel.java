package com.subhrajyoti.borrow.listItems;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.subhrajyoti.borrow.db.AppDatabase;
import com.subhrajyoti.borrow.db.DatabaseCreator;
import com.subhrajyoti.borrow.db.model.BorrowModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BorrowedListViewModel extends AndroidViewModel {

    private LiveData<List<BorrowModel>> itemAndPersonList;
    private MutableLiveData<List<BorrowModel>> itemAndPersonListMutable = new MutableLiveData<>();
    private MutableLiveData<Boolean> sortData = new MutableLiveData<>();
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

        Transformations.map(itemAndPersonList, new Function<List<BorrowModel>, Object>() {
            @Override
            public Object apply(List<BorrowModel> input) {
                itemAndPersonListMutable.setValue(input);
                return null;
            }
        });
    }

    public void sortData() {
        List<BorrowModel> list = itemAndPersonListMutable.getValue();
        if (list != null) {
            Collections.sort(list, (Comparator) (o1, o2) -> {
                BorrowModel p1 = (BorrowModel) o1;
                BorrowModel p2 = (BorrowModel) o2;
                return p1.getPersonName().compareToIgnoreCase(p2.getPersonName());
            });

            itemAndPersonListMutable.setValue(list);
        }
    }

    public LiveData<List<BorrowModel>> getProducts() {
        if (itemAndPersonListMutable == null || itemAndPersonListMutable.getValue() == null) {
            loadProducts();
        }
        return itemAndPersonListMutable;
    }

    private void loadProducts() {
        final DatabaseCreator databaseCreator = DatabaseCreator.getInstance(this.getApplication());

        final LiveData<Boolean> databaseCreated = databaseCreator.isDatabaseCreated();

        itemAndPersonList = Transformations.switchMap(databaseCreated,
                new Function<Boolean, LiveData<List<BorrowModel>>>() {

                    @Override
                    public LiveData<List<BorrowModel>> apply(Boolean isDbCreated) {
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

        databaseCreator.createDb(this.getApplication());
    }


    public void deleteItem(BorrowModel post) {
        executorService.execute(() -> appDatabase.getBorrowDao().deleteBorrow(post));
    }


}
