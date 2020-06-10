package pl.edu.mimuw.students.Productivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pl.edu.mimuw.students.Productivity.Database.Task.Task;
import pl.edu.mimuw.students.Productivity.Database.Task.TaskListAdapter;
import pl.edu.mimuw.students.Productivity.Database.Task.TaskViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListFragment extends Fragment implements TaskEditor {
    TaskViewModel taskViewModel;
    SelectedTaskViewModel selectedTaskViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TaskListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskListFragment newInstance(String param1, String param2) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_task_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final TaskListAdapter adapter = new TaskListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        adapter.setOnLongClickListener(this);
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        // Update the cached copy of the words in the adapter.
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), adapter::setTasks);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.newTaskFragment));

        selectedTaskViewModel = new ViewModelProvider(requireActivity()).get(SelectedTaskViewModel.class);
        selectedTaskViewModel.getSelectedTask().observe(getViewLifecycleOwner(), task -> {
            getActivity().invalidateOptionsMenu();
            if(task != null) {
                fab.hide();
            }
            else {
                fab.show();
            }
        });

        NavController navController = Navigation.findNavController(view);
        /*AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.taskListFragment, R.id.timerFragment, R.id.queueFragment).build();*/
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        AppBarConfiguration config = new AppBarConfiguration.Builder(R.id.taskListFragment, R.id.timerFragment, R.id.statisticsFragment, R.id.queueFragment).build();

        NavigationUI.setupWithNavController(toolbar, navController, config);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Productivity");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);




        MenuItem editTask = menu.findItem(R.id.newTaskFragment);
        MenuItem removeTask = menu.findItem(R.id.action_delete_task);
        MenuItem settings = menu.findItem(R.id.settingsFragment);
        MenuItem cancel = menu.findItem(R.id.action_cancel_task);
        if(selectedTaskViewModel.getSelectedTask().getValue() == null) {
            editTask.setVisible(false);
            removeTask.setVisible(false);
            cancel.setVisible(false);
        }
        else {
            settings.setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedTaskViewModel.selectTask(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_cancel_task :
                selectedTaskViewModel.selectTask(null);
                break;
            case R.id.action_delete_task :
                taskViewModel.delete(selectedTaskViewModel.getSelectedTask().getValue());
                selectedTaskViewModel.selectTask(null);
        }

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public void taskEdit(Task task) {
            selectedTaskViewModel.selectTask(task);
    }
}
