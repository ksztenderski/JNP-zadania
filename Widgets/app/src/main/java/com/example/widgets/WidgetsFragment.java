package com.example.widgets;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class WidgetsFragment extends Fragment {
    private TextView progressTextView;

    class MyOnSeekBarChangeListener implements OnSeekBarChangeListener {
        private int stringResource;

        public MyOnSeekBarChangeListener(int stringResource) {
            this.stringResource = stringResource;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            progressTextView.setText(getString(stringResource, progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }


    public WidgetsFragment() {
        // Required empty public constructor
    }

    static WidgetsFragment newInstance() {
        return new WidgetsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_widgets, container, false);

        progressTextView = rootView.findViewById(R.id.progressTextView);
        TextView timerTextView = rootView.findViewById(R.id.timerTextView);

        SeekBar seekBar = rootView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener(R.string.SeekBarProgress));

        SeekBar seekBarDiscrete = rootView.findViewById(R.id.seekBarDiscrete);
        seekBarDiscrete.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener(R.string.SeekBarDiscreteProgress));

        ProgressBar progressBar = rootView.findViewById(R.id.progressBar);

        progressBar.setMax(100);

        new CountDownTimer(100000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;

                timerTextView.setText(getResources().getString(R.string.TimerProgress, 100 - secondsRemaining));

                progressBar.setProgress((int) (100 - secondsRemaining));
            }

            @Override
            public void onFinish() {
                timerTextView.setText(R.string.countdownFinished);
            }
        }.start();

        return rootView;
    }
}
