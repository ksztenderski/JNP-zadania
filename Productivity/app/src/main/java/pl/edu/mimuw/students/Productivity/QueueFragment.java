package pl.edu.mimuw.students.Productivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


import pl.edu.mimuw.students.Productivity.Database.Task.Task;
import pl.edu.mimuw.students.Productivity.Database.Task.TaskQueued;
import pl.edu.mimuw.students.Productivity.Database.Task.TaskQueuedAdapter;
import pl.edu.mimuw.students.Productivity.Database.Task.TaskViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QueueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QueueFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private LiveData<List<TaskQueued>> listLiveData;

    private RecyclerView recyclerView;
    private TaskQueuedAdapter recyclerAdapter;
    private AtomicBoolean atomicBoolean;

    public QueueFragment() {
        atomicBoolean = new AtomicBoolean(false);
    }


    public static QueueFragment newInstance() {
        return new QueueFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.queue_fragment, container, false);

        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recyclerview_queue);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        recyclerAdapter = new TaskQueuedAdapter(taskViewModel, getViewLifecycleOwner());
        recyclerView.setAdapter(recyclerAdapter);

        listLiveData = taskViewModel.getQueue();
        listLiveData.observe(getViewLifecycleOwner(), recyclerAdapter::setList);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int positionHolder = viewHolder.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();
                if (listLiveData.hasObservers()) {
                    listLiveData.removeObservers(getViewLifecycleOwner());
                }
                recyclerAdapter.swapTask(taskViewModel, positionHolder, positionTarget);
                recyclerAdapter.notifyItemMoved(positionHolder, positionTarget);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab_add = view.findViewById(R.id.fab_add_queue);
        fab_add.setOnClickListener(v -> {
            if (!listLiveData.hasObservers()) {
                listLiveData.observe(getViewLifecycleOwner(), recyclerAdapter::setList);
            }
            atomicBoolean.set(true);
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            taskViewModel.getAvailableTask().observe(getViewLifecycleOwner(), list -> {
                if (atomicBoolean.getAndSet(false)) {
                    QueueTaskDialog addTaskDialog = new QueueTaskDialog(list, taskViewModel, getViewLifecycleOwner());
                    addTaskDialog.show(fm, "fragment_add_task");
                }
            });
        });

        FloatingActionButton fab_reset = view.findViewById(R.id.fab_reset_queue);
        fab_reset.setOnClickListener(v -> {
            if (!listLiveData.hasObservers()) {
                listLiveData.observe(getViewLifecycleOwner(), recyclerAdapter::setList);
            }
            taskViewModel.deleteAllTaskQueued();
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar_queue);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        NavController navHostFragment = NavHostFragment.findNavController(this);
        AppBarConfiguration config = new AppBarConfiguration.Builder(R.id.taskListFragment, R.id.timerFragment, R.id.statisticsFragment, R.id.queueFragment).build();

        NavigationUI.setupWithNavController(toolbar, navHostFragment, config);


        //NavController navController = Navigation.findNavController(view);
        /*AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build(); */
       /* AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.taskListFragment, R.id.timerFragment, R.id.queueFragment).build();
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);*/
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Today's Task Queue");

        MenuItem editTask = menu.findItem(R.id.newTaskFragment);
        MenuItem removeTask = menu.findItem(R.id.action_delete_task);
        MenuItem settings = menu.findItem(R.id.settingsFragment);
        MenuItem cancel = menu.findItem(R.id.action_cancel_task);
        editTask.setVisible(false);
        removeTask.setVisible(false);
        cancel.setVisible(false);
    }

        @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }
}



