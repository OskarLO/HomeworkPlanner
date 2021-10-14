package com.test2.homework_planer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class CreateItemFragment extends Fragment {

    private Button buttonSave;
    private Button buttonCancle;

    public CreateItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonSave = view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });

        buttonCancle = view.findViewById(R.id.buttonDelete);
        buttonCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new Overview()).commit();
            }
        });

    }

    private void sendData() {
        EditText titleBox = getActivity().findViewById(R.id.enterTitle);
        EditText subjectBox = getActivity().findViewById(R.id.enterSubject);
        EditText deadlineBox = getActivity().findViewById(R.id.enterDeadline);
        EditText commentBox = getActivity().findViewById(R.id.editComment);

        String title = titleBox.getText().toString();
        String subject = subjectBox.getText().toString();
        int deadline = Integer.parseInt(deadlineBox.getText().toString().trim());
        String comment = commentBox.getText().toString();

        Intent send = new Intent(getContext(), MainActivity.class);

        send.putExtra(Overview.TITLE, title);
        send.putExtra(Overview.SUBJECT, subject);
        send.putExtra(Overview.DEADLINE, deadline);
        send.putExtra(Overview.COMMENT, comment);

        if (!(title.equals("")) & !(subject.equals("")) & deadline!=0) {
            startActivity(send);

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Title, Subject and Deadline(dd/mm/yyyy) can not be empty...", Toast.LENGTH_LONG).show();

        }

    }
}