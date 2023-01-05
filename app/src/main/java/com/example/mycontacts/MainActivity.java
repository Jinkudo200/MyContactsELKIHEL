package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_CONTACT = 1;

    private static final int Perm_CTC = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textview_hint);
        Button buttonContactId = findViewById(R.id.button_contact_id);
        Button buttonDetailsContact = findViewById(R.id.button_details_contact);
        Button buttonCall = findViewById(R.id.button_call);


        buttonContactId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts/people"));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, Perm_CTC);
                } else {
                    startActivityForResult(pickContactIntent, REQUEST_CODE_PICK_CONTACT);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //check the permission type using the requestCode
        if (requestCode == Perm_CTC) {
            //the array is empty if not granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "GRANTED CALL", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView textView = findViewById(R.id.textview_hint);
        Button buttonContactId = findViewById(R.id.button_contact_id);
        Button buttonDetailsContact = findViewById(R.id.button_details_contact);
        Button buttonCall = findViewById(R.id.button_call);
        // Vérifiez que la requête est bien la sélection de contact
        if (requestCode == REQUEST_CODE_PICK_CONTACT) {
            // Vérifiez que la sélection a réussi
            if (resultCode == RESULT_OK) {
                // Récupérez l'URI du contact sélectionné
                Uri contactUri = data.getData();
                // Récupérez le numéro de téléphone du contact à l'aide de l'URI
                String phoneNumber = getPhoneNumber(contactUri);
                // Mettez à jour le TextView avec le numéro de téléphone
                textView.setText(phoneNumber);
                // Activez les boutons Détails contact et Call
                buttonDetailsContact.setEnabled(true);
                buttonCall.setEnabled(true);
            } else {
                // L'utilisateur a annulé la sélection de contact
                textView.setText("Opération annulée");
            }
        }
    }

    private String getPhoneNumber(Uri contactUri) {
        String phoneNumber = "";
        // Ouvrez une connection à la base de données de contacts en lecture
        Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
        // Bouclez sur tous les numéros de téléphone du contact
        if (cursor.moveToFirst()) {
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNumber = cursor.getString(phoneIndex);
        }
        cursor.close();
        return phoneNumber;
    }


}