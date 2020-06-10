package pl.edu.mimuw.students.Productivity;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pl.edu.mimuw.students.Productivity.Database.Category.Category;
import pl.edu.mimuw.students.Productivity.Database.Category.CategoryListAdapter;
import pl.edu.mimuw.students.Productivity.Database.Category.CategoryViewModel;

public class ChooseCategoryFragment extends Fragment {
    public CategoryViewModel categoryViewModel;
    private SelectedTaskViewModel taskViewModel;
    private EditText newCategoryName;
    private FloatingActionButton newCategoryButton;

    public ChooseCategoryFragment() {
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            checkEmptyNewCategoryName();
        }
    };

    private void checkEmptyNewCategoryName() {
        if (newCategoryName.getText().toString().equals("")) {
            newCategoryButton.getBackground().setAlpha(128);
            newCategoryButton.setImageResource(R.drawable.ic_add_black_opacity_25_24dp);
            newCategoryButton.setEnabled(false);
        } else {
            newCategoryButton.getBackground().setAlpha(255);
            newCategoryButton.setImageResource(R.drawable.ic_add_black_24dp);
            newCategoryButton.setEnabled(true);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.choose_category_fragment, container, false);

        newCategoryName = rootView.findViewById(R.id.new_category_name);
        newCategoryButton = rootView.findViewById(R.id.button_add_new_category);
        newCategoryName.addTextChangedListener(textWatcher);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview_choose_category);

        final CategoryListAdapter adapter = new CategoryListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        taskViewModel = new ViewModelProvider(requireActivity()).get(SelectedTaskViewModel.class);

        // Update the cached copy of the categories in the adapter.
        categoryViewModel.getAllCategories().observe(requireActivity(), adapter::setCategories);

        checkEmptyNewCategoryName();

        newCategoryButton.setOnClickListener(view -> {
            String categoryName = newCategoryName.getText().toString();
            Category category = new Category(categoryName);
            categoryViewModel.insert(category);
            newCategoryName.setText("");

            //this has been commented out as the category here does not yet have a proper id
            //and sending it to chooseCategory() results in undefined behaviour
            //chooseCategory(category);
        });

        return rootView;
    }

    public void chooseCategory(Category category) {
        taskViewModel.setCategory(category);
        NavHostFragment.findNavController(this).popBackStack();
    }
}
