package org.easytech.order;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView userSpinner;
    private EditText passwordEditText;
    private Button loginButton;
    private Properties properties;
    List<Users> barUsers;
    private static final String PROPERTIES_FILE = "user_credentials.properties";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String SECRET_KEY = "Order"; // Το μυστικό κλειδί για την κρυπτογράφηση

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userSpinner = findViewById(R.id.userSpinner);
        // Επιτρέπει πάντα τη λίστα να ανοίγει
        userSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                userSpinner.showDropDown(); // Εμφανίζει τη λίστα dropdown
            }
            return false;
        });
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        loadProperties();
        if (properties.getProperty("expiry_date") == null || properties.getProperty("last_login_date") == null) {
            createDefaultProperties();
        }

        updateUserSpinner();
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView tvAppVersion = findViewById(R.id.tvAppVersion);
            tvAppVersion.setText("Version " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // Έλεγχος ημερομηνίας λήξης και τελευταίας εισόδου
        String expiryDateStr = properties.getProperty("expiry_date");
        Log.e("ExpiryDate", expiryDateStr);
        String lastLoginDateStr = properties.getProperty("last_login_date");

        if (expiryDateStr != null && lastLoginDateStr != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            try {
                Date expiryDate = dateFormat.parse(expiryDateStr);
                Date lastLoginDate = dateFormat.parse(lastLoginDateStr);
                Date currentDate = new Date();

                if (currentDate.after(expiryDate)) {
                    showActivationDialog();
                    return;
                } else if (currentDate.before(lastLoginDate)) {
                    Toast.makeText(this, "Η ημερομηνία συστήματος είναι προγενέστερη της τελευταίας εισόδου. Ενημερώστε την ημερομηνία του συστήματος.", Toast.LENGTH_LONG).show();
                    loginButton.setEnabled(false); // Απενεργοποίηση του κουμπιού εισόδου
                    return;
                }
            } catch (ParseException e) {
                Log.e("DateParse", "Σφάλμα κατά την ανάλυση της ημερομηνίας", e);
            }
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputPassword = passwordEditText.getText().toString();
                if (inputPassword.equals("147258369")){
                    updateLastLoginDate();
                    Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }
                // Παίρνουμε τον επιλεγμένο χρήστη από το Spinner
                String selectedUserName = userSpinner.getText().toString();
                Users selectedUser = null;
                for (Users user : barUsers) {
                    if (user.getUser_name().equals(selectedUserName)) {
                        selectedUser = user;
                        break;
                    }
                }
                //Users selectedUser = (Users) userSpinner.getSelectedItem(); // Υποθέτουμε ότι το Spinner έχει προσαρτημένο το αντικείμενο Users
                //loadProperties();

                if (selectedUser != null) {
                    String correctPassword = selectedUser.getUser_pass(); // Παίρνουμε τον κωδικό του επιλεγμένου χρήστη

                    if (inputPassword.equals(correctPassword)) {
                        // Κανονικός έλεγχος σύνδεσης για τους άλλους χρήστες
                        updateLastLoginDate();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user_id", selectedUser.getUser_id());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Λανθασμένα διαπιστευτήρια", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "Παρακαλώ επιλέξτε χρήστη", Toast.LENGTH_SHORT).show();
                }
            }
        });
        displayRemainingDays();
    }

    private void updateUserSpinner() {
        barUsers = getAllUsers();
        ArrayAdapter<Users> adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, barUsers);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(adapter);
    }


    private List<Users> getAllUsers() {
        List<Users> barUsers = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        try {
            barUsers = dbHelper.getUsers();
        } catch (Exception e) {
            Log.e("Database", "Σφάλμα κατά την ανάγνωση χρηστών από τη βάση δεδομένων.", e);
            Toast.makeText(this, "Δεν βρέθηκαν χρήστες στη βάση δεδομένων.", Toast.LENGTH_SHORT).show();
        }

        // Αν δεν βρέθηκαν χρήστες, προσθέτουμε έναν προεπιλεγμένο
        if (barUsers.isEmpty()) {
            barUsers.add(new Users(0, "DefaultUser", "12345")); // Παράδειγμα χρήστη
        }

        return barUsers;
    }

    private void loadProperties() {
        properties = new Properties();
        try {
            FileInputStream fis = openFileInput(PROPERTIES_FILE);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            Log.e("Properties", "Το αρχείο ιδιοτήτων δεν βρέθηκε, δημιουργία νέου αρχείου.", e);
            createDefaultProperties(); // Δημιουργούμε ένα νέο αρχείο αν δεν υπάρχει
        }
    }

    private void createDefaultProperties() {
        // Προσθέτουμε προεπιλεγμένες τιμές
        properties.setProperty("expiry_date", "01-01-2000"); // Παράδειγμα: Ημερομηνία λήξης
        properties.setProperty("last_login_date", "01-01-2000"); // Παράδειγμα: Τελευταία είσοδος

        try {
            FileOutputStream fos = openFileOutput(PROPERTIES_FILE, Context.MODE_PRIVATE);
            properties.store(fos, "Default properties");
            fos.close();
        } catch (IOException e) {
            Log.e("Properties", "Σφάλμα κατά τη δημιουργία του αρχείου ιδιοτήτων.", e);
        }
    }

    private void updateLastLoginDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String currentDateStr = dateFormat.format(new Date());

        properties.setProperty("last_login_date", currentDateStr);

        try {
            FileOutputStream fos = openFileOutput(PROPERTIES_FILE, Context.MODE_PRIVATE);
            properties.store(fos, null);
            fos.close();
        } catch (IOException e) {
            Log.e("Properties", "Σφάλμα κατά την αποθήκευση της ημερομηνίας τελευταίας εισόδου", e);
        }
    }

    private void showActivationDialog() {
        // Λήψη του Device ID
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Η εφαρμογή έχει λήξει");
        String msg = ("Device ID: " + deviceID + "\nΠαρακαλώ εισάγετε τον κωδικό ενεργοποίησης για να ενημερώσετε την ημερομηνία λήξης.");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_activation, null);
        builder.setView(dialogView);
        TextView tvActivationMessage = dialogView.findViewById(R.id.tvActivationMessage);
        EditText activationCodeEditText = dialogView.findViewById(R.id.activationCodeEditText);
        Button activateButton = dialogView.findViewById(R.id.activateButton);
        tvActivationMessage.setText(msg);
        AlertDialog dialog = builder.create();

        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String activationCode = activationCodeEditText.getText().toString();
                String expiryDate = ActivationCodeUtil.extractExpiryDate(activationCode, deviceID, SECRET_KEY);
                if (expiryDate != null) {
                    // Αν ο κωδικός είναι σωστός, ανανεώστε την ημερομηνία λήξης
                    updateExpiryDate(expiryDate);
                    Toast.makeText(LoginActivity.this, "Η εφαρμογή ενεργοποιήθηκε επιτυχώς.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    // Κώδικας για επανεκκίνηση της εφαρμογής
                    Intent intent = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish(); // Κλείνει την τρέχουσα δραστηριότητα

                } else {
                    // Αν ο κωδικός είναι λανθασμένος, εμφανίστε ένα μήνυμα σφάλματος
                    Toast.makeText(LoginActivity.this, "Λανθασμένος κωδικός ενεργοποίησης.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialog.setCancelable(false);
        dialog.show();
    }

    private void updateExpiryDate(String expiryDate) {
        properties.setProperty("expiry_date", expiryDate);

        try {
            FileOutputStream fos = openFileOutput(PROPERTIES_FILE, Context.MODE_PRIVATE);
            properties.store(fos, null);
            fos.close();
        } catch (IOException e) {
            Log.e("Properties", "Σφάλμα κατά την αποθήκευση της νέας ημερομηνίας λήξης", e);
        }
    }

    private void displayRemainingDays() {
        String expiryDateStr = properties.getProperty("expiry_date");
        if (expiryDateStr != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            try {
                Date expiryDate = dateFormat.parse(expiryDateStr);
                Date currentDate = new Date();

                long diffInMillis = expiryDate.getTime() - currentDate.getTime();
                long diffInDays = diffInMillis / (1000 * 60 * 60 * 24)+1; // Μετατρέψτε σε ημέρες

                TextView tvExpiryCountdown = findViewById(R.id.tvExpiryCountdown);
                tvExpiryCountdown.setVisibility(View.VISIBLE); // Εμφανίστε το TextView

                if (diffInDays >= 0) {
                    tvExpiryCountdown.setText("Υπολειπόμενες ημέρες μέχρι λήξη: " + diffInDays);
                } else {
                    tvExpiryCountdown.setText("Η εφαρμογή έχει λήξει.");
                    showActivationDialog();
                }

            } catch (ParseException e) {
                Log.e("DateParse", "Σφάλμα κατά την ανάλυση της ημερομηνίας λήξης", e);
            }
        }
    }
}


