package de.androidcrypto.nfcwificonnectown;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    //Button readNfc, readNfcSsaurel;
    com.google.android.material.textfield.TextInputLayout inputField1Decoration, inputField2Decoration, inputField3Decoration, inputField4Decoration;
    com.google.android.material.textfield.TextInputEditText typeDescription, inputField1, inputField2, inputField3, inputField4, resultNfcWriting;
    SwitchMaterial addTimestampToData;
    AutoCompleteTextView autoCompleteTextView;

    Intent readNfcIntent;
    private NfcAdapter mNfcAdapter;
    boolean writeSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //readNfc = findViewById(R.id.btnMainReadNfcNdefTag);
        //readNfcSsaurel = findViewById(R.id.btnMainSsaurelReadNfcNdefTag);
        //readNfcIntent = new Intent(MainActivity.this, ReadNdefActivity.class);

        typeDescription = findViewById(R.id.etMainTypeDescription);
        inputField1 = findViewById(R.id.etMainInputline1);
        inputField1Decoration = findViewById(R.id.etMainInputline1Decoration);
        inputField2 = findViewById(R.id.etMainInputline2);
        inputField2Decoration = findViewById(R.id.etMainInputline2Decoration);
        inputField3 = findViewById(R.id.etMainInputline3);
        inputField3Decoration = findViewById(R.id.etMainInputline3Decoration);
        inputField4 = findViewById(R.id.etMainInputline4);
        inputField4Decoration = findViewById(R.id.etMainInputline4Decoration);
        resultNfcWriting = findViewById(R.id.etMainResult);
        addTimestampToData = findViewById(R.id.swMainAddTimestampSwitch);

        initUi();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    private void initUi() {
        inputField1Decoration.setHint("Wifi key (OPEN, WPA, WPA2");
        inputField1.setText("WPA2");
        inputField2Decoration.setHint("Encryption (NONE, WEP, TKIP, AES, AES/TKIP)");
        inputField2.setText("AES/TKIP");
        inputField3Decoration.setHint("SSID");
        inputField3.setText("Kiddy_Net");
        inputField4Decoration.setHint("Password");
        inputField4.setText("Leon_2018");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            Bundle options = new Bundle();
            // Work around for some broken Nfc firmware implementations that poll the card too fast
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

            // Enable ReaderMode for all types of card and disable platform sounds
            // the option NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK is NOT set
            // to get the data of the tag afer reading
            mNfcAdapter.enableReaderMode(this,
                    this,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE |
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                    options);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableReaderMode(this);
    }

    // This method is run in another thread when a card is discovered
    // !!!! This method cannot cannot direct interact with the UI Thread
    // Use `runOnUiThread` method to change the UI from this method
    @Override
    public void onTagDiscovered(Tag tag) {
        // Read and or write to Tag here to the appropriate Tag Technology type class
        // in this example the card should be an Ndef Technology Type

        // Make a Sound
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
        } else {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        }

        WifiAuthType wifiAuthType;
        wifiAuthType = WifiAuthType.WPA2_PSK;
        WifiNetwork wifiNetwork = new WifiNetwork(inputField3.getText().toString(),
                wifiAuthType, inputField4.getText().toString(), false);
        writeSuccess = NfcUtils.writeTag(wifiNetwork, tag);
        runOnUiThread(() -> {
            if (writeSuccess) {
                resultNfcWriting.setText("NFC tag was written successful");

            } else {
                resultNfcWriting.setText("*** ERROR *** on writing the tag");
            }
        });


 /*
        Ndef mNdef = Ndef.get(tag);

        // Check that it is an Ndef capable card
        if (mNdef != null) {
            NdefMessage ndefMessage = null;
            NdefRecord ndefRecord1;
            // nfc ndef writing depends on the type
            String choiceString = autoCompleteTextView.getText().toString();
            String inputData1 = inputField1.getText().toString();
            boolean addTimestamp = addTimestampToData.isChecked();



            // the tag is written here
            try {
                mNdef.connect();
                mNdef.writeNdefMessage(ndefMessage);
                // Success if got to here
                runOnUiThread(() -> {
                    resultNfcWriting.setText("write to NFC success");
                    Toast.makeText(getApplicationContext(),
                            "write to NFC success",
                            Toast.LENGTH_SHORT).show();
                });
            } catch (FormatException e) {
                runOnUiThread(() -> {
                    resultNfcWriting.setText("failure FormatException: " + e);
                    Toast.makeText(getApplicationContext(),
                            "FormatException: " + e,
                            Toast.LENGTH_SHORT).show();
                });
                // if the NDEF Message to write is malformed
            } catch (TagLostException e) {
                runOnUiThread(() -> {
                    resultNfcWriting.setText("failure TagLostException: " + e);
                    Toast.makeText(getApplicationContext(),
                            "TagLostException: " + e,
                            Toast.LENGTH_SHORT).show();
                });
                // Tag went out of range before operations were complete
            } catch (IOException e) {
                // if there is an I/O failure, or the operation is cancelled
                runOnUiThread(() -> {
                    resultNfcWriting.setText("failure IOException: " + e);
                    Toast.makeText(getApplicationContext(),
                            "IOException: " + e,
                            Toast.LENGTH_SHORT).show();
                });
            } finally {
                // Be nice and try and close the tag to
                // Disable I/O operations to the tag from this TagTechnology object, and release resources.
                try {
                    mNdef.close();
                } catch (IOException e) {
                    // if there is an I/O failure, or the operation is cancelled
                    runOnUiThread(() -> {
                        resultNfcWriting.setText("failure IOException: " + e);
                        Toast.makeText(getApplicationContext(),
                                "IOException: " + e,
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }

            // Make a Sound
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
            } else {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
            }
        } else {
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(),
                        "mNdef is null",
                        Toast.LENGTH_SHORT).show();
            });
        }*/
    }

}