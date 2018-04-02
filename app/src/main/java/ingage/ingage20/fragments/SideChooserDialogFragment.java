package ingage.ingage20.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.cast.CastRemoteDisplayLocalService;

import ingage.ingage20.R;
import ingage.ingage20.activities.MainActivity;

/**
 * Created by wuv66 on 3/31/2018.
 */

public class SideChooserDialogFragment extends android.support.v4.app.DialogFragment {

    TextView tvMsg;
    TextView tvDescription;
    TextView tvCapacity;
    final static int REQUEST_CODE_SIDE_DIALOG = 100;

    public static SideChooserDialogFragment newInstance(String text) {
        SideChooserDialogFragment f = new SideChooserDialogFragment();
        Bundle args = new Bundle();
        args.putString("text", text);
        f.setArguments(args);
        return f;
    }

    public SideChooserDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_side_dialog, container);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_choose_side_dialog, null);

        tvMsg = (TextView) view.findViewById(R.id.tvChooseSide);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvCapacity = (TextView) view.findViewById(R.id.tvSideCapacity);

        tvMsg.setText(R.string.pick_side);
        tvDescription.setText(getArguments().getString("text"));
        tvCapacity.setText(FragmentBase.threadCapacity);

        if(getArguments().getString("text").length() == 0 || getArguments().getString("text") == null)
            tvDescription.setText("No description available.");

        alertDialogBuilder.setView(view);

        alertDialogBuilder.setPositiveButton("Agree",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra("side", true);
                getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE_SIDE_DIALOG, intent);
                dismiss();
            }
        });

        alertDialogBuilder.setNegativeButton("Disagree",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra("side", false);
                getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE_SIDE_DIALOG , intent);
                dismiss();
            }
        });

        Dialog d = alertDialogBuilder.create();
        // request a window without the title
        d.getWindow().setTitle(FragmentBase.threadTitle);
//        return alertDialogBuilder.create();

        return d;
    }

}