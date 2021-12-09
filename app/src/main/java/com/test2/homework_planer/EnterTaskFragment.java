package com.test2.homework_planer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EnterTaskFragment extends Fragment {

    private Button btnSaveTask, btnDeleteTask, btnCancle;
    private TextView textViewTitle, textViewDeadline, editComment;
    private String taskId;

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
        textViewDeadline = view.findViewById(R.id.textViewDeadline);
        editComment = view.findViewById(R.id.editComment);

        Bundle argsTemp = getArguments();
        EnterTaskFragmentArgs args = EnterTaskFragmentArgs.fromBundle(argsTemp);

        textViewTitle.setText(args.getTaskTitle());
        textViewDeadline.setText("Deadline: " + args.getTaskDeadline());
        editComment.setText(args.getTaskComment());
        taskId = args.getTaskId();

        btnSaveTask = view.findViewById(R.id.buttonSave);
        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newComment = editComment.getText().toString();

                EnterTaskFragmentDirections.ActionEnterTaskFragmentToOverview action = EnterTaskFragmentDirections.actionEnterTaskFragmentToOverview();
                action.setEditTaskId(taskId);
                action.setNewComment(newComment);
                action.setSaveBool(true);
                Navigation.findNavController(view).navigate(action);
            }
        });

        btnDeleteTask = view.findViewById(R.id.buttonDelete);
        btnDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnterTaskFragmentDirections.ActionEnterTaskFragmentToOverview action = EnterTaskFragmentDirections.actionEnterTaskFragmentToOverview();
                action.setEditTaskId(taskId);
                action.setDeleteBool(true);
                Navigation.findNavController(view).navigate(action);
            }
        });

        btnCancle = view.findViewById(R.id.btnCancle);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new Overview()).commit();
                EnterTaskFragmentDirections.ActionEnterTaskFragmentToOverview action = EnterTaskFragmentDirections.actionEnterTaskFragmentToOverview();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }
}