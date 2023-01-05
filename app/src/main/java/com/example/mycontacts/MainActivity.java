package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_CONTACT = 1;

    private static final int Perm_CTC = 1;

    private static final int CALL_Perm = 1;

    private static String uri;

    private String id;
    private String name;
    private String phoneNumber;


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
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, Perm_CTC);
                } else {
                    startActivityForResult(contactPickerIntent, REQUEST_CODE_PICK_CONTACT);
                }
            }
        });

        buttonDetailsContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView textViewid = findViewById(R.id.textViewId);
                textViewid.setText("ID : "+id);
                TextView textViewName = findViewById(R.id.textViewName);
                textViewName.setText("Name : "+name);
                TextView textViewNumber = findViewById(R.id.textViewNumber);
                textViewNumber.setText("Number : "+phoneNumber);
                buttonCall.setEnabled(true);
            }
        });

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri phoneUri = Uri.parse("tel:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(phoneUri);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_Perm);
                } else {
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1 :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    ContentResolver cr = getContentResolver();
                    Cursor cur = cr.query(contactData, null, null, null, null);
                    if (cur.getCount() > 0) {// thats mean some resutl has been found
                        if(cur.moveToNext()) {
                            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                            this.id = id;
                            TextView textView = findViewById(R.id.textview_hint);
                            uri = "« content://contacts/people/"+id+" »";
                            textView.setText(uri);
                            Button buttonDetailsContact = findViewById(R.id.button_details_contact);
                            buttonDetailsContact.setEnabled(true);




                            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            this.name = name;
                            Log.e("Names", name);
                            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                            {
                                // Query phone here. Covered next
                                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
                                while (phones.moveToNext()) {
                                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    Log.e("Number", phoneNumber);
                                    this.phoneNumber = phoneNumber;
                                }
                                phones.close();
                            }



                        }
                    }
                    cur.close();
                }
                break;
        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
//            permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        //check the permission type using the requestCode
//        if (requestCode == Perm_CTC) {
//            //the array is empty if not granted
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "GRANTED CALL", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        TextView textView = findViewById(R.id.textview_hint);
//        Button buttonContactId = findViewById(R.id.button_contact_id);
//        Button buttonDetailsContact = findViewById(R.id.button_details_contact);
//        Button buttonCall = findViewById(R.id.button_call);
//        // Vérifiez que la requête est bien la sélection de contact
//        if (requestCode == REQUEST_CODE_PICK_CONTACT) {
//            // Vérifiez que la sélection a réussi
//            if (resultCode == RESULT_OK) {
//                // Récupérez l'URI du contact sélectionné
//                Uri contactUri = data.getData();
//                // Récupérez le numéro de téléphone du contact à l'aide de l'URI
//                String phoneNumber = getPhoneNumber(contactUri);
//                // Mettez à jour le TextView avec le numéro de téléphone
//                textView.setText(phoneNumber);
//                // Activez les boutons Détails contact et Call
//                buttonDetailsContact.setEnabled(true);
//                buttonCall.setEnabled(true);
//            } else {
//                // L'utilisateur a annulé la sélection de contact
//                textView.setText("Opération annulée");
//            }
//        }
//    }
//
//    private String getPhoneNumber(Uri contactUri) {
//        String phoneNumber = "";
//        // Ouvrez une connection à la base de données de contacts en lecture
//        Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
//        // Bouclez sur tous les numéros de téléphone du contact
//        if (cursor.moveToFirst()) {
//            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//            phoneNumber = cursor.getString(phoneIndex);
//        }
//        cursor.close();
//        return phoneNumber;
//    }


}