package org.easytech.order;

import android.util.Log;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;

import java.util.List;

public class EscPosPrinterHelper {

    private String printerIpAddress;
    private int printerPort;
    private EscPosPrinter printer;

    // Callback interface για την εκτύπωση
    public interface PrintCallback {
        void onSuccess();
        void onError(Exception e);
    }

    // Κατασκευαστής: Αποθηκεύει τα στοιχεία του εκτυπωτή
    public EscPosPrinterHelper(String printerIpAddress, int printerPort) {
        this.printerIpAddress = printerIpAddress;
        this.printerPort = printerPort;
    }

    // Ασύγχρονη μέθοδος για τη σύνδεση με τον εκτυπωτή
    public void connectPrinterAsync(Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            try {
                TcpConnection connection = new TcpConnection(printerIpAddress, printerPort);
                connection.connect();
                printer = new EscPosPrinter(connection, 203, 48, 32,); // 203 DPI, 48 χαρακτήρες ανά γραμμή
                onSuccess.run();
            } catch (Exception e) {
                e.printStackTrace();
                onFailure.run();
            }
        }).start();
    }

    // Ασύγχρονη μέθοδος για την εκτύπωση παραγγελίας
    public void printOrderAsync(int orderId, List<Product> cartItems, PrintCallback callback) {
        connectPrinterAsync(() -> {
            try {
                // Δημιουργία του κειμένου της απόδειξης
                StringBuilder receipt = new StringBuilder();
                receipt.append("Παραγγελία #").append(orderId).append("\n");
                receipt.append("-------------------------------\n");
                Log.e("cartitems", cartItems.size()+"");
                for (Product product : cartItems) {
                    receipt.append(product.getProd_name())
                            .append(" x")
                            .append(product.getQuantity())
                            .append(" - ")
                            .append(product.getProd_price())
                            .append("€\n");
                }

                receipt.append("-------------------------------\n");

                // Υπολογισμός συνολικού ποσού
                double total = 0;
                for (Product product : cartItems) {
                    total += product.getProd_price() * product.getQuantity();
                }

                receipt.append("Σύνολο: ").append(total).append("€\n\n\n\n\n");

                // Εκτύπωση και κόψιμο χαρτιού
                printer.printFormattedTextAndCut(receipt.toString());
                Log.d("EscPosPrinterHelper", "Εκτύπωση επιτυχής:\n" + receipt);
                callback.onSuccess();
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError(e);
            }
        }, () -> callback.onError(new Exception("Αποτυχία σύνδεσης με τον εκτυπωτή")));
    }
}
