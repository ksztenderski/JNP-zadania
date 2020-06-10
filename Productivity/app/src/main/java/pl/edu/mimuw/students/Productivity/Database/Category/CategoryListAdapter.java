package pl.edu.mimuw.students.Productivity.Database.Category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.edu.mimuw.students.Productivity.ChooseCategoryFragment;
import pl.edu.mimuw.students.Productivity.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder> {
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryNameView;
        private final EditText editCategoryNameView;
        private final FloatingActionButton deleteCategoryButton;
        private final FloatingActionButton saveCategoryButton;
        private final LinearLayout layout;

        private CategoryViewHolder(View itemView) {
            super(itemView);
            categoryNameView = itemView.findViewById(R.id.category_name);
            editCategoryNameView = itemView.findViewById(R.id.edit_category_name);
            deleteCategoryButton = itemView.findViewById(R.id.delete_category_button);
            saveCategoryButton = itemView.findViewById(R.id.save_category_button);
            layout = itemView.findViewById(R.id.category_layout);
        }
    }

    private ChooseCategoryFragment context;
    private final LayoutInflater inflater;
    private List<Category> categories;

    public CategoryListAdapter(ChooseCategoryFragment context) {
        inflater = LayoutInflater.from(context.getActivity());
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_choose_category_activity, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (categories != null) {
            Category current = categories.get(position);
            holder.layout.setOnClickListener(view -> context.chooseCategory(current));
            holder.layout.setOnLongClickListener(view -> {
                editCategory(holder, current);

                return true;
            });

            holder.categoryNameView.setText(current.getName());
            hideEditing(holder);
        } else {
            // Covers the case of data not being ready yet.
            holder.categoryNameView.setText(R.string.category_not_ready_error);
        }
    }

    private void hideEditing(CategoryViewHolder holder) {
        holder.editCategoryNameView.setVisibility(View.GONE);
        holder.deleteCategoryButton.hide();
        holder.saveCategoryButton.hide();
        holder.categoryNameView.setVisibility(View.VISIBLE);
    }

    private void editCategory(CategoryViewHolder holder, Category category) {
        holder.editCategoryNameView.setVisibility(View.VISIBLE);
        holder.deleteCategoryButton.show();
        holder.saveCategoryButton.show();
        holder.categoryNameView.setVisibility(View.GONE);

        holder.editCategoryNameView.setText(holder.categoryNameView.getText().toString());

        holder.deleteCategoryButton.setOnClickListener(view -> {
            hideEditing(holder);

            context.categoryViewModel.deleteCategory(category.getId());
        });

        holder.saveCategoryButton.setOnClickListener(view -> {
            hideEditing(holder);

            category.setName(holder.editCategoryNameView.getText().toString());
            context.categoryViewModel.update(category);
        });
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (categories != null) {
            return categories.size();
        } else return 0;
    }
}
