package org.easytech.order;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PrintOrderWorker extends Worker {

    public PrintOrderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Κώδικας για εκτύπωση παραγγελιών
        printPendingOrders();

        // Επαναπρογραμματισμός του worker
        scheduleNextWork();

        return Result.success();
    }

    private void scheduleNextWork() {
        OneTimeWorkRequest nextWorkRequest = new OneTimeWorkRequest.Builder(PrintOrderWorker.class)
                .setInitialDelay(10, TimeUnit.SECONDS) // Καθυστέρηση πριν τον επόμενο έλεγχο
                .build();

        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork("PrintOrderWorker", ExistingWorkPolicy.REPLACE, nextWorkRequest);
    }


    private void printPendingOrders() {
        // Λογική για την εκτύπωση παραγγελιών που δεν έχουν εκτυπωθεί
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        List<Order> pendingOrders = dbHelper.getPendingPrintOrders();

        if (!pendingOrders.isEmpty()) {
            // Κώδικας για εκτύπωση των παραγγελιών
            for (Order order : pendingOrders) {
                printOrder(order.getOrderId());
            }
        }
    }

    private void printOrder(int orderId) {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        EscPosPrinterHelper printerHelper = new EscPosPrinterHelper();
        List<Product> orderItems = dbHelper.getUnprintedOrderItems(orderId);
        printerHelper.printOrderAsync(orderId, orderItems, new EscPosPrinterHelper.PrintCallback() {
            @Override
            public void onSuccess() {
                //Toast.makeText(getApplicationContext(), "Η εκτύπωση ολοκληρώθηκε!", Toast.LENGTH_SHORT).show();
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                dbHelper.updatePrintStatus(orderId, 1); // Ενημέρωση για επιτυχημένη εκτύπωση
            }

            @Override
            public void onError(Exception e) {
                //Toast.makeText(getApplicationContext(), "Σφάλμα στην εκτύπωση: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

