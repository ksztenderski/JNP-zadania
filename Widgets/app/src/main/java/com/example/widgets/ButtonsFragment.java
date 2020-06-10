package com.example.widgets;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ButtonsFragment extends Fragment {
    public ButtonsFragment() {
        // Required empty public constructor
    }

    static ButtonsFragment newInstance() {
        return new ButtonsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_buttons, container, false);

        rootView.findViewById(R.id.imageButton).setOnClickListener(view ->
                displayToast(getResources().getString(R.string.imagebutton) + " Clicked!"));

        final CheckBox checkBox1 = rootView.findViewById(R.id.checkBox1);
        final CheckBox checkBox2 = rootView.findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = rootView.findViewById(R.id.checkBox3);

        checkBox1.setOnClickListener(view ->
                displayToast(getResources().getString(R.string.checkbox1) + (checkBox1.isChecked() ? " ON" : " OFF")));
        checkBox2.setOnClickListener(view ->
                displayToast(getResources().getString(R.string.checkbox1) + (checkBox2.isChecked() ? " ON" : " OFF")));
        checkBox3.setOnClickListener(view ->
                displayToast(getResources().getString(R.string.checkbox1) + (checkBox3.isChecked() ? " ON" : " OFF")));

        rootView.findViewById(R.id.radioButton1).setOnClickListener(view ->
                displayToast(getResources().getString(R.string.radiobutton1) + " Checked"));
        rootView.findViewById(R.id.radioButton2).setOnClickListener(view ->
                displayToast(getResources().getString(R.string.radiobutton2) + " Checked"));
        rootView.findViewById(R.id.radioButton3).setOnClickListener(view ->
                displayToast(getResources().getString(R.string.radiobutton3) + " Checked"));

        final ToggleButton toggleButton = rootView.findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(view ->
                displayToast(getResources().getString(R.string.togglebutton) + (toggleButton.isChecked() ? " ON" : " OFF")));

        final Switch switchButton = rootView.findViewById(R.id.switchButton);
        switchButton.setOnClickListener(view ->
                displayToast(getResources().getString(R.string.switchButton) + (switchButton.isChecked() ? " ON" : " OFF")));

        rootView.findViewById(R.id.floatingActionButton).setOnClickListener(view->
                displayToast("FloatingActionButton Clicked!"));

        return rootView;
    }

    private void displayToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
