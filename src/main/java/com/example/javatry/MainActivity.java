package com.example.javatry;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    public static final  String Error_Detected="No NFC Tag Detected";
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter writingTagFilters;
    private Tag myTag;
    private Context context;
    private User user;
    private int nfcText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeNewUser();

        context = this;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null) {
            Toast.makeText(this,"This device does not support NFC",Toast.LENGTH_LONG).show();
            finish();
        }
        try {
            readFromIntent(getIntent());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        pendingIntent=PendingIntent.getActivity(this,0,new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
        IntentFilter tagDetected= new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
    }

    @SuppressLint("SetTextI18n")
    private void initializeNewUser() {
        user = User.getInstance();

    }

    private void readFromIntent(Intent intent) throws UnsupportedEncodingException {
        String action= intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
            || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
            || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Parcelable[] rawMsgs=intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs=null;
            if (rawMsgs!=null)
            {
                msgs= new NdefMessage[rawMsgs.length];
                for (int i=0; i< rawMsgs.length;i++)
                {
                    msgs[i]=(NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }

    @SuppressLint("SetTextI18n")
    private void buildTagViews(NdefMessage[] msgs) throws UnsupportedEncodingException {
        if (msgs==null || msgs.length==0) return;
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding=((payload[0] & 128)==0) ? "UTF-8" : "UTF-16";
        int languageCodeLength=payload[0] & 0063;// en
        this.nfcText = Integer.parseInt(new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding));
        calculateBank(nfcText);


    }



    private void calculateBank(int operation) {
        String txt=user.endgame();
        if (txt!=null) checkmate();
        String text1="";
        String text2="";
        if (user.getCanbye()!=3) {
            if (operation == 1) {
                user.addToBank(500);
            } else if (operation == 2 || operation == 6 || operation == 8 || operation == 14) {
                user.addToBank(100);
            } else if (operation == 3) {
                user.addToBank(150);
            } else if (operation == 4) {
                user.addToBank(-100);
            } else if (operation == 5) {
                user.addToBank(-150);
            } else if (operation == 7) {
                user.addToBank(75);
            } else if (operation == 9) {
                user.addToBank(-50);
            } else if (operation == 11) {
                user.addToBank(+60);
            } else if (operation == 12) {
                user.addToBank(-80);
            } else if (operation == 13) {
                user.addToBank(50);
            } else if (operation == 15) {
                user.addToBank(-200);
            } else if (operation > 15 && operation <= 33) {
                if (user.isOwner(operation) == 0) {
                    if (user.getCanbye() == 0) {
                        text1 = "Бажаєте придбати?";

                        text2 = "Купівля " + user.namefind(nfcText) + " за " + user.pricefind(nfcText) + " $ ";
                        confirmationAlert(text1, text2, operation);
                    }
                } else if (user.isOwner(operation) == 1) {
                    if (user.getCanbye() < 3) {
                        text1 = "Бажаєте закласти?";

                        text2 = "Продажа " + user.namefind(nfcText) + " за " + Math.round(user.pricefind(nfcText) * 0.9) + " $ ";
                        confirmationAlert(text1, text2, operation);
                    }
                } else {
                    if (user.getCanbye() == 0) {
                        text1 = "Бажаєте викупити?";

                        text2 = "Викуп " + user.namefind(nfcText) + " за " + Math.round(user.pricefind(nfcText) * 1.1) + " $ ";
                        confirmationAlert(text1, text2, operation);
                    }
                }
                updateTheBalanceText();
                updateIcon();
            } else if (operation == 100) {
                user.addToBank(-500);
            }
        }
        updateTheBalanceText();
        updateIcon();


    }


    private void checkmate()
    {
        String text=user.endgame();
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        if (user.getCanbye()==3)
        {
            TextView textView=findViewById(R.id.textView3);
            textView.setText(text);
        }
    }

    private void confirmationAlert(String h1, String h2,int oper) {


        new AlertDialog.Builder(this)
                .setTitle(h1)
                .setIcon(R.drawable.house)
                .setMessage(h2)
                .setPositiveButton("Так", (dialog, id) -> {
                    user.cnangeOnw(oper);
                    Toast.makeText(this, "Операція успішна", Toast.LENGTH_SHORT).show();
                    updateTheBalanceText();
                    updateIcon();
                    dialog.cancel();
                })
                .setNegativeButton("Ні", (dialog, id) ->
                {
                    user.rateprice(oper);
                    Toast.makeText(this, user.rateText(oper), Toast.LENGTH_SHORT).show();
                    updateTheBalanceText();
                    updateIcon();
                    dialog.cancel();
                })
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void updateTheBalanceText() {
        TextView txt = findViewById(R.id.bank_text);
        txt.setText(user.getName() + "! Ваш баланс \nДорівнює:" + user.getBank() + "$");
    }
    private void updateIcon()
    {
        String icont="";
        byte[] arr=user.getActivesArray();
        for (int i=1;i<=18;i+=1)
        {
            icont="imageView"+i;
            int id = this.getResources().getIdentifier(icont, "id", getPackageName());
            ImageView imageView = findViewById(id);
            if (arr[i-1]==1)
            {
                imageView.setVisibility(View.VISIBLE);
                imageView.setBackgroundColor(0xFF00FF00);
            }
            else if (arr[i-1]==2)
            {
                imageView.setVisibility(View.VISIBLE);
                imageView.setBackgroundColor(0xFFFF0000);
            }
            else imageView.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            readFromIntent(intent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }
    public void onMyButtonClick2(View view)
    {
        TextView textView = findViewById(R.id.editTextTextPersonName2);
        String txt=textView.getText().toString();
        if (txt.length() == 0) {
            Toast.makeText(this, "You need to enter username", Toast.LENGTH_SHORT).show();
            return;
        }
        user.setName(txt);
        textView.setVisibility(View.GONE);
        Button btn=findViewById(R.id.button1);
        btn.setVisibility(View.GONE);
        TextView textile=findViewById(R.id.textView3);
        textile.setVisibility(View.VISIBLE);
        ImageView img=findViewById(R.id.imageView0);
        img.setVisibility(View.VISIBLE);
        updateTheBalanceText();
    }


    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
}

