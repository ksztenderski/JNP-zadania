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
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pl.edu.mimuw.students.Productivity.Database.Task.TaskListAdapter;
import pl.edu.mimuw.students.Productivity.Database.Task.TaskViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TaskViewModel taskViewModel;

    private int planned;
    private int completed;

    private TextView stats;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    protected void setPlanned(Integer i) {
        this.planned = i;
        setStats();
    }

    protected void setDone(Integer i) {
        this.completed = i;
        setStats();
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.planned = 0;
        this.completed = 0;
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView =  inflater.inflate(R.layout.fragment_statistics, container, false);

        setHasOptionsMenu(true);
        return thisView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar_statistics);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        NavController navHostFragment = NavHostFragment.findNavController(this);
        AppBarConfiguration config = new AppBarConfiguration.Builder(R.id.taskListFragment, R.id.timerFragment, R.id.statisticsFragment, R.id.queueFragment).build();

        NavigationUI.setupWithNavController(toolbar, navHostFragment, config);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_stat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final TaskListAdapter adapter = new TaskListAdapter(getContext());
        recyclerView.setAdapter(adapter);

        taskViewModel.getSessionDone().observe(getViewLifecycleOwner(), this::setDone);
        taskViewModel.getSessionLeft().observe(getViewLifecycleOwner(), this::setPlanned);
        taskViewModel.getStatTasks().observe(getViewLifecycleOwner(), adapter::setTasks);
        stats = view.findViewById(R.id.stat_txt);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem editTask = menu.findItem(R.id.newTaskFragment);
        MenuItem removeTask = menu.findItem(R.id.action_delete_task);
        MenuItem settings = menu.findItem(R.id.settingsFragment);
        MenuItem cancel = menu.findItem(R.id.action_cancel_task);
        editTask.setVisible(false);
        removeTask.setVisible(false);
        cancel.setVisible(false);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Task Statistics");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }


    private void setStats() {
        if (this.planned + this.completed == 0) {
            stats.setText(String.format("Completed: %d/%d (%d%%)\nLeft session: %d", this.completed, this.completed + this.planned,  0, this.planned));
        } else {
            stats.setText(String.format("Completed: %d/%d (%d%%)\nLeft session: %d", this.completed, this.completed + this.planned,  100 * this.completed / (this.planned + this.completed), this.planned));
        }
    }
}
