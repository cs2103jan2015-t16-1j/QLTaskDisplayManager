import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

public class TaskDisplayManagerTest implements Observer {
    private TaskDisplayManager taskManager;
    private List<Task> display;
    private boolean updated;
    
    @Before
    public void setUp() throws Exception {
        Task task1 = new Task("task 1");
        task1.setPriority("H");
        task1.setStartDate("2603");
        task1.setDueDate("2703");
        Task task2 = new Task("task 2");
        task2.setPriority("M");
        task2.setStartDate("2603");
        task2.setDueDate("2803");
        Task task3 = new Task("task 3");
        task3.setPriority("M");
        task3.setStartDate("2703");
        task3.setDueDate("2803");
        List<Task> taskList = new LinkedList<Task>();
        taskList.add(task1);
        taskList.add(task2);
        taskList.add(task3);
        taskManager = new TaskDisplayManager();
        taskManager.addObserver(this);
        taskManager.updateTaskList(taskList);
    }

    @Test
    public void test() {
        /* This is to test tasks are retrieved in the added order */
        assertEquals("task 1", display.get(0).getName());
        assertEquals("task 2", display.get(1).getName());
        assertEquals("task 3", display.get(2).getName());
        
        /* This is to test if the task is sorted successfully according to priority */
        updated = false; 
        taskManager.addSort(TaskDisplayManager.SortCriteria.PRIORITY, true);
        taskManager.apply();
        assertEquals(true, updated);
        assertEquals("M",  display.get(0).getPriority());
        assertEquals("M",  display.get(1).getPriority());
        assertEquals("H",  display.get(2).getPriority());
        
        /* This is to test if the list of tasks is reset to original order */
        updated = false;
        taskManager.reset();
        assertEquals(true, updated);
        assertEquals("task 1", display.get(0).getName());
        assertEquals("task 2", display.get(1).getName());
        assertEquals("task 3", display.get(2).getName());
        
        /* This is to test if the task is sorted successfully according to due date */
        updated = false;
        taskManager.addSort(TaskDisplayManager.SortCriteria.DUE_DATE, true);
        taskManager.apply();
        assertEquals(true, updated);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        assertEquals("27/03",  sdf.format(display.get(0).getDueDate().getTime()));
        assertEquals("28/03",  sdf.format(display.get(1).getDueDate().getTime()));
        assertEquals("28/03",  sdf.format(display.get(2).getDueDate().getTime()));
        
        /* This is to test if the task is sort successfully according to their duration*/
        taskManager.reset();
        updated = false;
        taskManager.addSort(TaskDisplayManager.SortCriteria.DURATION, true);
        taskManager.apply();
        assertEquals(true, updated);
        assertEquals("task 1", display.get(0).getName());
        assertEquals("task 3", display.get(1).getName());
        assertEquals("task 2", display.get(2).getName());
        
        /* This is to test if the task is sort successfully according to descending priorities first. If
         * they are of the same priority, then sort according to duration in ascending order*/
        taskManager.reset();
        updated = false;
        String errorMsg = "No errors.";
        try {
            taskManager.addSort(TaskDisplayManager.SortCriteria.PRIORITY, false);
            taskManager.addSort(TaskDisplayManager.SortCriteria.DURATION, true);
            taskManager.apply();
            
        } catch (Error e) {
            errorMsg = e.getMessage();
        }
        assertEquals("No errors.", errorMsg);    
        // task 1: H 1day
        // task 2: M 2days
        // task 3: M 1day
        assertEquals("task 1", display.get(0).getName());
        assertEquals("task 3", display.get(1).getName());
        assertEquals("task 2", display.get(2).getName());
        
        
        /* This is to test for error when duplicated criterias are added. */
        taskManager.reset();
        errorMsg = "No errors.";
        try {
            taskManager.addSort(TaskDisplayManager.SortCriteria.PRIORITY, false);
            taskManager.addSort(TaskDisplayManager.SortCriteria.PRIORITY, true);
        } catch (Error e) {
            errorMsg = e.getMessage();
        }
        assertEquals("Duplicated sorting criteria.", errorMsg);
    }

    public void update(Observable o, Object arg) {
        display = taskManager.getDisplayList();
        updated = true;
    }
}
