package com.cool.nfckiosk.ui.nfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.cool.nfckiosk.R;
import com.cool.nfckiosk.ui.MainActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NfcDialogFragment extends DialogFragment {

    public static final String NFC_REQUEST = "com.cool.nfckiosk.nfc_request";
    public static final String NFC_RESULT_TEXT = "com.cool.nfckiosk_nfc_result";
    public static final String NFC_RESULT_WRITE_SUCCESS = "com.cool.nfckiosk_nfc_success";

    private String textToWrite;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nfc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            textToWrite = NfcDialogFragmentArgs.fromBundle(getArguments()).getTextToWrite();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireContext().registerReceiver(nfcReceiver, new IntentFilter(MainActivity.ACTION_NFC_DETECTED));
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(nfcReceiver);
    }

    private boolean writeToNfc(Ndef ndef, String message) {

        if (ndef != null) {
            try {
                ndef.connect();
                NdefRecord mimeRecord = NdefRecord.createMime(
                        "text/plain", message.getBytes(StandardCharsets.US_ASCII));
                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
                ndef.close();
                return true;
            } catch (IOException | FormatException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String readFromNfc(Ndef ndef) {

        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            if (ndefMessage == null) {
                return null;
            }
            String message = new String(ndefMessage.getRecords()[0].getPayload());
            ndef.close();
            return message;
        } catch (IOException | FormatException e) {
            e.printStackTrace();
        }
        return null;
    }


    private final BroadcastReceiver nfcReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (tag != null) {
                Ndef ndef = Ndef.get(tag);
                Bundle bundle = new Bundle();

                if (textToWrite != null) {
                    boolean success = writeToNfc(ndef, textToWrite);
                    bundle.putBoolean(NFC_RESULT_WRITE_SUCCESS, success);
                } else {
                    String text = readFromNfc(ndef);
                    bundle.putString(NFC_RESULT_TEXT, text);
                }
                getParentFragmentManager().setFragmentResult(NFC_REQUEST, bundle);
                NavHostFragment.findNavController(NfcDialogFragment.this).popBackStack();
            }
        }
    };

}









