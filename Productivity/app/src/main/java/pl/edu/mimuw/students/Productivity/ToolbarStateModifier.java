package pl.edu.mimuw.students.Productivity;

import pl.edu.mimuw.students.Productivity.Database.Task.Task;

public interface ToolbarStateModifier {
    public void setState(ToolbarState state, Task task);
    public ToolbarState getState();
}
