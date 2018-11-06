package com.subhrajyoti.borrow.db;

import com.subhrajyoti.borrow.db.model.BorrowModel;

import org.ajbrown.namemachine.Gender;
import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/** Generates dummy data and inserts them into the database */
class DatabaseInitUtil {

    static void initializeDb(AppDatabase db) {
        List<BorrowModel> products = new ArrayList<>();

        generateData(products);

        insertData(db, products);
    }

    private static void generateData(List<BorrowModel> products) {

        NameGenerator generator = new NameGenerator();
        // generate male and female names.
        List<Name> names = generator.generateNames( 10 );

        for (int i = 0; i < names.size(); i++) {
            BorrowModel product = new BorrowModel();
            product.setId(i);
            product.setItemName(names.get(i).getFirstName());
            product.setPersonName(names.get(i).getLastName());
            product.setBorrowDate(new Date());
            products.add(product);
        }
    }

    private static void insertData(AppDatabase db, List<BorrowModel> products) {
        db.beginTransaction();
        try {
            db.getBorrowDao().insertAll(products);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static Date dateRandom(int initialYear, int lastYear) {
        if (initialYear > lastYear) {
            int year = lastYear;
            lastYear = initialYear;
            initialYear = year;
        }

        Calendar cInitialYear = Calendar.getInstance();
        cInitialYear.set(Calendar.YEAR, 2015);
        long offset = cInitialYear.getTimeInMillis();

        Calendar cLastYear = Calendar.getInstance();
        cLastYear.set(Calendar.YEAR, 2016);
        long end = cLastYear.getTimeInMillis();

        long diff = end - offset + 1;
        Timestamp timestamp = new Timestamp(offset + (long) (Math.random() * diff));
        return new Date(timestamp.getTime());
    }
}
