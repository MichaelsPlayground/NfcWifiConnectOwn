package de.androidcrypto.nfcwificonnectown;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    com.google.android.material.textfield.TextInputLayout inputField3Decoration, inputField4Decoration;
    com.google.android.material.textfield.TextInputEditText typeDescription, inputFieldSsid, inputFieldPassword, resultNfcWriting;
    AutoCompleteTextView autoCompleteTextView;

    private NfcAdapter mNfcAdapter;
    boolean writeSuccess = false;

    WifiAuthType wifiAuthType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        typeDescription = findViewById(R.id.etMainTypeDescription);
        inputFieldSsid = findViewById(R.id.etMainInputlineSsid);
        inputField3Decoration = findViewById(R.id.etMainInputline3Decoration);
        inputFieldPassword = findViewById(R.id.etMainInputlinePassword);
        inputField4Decoration = findViewById(R.id.etMainInputline4Decoration);
        resultNfcWriting = findViewById(R.id.etMainResult);

        String[] type = new String[]{
                "OPEN", "WEP", "WPA PSK", "WPA2 PSK"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.drop_down_item,
                type);

        autoCompleteTextView = findViewById(R.id.auth_type);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String choiceString = autoCompleteTextView.getText().toString();
                switch (choiceString) {
                    case "OPEN": {
                        wifiAuthType = WifiAuthType.OPEN;
                        break;
                    }
                    case "WEP": {
                        wifiAuthType = WifiAuthType.WEP;
                        break;
                    }
                    case "WPA PSK": {
                        wifiAuthType = WifiAuthType.WPA_PSK;
                        break;
                    }
                    case "WPA2 PSK": {
                        wifiAuthType = WifiAuthType.WPA2_PSK;
                        break;
                    }
                    default: {
                        wifiAuthType = null;
                        break;
                    }
                }
            }
        });

        initUi();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    private void initUi() {
        typeDescription.setText("This app writes a tag with connection parameters to a WIFI network.");
        inputField3Decoration.setHint("SSID");
        inputFieldSsid.setText("WifiGuest");
        inputField4Decoration.setHint("Password");
        inputFieldPassword.setText("87654321");
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

        // simple sanity checks
        if (wifiAuthType == null) {
            runOnUiThread(() -> {
                resultNfcWriting.setText("Choose an auth type");
            });
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 120);
            toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK);
            return;
        }
        String ssid = inputFieldSsid.getText().toString();
        String password = inputFieldPassword.getText().toString();
        if (ssid.isEmpty()) {
            runOnUiThread(() -> {
                resultNfcWriting.setText("enter a SSID");
            });
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 120);
            toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK);
            return;
        }
        if (password.isEmpty()) {
            runOnUiThread(() -> {
                resultNfcWriting.setText("enter a password");
            });
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 120);
            toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK);
            return;
        }

        // Make a Sound
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
        } else {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        }

        WifiNetwork wifiNetwork = new WifiNetwork(inputFieldSsid.getText().toString(),
                wifiAuthType, inputFieldPassword.getText().toString(), false);
        writeSuccess = NfcUtils.writeTag(wifiNetwork, tag);
        runOnUiThread(() -> {
            if (writeSuccess) {
                resultNfcWriting.setText("NFC tag was written successful");

            } else {
                resultNfcWriting.setText("*** ERROR *** on writing the tag");
            }
        });
    }

}