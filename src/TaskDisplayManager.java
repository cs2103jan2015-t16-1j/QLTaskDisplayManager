import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.util.Observable;

public class TaskDisplayManager extends Observable {
    
    private static class Sort {
        protected SortCriteria type;
        protected boolean isAsc;
    }
    
    private static class Search {
        protected SearchCriteria type;
        protected String keyword;
    }
    
    private List<Task> _taskList;
    private List<Task> _displayList;
    private List<Sort> _sortCriterias;
    private List<Search> _searchCriterias;
    
    static enum SortCriteria {
        DUE_DATE, DURATION, PRIORITY
    }
    
    static enum SearchCriteria {
        NAME, START_DATE, DUE_DATE, PRIORITY, COMPLETED, OVERDUE
    }
    
    public TaskDisplayManager() {
        _sortCriterias = new LinkedList<Sort>();
        _searchCriterias = new LinkedList<Search>();
        _taskList = new LinkedList<Task>();
        _displayList = new LinkedList<Task>();
        
    }
    
    public void updateTaskList(List<Task> taskList) {
        _taskList = new LinkedList<Task>(taskList);
        apply();
    }
    
    public List<Task> getDisplayList() {
        return _displayList;
    }
    
    public void addSort(SortCriteria type, boolean isAsc) {
        for (int i = 0; i < _sortCriterias.size(); ++i) {
            if (type == _sortCriterias.get(i).type) {
                throw new Error("Duplicated sorting criteria.");
            }
        }
        Sort item = new Sort();
        item.type = type;
        item.isAsc = isAsc;
        _sortCriterias.add(item);
    }
    
    public void apply() {
        sortAndFilter();
        setChanged();
        notifyObservers();
    }
    
    public void addSearch(SearchCriteria type, String keywords) {
        Search item = new Search();
        item.type = type;
        item.keyword = keywords;
        _searchCriterias.add(item);
    }
    
    private void sortAndFilter() {
        _displayList = new LinkedList<Task>(_taskList);
        Collections.sort(_displayList, new TaskComparator());
    }
    
    private class TaskComparator implements Comparator<Task> {
        public int compare(Task t1, Task t2) {
            for (int i = 0; i < _sortCriterias.size(); ++i) {
                Sort current = _sortCriterias.get(i);
                switch (current.type) {
                case DUE_DATE:
                    if (!t1.getDueDate().equals(t2.getDueDate())) {
                        int result = t1.getDueDate().compareTo(t2.getDueDate());
                        if (current.isAsc) {
                            return result;
                        }
                        else {
                            return -result;
                        }
                    }
                    break;
                case PRIORITY:
                    if (t1.getPriorityInt() != t2.getPriorityInt()) {
                        int result = t1.getPriorityInt() - t2.getPriorityInt();
                        if (current.isAsc) {
                            return result;
                        }
                        else {
                            return -result;
                        }
                    }
                    break;
                case DURATION:
                    long duration1 = 0;
                    long duration2 = 0;
                    if (t1.getDueDate() != null && t1.getStartDate() != null) {
                        duration1 = t1.getDueDate().getTimeInMillis() - t1.getStartDate().getTimeInMillis();
                    }
                    if (t2.getDueDate() != null && t2.getStartDate() != null) {
                        duration2 = t2.getDueDate().getTimeInMillis() - t2.getStartDate().getTimeInMillis();
                    }
                    if (duration1 != duration2) {
                        int result = 1;
                        if (duration1 < duration2) {
                            result = -1;
                        }
                        if (current.isAsc) {
                            return result;
                        } else {
                            return -result;
                        }
                    }
                    break;
                default:
                    break;
                }
            }
            return 0;
        }
    }
    
    public void reset() {
        _displayList = new LinkedList<Task>(_taskList);
        _sortCriterias.clear();
        _searchCriterias.clear();
        setChanged();
        notifyObservers();        
    }
    
    
    
    
    
    
    
    
}
