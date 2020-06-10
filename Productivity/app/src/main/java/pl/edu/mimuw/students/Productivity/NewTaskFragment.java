package pl.edu.mimuw.students.Productivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import pl.edu.mimuw.students.Productivity.Database.Category.Category;
import pl.edu.mimuw.students.Productivity.Database.Category.CategoryViewModel;
import pl.edu.mimuw.students.Productivity.Database.Task.RecurringTask;
import pl.edu.mimuw.students.Productivity.Database.Task.Task;
import pl.edu.mimuw.students.Productivity.Database.Task.TaskViewModel;

import java.util.List;

public class NewTaskFragment extends Fragment {
    public TaskViewModel taskViewModel;
    public CategoryViewModel categoryViewModel;
    SelectedTaskViewModel selectedTaskViewModel;

    public static final int CHOOSE_CATEGORY_ACTIVITY_REQUEST_CODE = 1;

    public static final String EXTRA_TASK_NAME_REPLY = "pl.edu.mimuw.students.android.productivity.extra.task_name.REPLY";
    public static final String EXTRA_NO_SESSIONS_REPLY = "pl.edu.mimuw.students.android.productivity.extra.no_sessions.REPLY";
    public static final String EXTRA_ID_REPLY = "pl.edu.mimuw.students.android.productivity.extra.task_id.REPLY";
    public static final String EXTRA_CATEGORY_ID_REPLY = "pl.edu.mimuw.students.android.productivity.extra.category_id.REPLY";
    public static final String EXTRA_RECURRING_REPLY = "pl.edu.mimuw.students.android.productivity.extra.recurring.REPLY";

    protected Category category;
    private Integer categoryId;
    private EditText editTaskName;
    private EditText editNoSessions;
    private Button chooseCategoryButton;
    private Switch isWeeklyToggle;
    private EditText monSNo;
    private EditText tueSNo;
    private EditText wedSNo;
    private EditText thuSNo;
    private EditText friSNo;
    private EditText satSNo;
    private EditText sunSNo;
    private TextView sessNoTitle;
    private LinearLayout weeklySessions;
    private Task taskToEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_task_fragment, container, false);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public static NewTaskFragment newInstance() {
        return new NewTaskFragment();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
         Toolbar toolbar = view.findViewById(R.id.toolbar_new_task);
         ((MainActivity) getActivity()).setSupportActionBar(toolbar);
         NavigationUI.setupWithNavController(
                  toolbar, navController, appBarConfiguration);

         setAttributes();
         weeklySessions.setVisibility(View.GONE);
         setAllFilters();
         setTaskToEdit();
         setListeners(view);
    }


    private void setListeners (View view) {
        chooseCategoryButton.setOnClickListener(view2 -> {
            Navigation.findNavController(view).navigate(R.id.action_newTaskFragment_to_chooseCategoryFragment);
        });

        isWeeklyToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editNoSessions.setVisibility(View.GONE);
                sessNoTitle.setVisibility(View.GONE);
                weeklySessions.setVisibility(View.VISIBLE);

            } else {
                editNoSessions.setVisibility(View.VISIBLE);
                sessNoTitle.setVisibility(View.VISIBLE);
                weeklySessions.setVisibility(View.GONE);
            }
        });

        final Button buttonSave = getActivity().findViewById(R.id.button_save);
        buttonSave.setOnClickListener(a -> save(view));

    }

    private void save(View view) {
        if (!errorFound()) {
            String taskName = editTaskName.getText().toString();
            if (isWeeklyToggle.isChecked()) {
                addRecurringTask(taskToEdit, taskName);
            } else {
                addNonRecurringTask(taskToEdit, taskName);
            }
        }
        selectedTaskViewModel.selectTask(null);
        //hides keyboard
        view.clearFocus();
        NavHostFragment.findNavController(this).navigate(R.id.action_newTaskFragment_to_taskListFragment);
    }

    private void setTaskToEdit() {
        taskToEdit = selectedTaskViewModel.getSelectedTask().getValue();

        if (taskToEdit == null) {
            taskToEdit = new Task("", 0, true);
            taskToEdit.setCategoryId(null);
            selectedTaskViewModel.selectTask(taskToEdit);
        }

        if (taskToEdit!= null && taskToEdit.getId() != null) {
            sessNoTitle.setText("Number of sessions left:");
            setEditedTask();
        }

        selectedTaskViewModel.getSelectedTask().observe(requireActivity(), new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                if (isAdded() && task != null) {
                    changedTask(task);
                }
            }
        });


    }

    private void changedTask(Task task) {
        taskToEdit = task;
        categoryId = task.getCategoryId();
        if (categoryId != null) {
            LiveData<Category> cat = categoryViewModel.getCategory(categoryId);
            cat.observe(requireActivity(), new Observer<Category>() {
                @Override
                public void onChanged(Category category) {
                    if (category == null) chooseCategoryButton.setText("");
                    else chooseCategoryButton.setText(category.getName());
                }
            });

            chooseCategoryButton.setBackgroundColor(getResources().getColor(R.color.addButtonEnabled));
        }

    }

    private void setEditedTask() {
        categoryId = taskToEdit.getCategoryId();
        if (categoryId != null)  chooseCategoryButton.setBackgroundColor(getResources().getColor(R.color.addButtonEnabled));
        editTaskName.setText(taskToEdit.getName());
        if (taskToEdit.isRecurring()) {
            isWeeklyToggle.setChecked(true);
            weeklySessions.setVisibility(View.VISIBLE);
            editNoSessions.setVisibility(View.GONE);
            LiveData<List<RecurringTask>> schedule = taskViewModel.getTaskSchedule(taskToEdit.getId());
            schedule.observe(getViewLifecycleOwner(), list -> {
                if (list != null && !list.isEmpty()) {
                    RecurringTask rec = list.get(0);
                    setWeekly(rec);
                }
            });

        } else {
            editNoSessions.setText(Integer.toString(taskToEdit.getSessionsLeft()));
        }
    }

    private void setWeekly(RecurringTask rec) {
        setDay(monSNo, rec.getMonTasks());
        setDay(tueSNo, rec.getTueTasks());
        setDay(wedSNo, rec.getWedTasks());
        setDay(thuSNo, rec.getThuTasks());
        setDay(friSNo, rec.getFriTasks());
        setDay(satSNo, rec.getSatTasks());
        setDay(sunSNo, rec.getSunTasks());
    }

    private void setDay(EditText text, int sNo) {
        if (sNo == 0) text.setText("");
        else text.setText(Integer.toString(sNo));
    }

    private void addRecurringTask(Task taskToEdit, String taskName) {
        int monSessions = getSNo(monSNo, 0);
        int tueSessions = getSNo(tueSNo, 0);
        int wedSessions = getSNo(wedSNo, 0);
        int thuSessions = getSNo(thuSNo, 0);
        int friSessions = getSNo(friSNo, 0);
        int satSessions = getSNo(satSNo, 0);
        int sunSessions = getSNo(sunSNo, 0);

        taskToEdit.setName(taskName);
        RecurringTask rec = new RecurringTask(monSessions, tueSessions, wedSessions, thuSessions, friSessions, satSessions, sunSessions);

        if (categoryId != null) {
            taskToEdit.setCategoryId(categoryId);
        }

        if (taskToEdit.getId() != null) {
            int id = taskToEdit.getId();
            rec.setId(id);

            if (taskToEdit.isRecurring()) {
                //had been recurring
                taskViewModel.update(taskToEdit);
                taskViewModel.updateRecurring(rec);

            } else {
                //hadn't - add recurrence
                taskToEdit.setRecurring(true);
                taskViewModel.update(taskToEdit);
                taskViewModel.insertRecurrence(rec);
            }
        } else {
            taskViewModel.insertRecurring(taskToEdit, rec);
        }
    }

    private void addNonRecurringTask(Task taskToEdit, String taskName) {
        int noSessions = getSNo(editNoSessions, 1);

        taskToEdit.setName(taskName);
        taskToEdit.setRecurring(false);
        taskToEdit.setSessionLeft(noSessions);

        if (categoryId != null) {
            taskToEdit.setCategoryId(categoryId);
        }


        if (taskToEdit.getId() != null) {
            int id = taskToEdit.getId();
            if (taskToEdit.isRecurring()) {
                taskViewModel.deleteRecurring(id);
            }

            taskViewModel.update(taskToEdit);

        } else {
            taskViewModel.insert(taskToEdit);
        }

    }

    private int getSNo(EditText text, int def) {
        return TextUtils.isEmpty(text.getText()) ? def : Integer.parseInt(text.getText().toString());
    }


    private boolean errorFound() {
        //Name is empty string
        if (TextUtils.isEmpty(editTaskName.getText())) {
            Toast.makeText(
                    getActivity(),
                    R.string.empty_name,
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (isWeeklyToggle.isChecked()) {
            int monSessions = getSNo(monSNo, 0);
            int tueSessions = getSNo(tueSNo, 0);
            int wedSessions = getSNo(wedSNo, 0);
            int thuSessions = getSNo(thuSNo, 0);
            int friSessions = getSNo(friSNo, 0);
            int satSessions = getSNo(satSNo, 0);
            int sunSessions = getSNo(sunSNo, 0);
            int sessNo = monSessions + tueSessions + wedSessions + thuSessions + friSessions + satSessions + sunSessions;
            //No sessions during the week

            if (sessNo <= 0) {
                Toast.makeText(
                        getActivity(),
                        R.string.zero_sessions,
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            //Too many sessions during week
            else if (sessNo + taskToEdit.getSessionsDone() > Task.MAX_SESSIONS) {
                Toast.makeText(
                        getActivity(),
                        R.string.too_many_sessions,
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
            //zero sessions chosen
            if (getSNo(editNoSessions, 1) < 0) {
                Toast.makeText(
                        getActivity(),
                        R.string.zero_sessions,
                        Toast.LENGTH_SHORT).show();
                return true;
            } else if (getSNo(editNoSessions, 1) == 0 && taskToEdit.getSessionsDone() == 0) {
                Toast.makeText(
                        getActivity(),
                        R.string.zero_sessions,
                        Toast.LENGTH_SHORT).show();
                return true;
            }

                //too many sessions
            else if (getSNo(editNoSessions, 1) + taskToEdit.getSessionsDone() > Task.MAX_SESSIONS) {
                Toast.makeText(
                        getActivity(),
                        R.string.too_many_sessions,
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }


    private void setAttributes() {
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        selectedTaskViewModel = new ViewModelProvider(requireActivity()).get(SelectedTaskViewModel.class);


        editTaskName = getActivity().findViewById(R.id.new_task_name);
        editNoSessions = getActivity().findViewById(R.id.new_task_no_sessions);
        chooseCategoryButton = getActivity().findViewById(R.id.choose_category_button);
        isWeeklyToggle = getActivity().findViewById(R.id.is_weekly_toggle);

        monSNo = getActivity().findViewById(R.id.mon_session_no);
        tueSNo = getActivity().findViewById(R.id.tue_session_no);
        wedSNo = getActivity().findViewById(R.id.wed_session_no);
        thuSNo = getActivity().findViewById(R.id.thu_session_no);
        friSNo = getActivity().findViewById(R.id.fri_session_no);
        satSNo = getActivity().findViewById(R.id.sat_session_no);
        sunSNo = getActivity().findViewById(R.id.sun_session_no);
        weeklySessions = getActivity().findViewById(R.id.weekly_sessions);
        sessNoTitle = getActivity().findViewById(R.id.session_number_title);

        chooseCategoryButton = getActivity().findViewById(R.id.choose_category_button);
        category = null;
        categoryId = null;

    }

    private  void setAllFilters() {
        // Sets filter for noSessions for (0, 100000).
        InputFilter[] filter = new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            String inp = dest.toString() + source.toString();
            int input = inp.equals("") ? 1 : Integer.parseInt(inp);
            if (input >= 0 && input < 100000) {
                return null;
            }
            return "";
        }
        };
        editNoSessions.setFilters(filter);
        monSNo.setFilters(filter);
        tueSNo.setFilters(filter);
        wedSNo.setFilters(filter);
        thuSNo.setFilters(filter);
        friSNo.setFilters(filter);
        satSNo.setFilters(filter);
        sunSNo.setFilters(filter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Set New Task");

    }
}
