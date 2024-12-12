package org.easytech.order;

import android.util.Log;

import com.dantsu.escposprinter.EscPosCharsetEncoding;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;

import java.util.List;

public class EscPosPrinterHelper {

    private String printerIpAddress = "192.168.1.97";
    private int printerPort = 9100;
    private EscPosPrinter printer;
    private TcpConnection tcpConnection;

    // Callback interface για την εκτύπωση
    public interface PrintCallback {
        void onSuccess();
        void onError(Exception e);
    }

    // Κατασκευαστής: Αποθηκεύει τα στοιχεία του εκτυπωτή
    public EscPosPrinterHelper() {
    }

    // Ασύγχρονη μέθοδος για τη σύνδεση με τον εκτυπωτή
    public void connectPrinterAsync(Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            try {
                // Έλεγχος αν υπάρχει ανοιχτή σύνδεση
                if (tcpConnection != null) {
                    tcpConnection.disconnect();
                }

                tcpConnection = new TcpConnection(printerIpAddress, printerPort,3600);
                tcpConnection.connect();
                printer = new EscPosPrinter(tcpConnection, 203, 72, 48, new EscPosCharsetEncoding("Cp737", 64)); // 203 DPI, 48 χαρακτήρες ανά γραμμή
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
                receipt.append("[C]<font size='big'><b>Παραγγελία #").append(orderId).append("</b></font>\n");
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

                receipt.append("Σύνολο: ").append(total).append("€\n\n\n\n\n.");
                // Εκτύπωση και κόψιμο χαρτιού
                printer.printFormattedTextAndCut(receipt.toString());
                Log.d("EscPosPrinterHelper", "Εκτύπωση επιτυχής:\n" + receipt);
                // Αποσύνδεση
                disconnectPrinter();
                callback.onSuccess();
            } catch (Exception e) {
                e.printStackTrace();
                // Αποσύνδεση
                disconnectPrinter();
                callback.onError(e);
            }
        }, () -> callback.onError(new Exception("Αποτυχία σύνδεσης με τον εκτυπωτή")));
    }

    // Μέθοδος για αποσύνδεση
    private void disconnectPrinter() {
        try {
            if (tcpConnection != null) {
                tcpConnection.disconnect();
                tcpConnection = null;
            }
        } catch (Exception e) {
            Log.e("EscPosPrinterHelper", "Σφάλμα κατά την αποσύνδεση: " + e.getMessage());
        }
    }
}
