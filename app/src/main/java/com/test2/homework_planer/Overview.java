package com.test2.homework_planer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Overview extends Fragment {

    private ArrayList<ListItem> tasks;
    private ArrayAdapter tasksAdapter;
    private ListView listViewTasks;
    private TextView textViewDate;
    private Button btnAddToList;
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private String today;

    public static final String TITLE = "TITLE";
    public static final String SUBJECT = "SUBJECT";
    public static final String DEADLINE = "DEADLINE";
    public static final String COMMENT = "COMMENT";

    public Overview() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        btnAddToList = view.findViewById(R.id.buttonAddToList);
        btnAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new CreateItemFragment()).commit();
            }
        });

        tasks = new ArrayList<>() ;
        tasksAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tasks);
        listViewTasks = view.findViewById(R.id.taskList);
        listViewTasks.setAdapter(tasksAdapter);

        time();
        receive();
        viewListItem();

    }

    private void time() {
        textViewDate = getActivity().findViewById(R.id.textViewDate);
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        today = sdf.format(calendar.getTime());
        textViewDate.setText(today);
    }

    private void viewListItem() {
        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //intent for å komme til task[i] fragment
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new EnterTaskFragment()).commit();
                OverviewDirections.ActionOverviewToEnterTaskFragment action = OverviewDirections.actionOverviewToEnterTaskFragment();
                action.setTaskTitle(tasks.get(i).getTitle());
                Navigation.findNavController(view).navigate(action);

            }
        });
    }

    private void receive() {

        Intent receive = getActivity().getIntent();

        String title = receive.getStringExtra(TITLE);
        String subject = receive.getStringExtra(SUBJECT);
        int deadline = receive.getIntExtra(DEADLINE, 0);
        String comment = receive.getStringExtra(COMMENT);

        if(!(title==null)){
            tasks.add(new ListItem(title, subject, deadline, comment));

            Toast.makeText(getActivity().getApplicationContext(), "List item added", Toast.LENGTH_SHORT).show();
            System.out.println(tasks);
        }
    }
}