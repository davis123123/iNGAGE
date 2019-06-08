package ingage.ingage.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ingage.ingage.R;

/**
 * Created by wuv66 on 6/8/2019.
 */

public class EULADialogFragment extends DialogFragment {

    AlertDialog dialog;

    public static EULADialogFragment newInstance() {

        EULADialogFragment f = new EULADialogFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_eula, container);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_eula, null);

        InputStream inputStream = getResources().openRawResource(R.raw.eula);
        BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));
        String eulaContent = "";
        String eachline = "";
        while (eachline != null) {
            // `the words in the file are separated by space`, so to get each words
            String[] words = eachline.split(" ");
            try {
                eulaContent += eachline;
                eulaContent += "\n";
                eachline = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_eula)
                .setMessage(eulaContent)
                .setPositiveButton("ACCEPT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean(String.valueOf(R.string.show_eula), false);
                                editor.commit();

                            }
                        }
                )
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                getActivity().finish();
                                System.exit(0);
                            }
                        }
                )
                .create();
        return dialog;
    }
}