package com.test2.homework_planer;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;


public class CreateItemFragment extends Fragment {

    private int day, month, year;
    private Button buttonSave, buttonCancle, btnEnterDate;
    private DatePickerDialog.OnDateSetListener listener;

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
                sendData(view);
            }
        });

        buttonCancle = view.findViewById(R.id.buttonCancle);
        buttonCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new Overview()).commit();
                Navigation.findNavController(view).navigate(R.id.overview);
            }
        });


        listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                int newDay = day;
                int newMonth = month+1;
                int newYear = year;

                String strDate = (newDay + "/" + newMonth + "/" + newYear);
                btnEnterDate.setText(strDate);

                System.out.println("success");
            }
        };

        pickDate(view);
    }

    private void pickDate(View view) {
        btnEnterDate = view.findViewById(R.id.btnEnterDate);
        btnEnterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTime();

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), listener, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void getTime() {
        Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
    }

    //datePickerDialog er ikke helt ferdig så deadline settes til 0
    private void sendData(View view) {
        EditText titleBox = getActivity().findViewById(R.id.enterTitle);
        EditText subjectBox = getActivity().findViewById(R.id.enterSubject);
        //EditText deadlineBox = getActivity().findViewById(R.id.enterDeadline);
        EditText commentBox = getActivity().findViewById(R.id.editComment);


        String title = titleBox.getText().toString();
        String subject = subjectBox.getText().toString();
        //int deadline = Integer.parseInt(deadlineBox.getText().toString().trim());
        String comment = commentBox.getText().toString();

        CreateItemFragmentDirections.ActionCreateItemFragmentToOverview action = CreateItemFragmentDirections.actionCreateItemFragmentToOverview();
        action.setCreateBool(true);
        action.setCreateTitle(title);
        action.setCreateSubject(subject);
        action.setCreateDeadline(0);//deadline
        action.setCreateComment(comment);

        if(title!=null && subject!=null){
            Navigation.findNavController(view).navigate(action);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Title, Subject and Deadline fields are required!", Toast.LENGTH_SHORT).show();
        }

    }
}