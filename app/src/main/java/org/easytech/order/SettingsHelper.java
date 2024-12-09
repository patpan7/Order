package org.easytech.order;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsHelper {

    private static final String PREFS_NAME = "AppPreferences";
    private static final String SERVER_KEY = "server_address";

    private SharedPreferences sharedPreferences;

    public SettingsHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getServerAddress() {
        return sharedPreferences.getString(SERVER_KEY, ""); // Επιστρέφει την προεπιλεγμένη τιμή αν δεν υπάρχει
    }

    public int getCols(String table) {
        return sharedPreferences.getInt(table, 3); // Προεπιλεγμένος αριθμός στηλών (3)
    }
}
