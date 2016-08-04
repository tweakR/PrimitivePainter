package com.example.vyacheslav.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.SeekBar;

public class ColorDialogFragment extends DialogFragment {
    private SeekBar mAlphaSeekBar;
    private SeekBar mRedSeekBar;
    private SeekBar mGreenSeekBar;
    private SeekBar mBlueSeekBar;
    private View mColorView;
    private int mColor;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View colorDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_color, null);
        builder.setView(colorDialogView);
        builder.setTitle(R.string.title_color_dialog);
        mAlphaSeekBar = (SeekBar) colorDialogView.findViewById(R.id.alphaSeekBar);
        mRedSeekBar = (SeekBar) colorDialogView.findViewById(R.id.redSeekBar);
        mGreenSeekBar = (SeekBar) colorDialogView.findViewById(R.id.greenSeekBar);
        mBlueSeekBar = (SeekBar) colorDialogView.findViewById(R.id.blueSeekBar);
        mColorView = colorDialogView.findViewById(R.id.colorView);

        mAlphaSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        mRedSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        mGreenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        mBlueSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        mColor = doodleView.getDrawingColor();

        mAlphaSeekBar.setProgress(Color.alpha(mColor));
        mRedSeekBar.setProgress(Color.red(mColor));
        mGreenSeekBar.setProgress(Color.green(mColor));
        mBlueSeekBar.setProgress(Color.blue(mColor));

        builder.setPositiveButton(R.string.button_set_color,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doodleView.setDrawingColor(mColor);
                    }
                });

        return builder.create();
    }

    private MainActivityFragment getDoodleFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.doodleFragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null) {
            fragment.setDialogOnScreen(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null) {
            fragment.setDialogOnScreen(false);
        }
    }

    private SeekBar.OnSeekBarChangeListener colorChangedListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mColor = Color.argb(mAlphaSeekBar.getProgress(), mRedSeekBar.getProgress(),
                        mGreenSeekBar.getProgress(), mBlueSeekBar.getProgress());
                mColorView.setBackgroundColor(mColor);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
