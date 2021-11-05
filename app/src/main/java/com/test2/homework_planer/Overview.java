package com.test2.homework_planer;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Overview extends Fragment {

    private ArrayList<ListItem> tasks;
    private ArrayList<String> tasksIdList;
    private ArrayAdapter tasksAdapter;
    private ListView listViewTasks;
    private TextView textViewDate;
    private Button btnAddToList;
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private String today;
    private Boolean saveTaskBool, deleteTaskBool, createTaskBool;
    private String taskId;
    private String editComment;
    private FirebaseFirestore firestoreDB;
    private CollectionReference taskCollectionReference;
    private ListenerRegistration fireStoreListenerReg;
    private String createTitle;
    private String createSubject;
    private int createDeadline;
    private String createComment;

    public Overview() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        createFirestoreListener();
    }

    private void createFirestoreListener() {

        fireStoreListenerReg = taskCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.w(TAG, "listen failed" + error);
                    return;
                }

                for (DocumentChange docChange : value.getDocumentChanges()){
                    QueryDocumentSnapshot docSnap = docChange.getDocument();
                    ListItem item = docSnap.toObject(ListItem.class);
                    item.setTaskId(docSnap.getId());
                    int pos = tasksIdList.indexOf(item.getTaskId());

                    switch (docChange.getType()){
                        case ADDED:
                            tasks.add(item);
                            tasksIdList.add(docSnap.getId());
                            tasksAdapter.notifyDataSetChanged();
                            break;
                        case REMOVED:
                            tasks.remove(pos);
                            tasksIdList.remove(pos);
                            tasksAdapter.notifyDataSetChanged();
                            break;
                        case MODIFIED:
                            tasks.set(pos, item);
                            tasksAdapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        if(fireStoreListenerReg != null){
            fireStoreListenerReg.remove();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestoreDB = FirebaseFirestore.getInstance();
        taskCollectionReference = firestoreDB.collection("tasks");

        btnAddToList = view.findViewById(R.id.buttonAddToList);
        btnAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.createItemFragment);
            }
        });

        tasks = new ArrayList<>();
        tasksIdList = new ArrayList<>();
        tasksAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tasks);
        listViewTasks = view.findViewById(R.id.taskList);
        listViewTasks.setAdapter(tasksAdapter);

        //safe args variables
        Bundle argsTemp = getArguments();
        OverviewArgs args = OverviewArgs.fromBundle(argsTemp);
        taskId = args.getEditTaskId();
        editComment = args.getNewComment();
        //---------
        createTitle = args.getCreateTitle();
        createSubject = args.getCreateSubject();
        createDeadline = args.getCreateDeadline();
        createComment = args.getCreateComment();
        //---------
        saveTaskBool = args.getSaveBool();
        deleteTaskBool = args.getDeleteBool();
        createTaskBool = args.getCreateBool();

        if(saveTaskBool == true){
            editTask(taskId, editComment);
        }
        if(deleteTaskBool == true){
            deleteTask(taskId);
        }
        if(createTaskBool == true){
            createTask(createTitle, createSubject, createDeadline, createComment);
        }

        time();
        viewListItem();
    }

    private void editTask(String i, String comment) {
        taskCollectionReference.document(i).update("comment", comment);
        saveTaskBool = false;
    }

    private void deleteTask(String i) {
        taskCollectionReference.document(i).delete();
        deleteTaskBool = false;
    }

    private void time() {
        textViewDate = getActivity().findViewById(R.id.textViewDate);
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        today = sdf.format(calendar.getTime());
        textViewDate.setText(today);
    }

    private void viewListItem() {
        System.out.println(tasksIdList);
        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Action for å komme til task[i] fragment
                OverviewDirections.ActionOverviewToEnterTaskFragment action = OverviewDirections.actionOverviewToEnterTaskFragment();
                action.setTaskTitle(tasks.get(i).getTitle());
                action.setTaskDeadline(tasks.get(i).getDeadline());
                action.setTaskComment(tasks.get(i).getComment());
                action.setTaskId(tasks.get(i).getTaskId());
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    private void createTask(String title, String subject, int deadline, String comment) {

        String taskId = "0"; //String.valueOf(tasks.size()); //dermed vil TaskId bli lik som array plassering

        if(!(title == null)){
            ListItem newItem = new ListItem(taskId, title, subject, deadline, comment);
            taskCollectionReference.add(newItem);
            createTaskBool = false;

            Toast.makeText(getActivity().getApplicationContext(), "List item added", Toast.LENGTH_SHORT).show();
        }
    }
}