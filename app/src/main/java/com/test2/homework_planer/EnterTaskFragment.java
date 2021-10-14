package com.test2.homework_planer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EnterTaskFragment extends Fragment {

    private Button btnSaveTask;
    private Button btnDeleteTask;
    private Button btnCancle;
    private TextView textViewTitle;

    public EnterTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewTitle = view.findViewById(R.id.textViewTitle);
        Bundle argsTemp = getArguments();
        EnterTaskFragmentArgs args = EnterTaskFragmentArgs.fromBundle(argsTemp);
        textViewTitle.setText(args.getTaskTitle());


        btnCancle = view.findViewById(R.id.btnCancle);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new Overview()).commit();
            }
        });

    }
}