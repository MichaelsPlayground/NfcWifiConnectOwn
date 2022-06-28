# NFC Wifi Connect own

The main functionality is taken from:

https://github.com/bparmentier/WiFiKeyShare

by Bruno Parmentier

The app is running on SDK 21 - 32 and is tested on Android devices running Android 5.01, 9 and 12.

WLAN test-tag:
```plaintext
I/System.out: nr of records: 2
I/System.out: 
I/System.out: rec: 0 TNF: 2 (2 TNF_MIME_MEDIA)
I/System.out: rec 0 inf: 2 type: 6170706c69636174696f6e2f766e642e7766612e777363 payload: 100e0020104500094b696464795f4e6574100300020020102700094c656f6e5f32303138 
I/System.out: �� E��	Kiddy_Net���� '��	Leon_2018 
I/System.out: 
I/System.out: rec: 1 TNF: 4 (4 TNF_EXTERNAL_TYPE)
I/System.out: rec 1 inf: 4 type: 616e64726f69642e636f6d3a706b67 payload: 64652e616e64726f696463727970746f2e6e666377696669636f6e6e6563746f776e 
I/System.out: de.androidcrypto.nfcwificonnectown 
I/System.out: TNF External type XX payload
I/System.out: de.androidcrypto.nfcwificonnectown 
I/System.out: TNF External type XX type
I/System.out: android.com:pkg 
```

WLAN-Test
```plaintext
Schlüssel WPA2 Personal, AES/TKIP Verschlüsselung, SSID T_Wpa2_Aestkip, Passwort 12345678
I/System.out: nr of records: 1
I/System.out: 
I/System.out: rec: 0 TNF: 2 (2 TNF_MIME_MEDIA)
I/System.out: rec 0 inf: 2 type: 6170706c69636174696f6e2f766e642e7766612e777363 payload: 100e003910260001011045000e545f577061325f416573746b6970100300020020100f0002000c10270008313233343536373810200006ffffffffffff 
I/System.out: ��9&��E��T_Wpa2_Aestkip���� ����'�12345678 �������� 
I/System.out: TNF Mime Media XX payload
I/System.out: ��9&��E��T_Wpa2_Aestkip���� ����'�12345678 �������� 
I/System.out: TNF Mime Media XX type
I/System.out: application/vnd.wfa.wsc 

```

The app icon is generated with help from **Launcher icon generator** 
(https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html), 
(options trim image and resize to 110%, color #2196F3).