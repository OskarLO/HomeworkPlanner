package com.test2.homework_planer;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Overview extends Fragment {

    private ArrayList<ListItem> tasks;
    private ArrayList<String> tasksIdList;
    private ArrayAdapter tasksAdapter;
    private ListView listViewTasks;
    private TextView textViewDate;
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private String today, taskId, editComment, createTitle, createSubject, createDeadline, createComment, username;
    private Boolean saveTaskBool, deleteTaskBool, createTaskBool;

    private FloatingActionButton mainButton, addTask, signOut;
    private Animation buttonOpen, buttonClose, rotateBack, rotateForw;
    private Boolean buttonIsOpen = false;

    private FirebaseFirestore firestoreDB;
    private CollectionReference taskCollectionReference;
    private ListenerRegistration fireStoreListenerReg;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

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
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null){
            username = auth.getCurrentUser().getDisplayName();
            createFirestoreListener();
        }

        closeAniButton();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        tasks.clear();
        tasksAdapter.notifyDataSetChanged();

        auth.removeAuthStateListener(authStateListener);
        if(fireStoreListenerReg != null){
            fireStoreListenerReg.remove();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestoreDB = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null){
            username = auth.getCurrentUser().getDisplayName();
            taskCollectionReference = firestoreDB.collection(username);
        }

        tasks = new ArrayList<>();
        tasksIdList = new ArrayList<>();
        tasksAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tasks);
        listViewTasks = view.findViewById(R.id.taskList);
        listViewTasks.setAdapter(tasksAdapter);

        mainButton = view.findViewById(R.id.mainButton);
        addTask = view.findViewById(R.id.addTask);
        signOut = view.findViewById(R.id.signOut);

        buttonOpen = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.button_open);
        buttonClose = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.button_close);
        rotateForw =AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_forward);
        rotateBack =AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_backwards);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButton();
            }
        });
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.createItemFragment);
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(getActivity().getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity().getApplicationContext(), "User logged out", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

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
        createAuthStateListener();
    }

    private void animateButton(){
        if(buttonIsOpen){
            closeAniButton();
        } else {
            mainButton.startAnimation(rotateBack);
            addTask.startAnimation(buttonOpen);
            signOut.startAnimation(buttonOpen);
            addTask.setClickable(true);
            signOut.setClickable(true);
            buttonIsOpen = true;
        }
    }

    private void closeAniButton(){
        mainButton.startAnimation(rotateForw);
        addTask.startAnimation(buttonClose);
        signOut.startAnimation(buttonClose);
        addTask.setClickable(false);
        signOut.setClickable(false);
        buttonIsOpen = false;
    }

    private void createAuthStateListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = auth.getCurrentUser();

                if (currentUser == null) {
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());

                    signInLauncher.launch(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build());
                }
                else {
                    username = auth.getCurrentUser().getDisplayName();
                    taskCollectionReference = firestoreDB.collection(username);
                }
            }
        };
    }

    protected void onSignInResult(FirebaseAuthUIAuthenticationResult result) {

        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser currentUser = auth.getCurrentUser();
            Toast.makeText(getActivity().getApplicationContext(), "Signed in as " + currentUser.getDisplayName(), Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), "Signed in cancelled", Toast.LENGTH_LONG).show();
        }
    }

    private void createFirestoreListener() {
        fireStoreListenerReg = firestoreDB.collection(username).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    Collections.sort(tasks, new SortByDate());
                }
            }
        });
    }

    private void editTask(String i, String comment) {
        taskCollectionReference.document(i).update("comment", comment);
        saveTaskBool = false;
    }

    private void deleteTask(String i) {
        taskCollectionReference.document(i).delete();
        deleteTaskBool = false;

        Toast.makeText(getActivity().getApplicationContext(), "List item deleted", Toast.LENGTH_SHORT).show();
    }

    private void createTask(String title, String subject, String deadline, String comment) {

        String taskId = "0"; //denne settes til firestore id ved innhenting

        if(!(title == null)){
            ListItem newItem = new ListItem(taskId, title, subject, deadline, comment);
            taskCollectionReference.add(newItem);
            createTaskBool = false;

            Toast.makeText(getActivity().getApplicationContext(), "List item added", Toast.LENGTH_SHORT).show();
        }
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
                //Action for å komme til task[i] fragment
                String Deadline = tasks.get(i).getDeadline(); //tasks.get(i).getDeadline()

                OverviewDirections.ActionOverviewToEnterTaskFragment action = OverviewDirections.actionOverviewToEnterTaskFragment();
                action.setTaskTitle(tasks.get(i).getTitle());
                action.setTaskDeadline(Deadline);
                action.setTaskComment(tasks.get(i).getComment());
                action.setTaskId(tasks.get(i).getTaskId());
                Navigation.findNavController(view).navigate(action);
            }
        });
    }
}