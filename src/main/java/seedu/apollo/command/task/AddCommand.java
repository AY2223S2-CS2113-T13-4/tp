package seedu.apollo.command.task;

import seedu.apollo.calendar.Calendar;
import seedu.apollo.exception.task.DateOverException;
import seedu.apollo.module.CalendarModule;
import seedu.apollo.task.Task;
import seedu.apollo.ui.Parser;
import seedu.apollo.storage.Storage;
import seedu.apollo.command.Command;
import seedu.apollo.exception.task.DateOrderException;
import seedu.apollo.task.Deadline;
import seedu.apollo.task.Event;
import seedu.apollo.module.ModuleList;
import seedu.apollo.task.TaskList;
import seedu.apollo.ui.Ui;
import seedu.apollo.exception.task.InvalidDeadline;
import seedu.apollo.exception.task.InvalidEvent;
import seedu.apollo.task.ToDo;

import java.io.IOException;
import java.rmi.UnexpectedException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;

import static seedu.apollo.calendar.SemesterUtils.getWeekNumber;
import static seedu.apollo.ui.Parser.COMMAND_DEADLINE_WORD;
import static seedu.apollo.ui.Parser.COMMAND_EVENT_WORD;
import static seedu.apollo.ui.Parser.COMMAND_TODO_WORD;


/**
 * Add Command class that adds a Task to the existing TaskList.
 * Handles {@code todo}, {@code deadline}, and {@code event} commands.
 */
public class AddCommand extends Command {

    protected String command;
    protected String desc;
    protected String by;
    protected String from;
    protected String to;

    /**
     * Initialises the class with the type and description of the task given in the command.
     *
     * @param command Type of task being added (ToDo, Deadline, or Event).
     * @param param   Description of task given by user (including date(s) for Deadline, Event).
     * @throws InvalidDeadline     If the Deadline being added has the wrong format.
     * @throws InvalidEvent        If the Event being added has the wrong format.
     * @throws UnexpectedException If the command word cannot be understood.
     */
    public AddCommand(String command, String param) throws InvalidDeadline, InvalidEvent, UnexpectedException {
        super("AddCommand");
        this.command = command;
        assert (command.equals(COMMAND_TODO_WORD) | command.equals(COMMAND_DEADLINE_WORD) |
                command.equals(COMMAND_EVENT_WORD)) : "AddCommand: Invalid Add Command";
        switch (command) {
        case COMMAND_TODO_WORD:
            this.desc = param.trim();
            break;
        case COMMAND_DEADLINE_WORD:
            String[] paramAndBy = Parser.parseDeadline(param.trim());
            this.desc = paramAndBy[0];
            this.by = paramAndBy[1];
            break;
        case COMMAND_EVENT_WORD:
            String[] paramAndFromTo = Parser.parseEvent(param.trim());
            this.desc = paramAndFromTo[0];
            this.from = paramAndFromTo[1];
            this.to = paramAndFromTo[2];
            break;
        default:
            throw new UnexpectedException("Adding Task");
        }
    }

    /**
     * Executes adding a Task to the TaskList and updating the hard disk.
     *
     * @param taskList The TaskList to be added to.
     * @param ui       Prints success or error message to user.
     * @param storage  Gets updated after the Task has been added.
     * @throws UnexpectedException If the command stored is not recognised.
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage, ModuleList moduleList, ModuleList allModules,
                        Calendar calendar)
            throws UnexpectedException {

        int initialSize = taskList.size();
        try {
            addTask(taskList, calendar, ui);
        } catch (DateTimeParseException e) {
            if(e.getMessage().contains("Invalid date")) {
                ui.dateNotWithinCalender();
            } else {
                ui.printInvalidDateTime();
            }
            return;
        } catch (DateOverException e) {
            ui.printDateOverException(e);
            return;
        } catch (DateOrderException e) {
            ui.printDateOrderException();
            return;
        }

        assert (taskList.size() > initialSize) : "AddCommand : Task not added successfully";
        ui.printAddMessage(taskList.get(taskList.size() - 1));

        try {
            storage.updateTask(taskList);
        } catch (IOException e) {
            ui.printErrorForIO();
        }
    }

    /**
     * Adds a Task to the TaskList based on data in the class.
     *
     * @param taskList The TaskList to be added to.
     * @param calendar The lesson schedule of the user.
     * @param ui       For printing messages in event of clashes.
     * @throws DateTimeParseException If any date is entered in the wrong format.
     * @throws DateOverException      If any date of the task occurs before the current date.
     * @throws DateOrderException     If an Event's end date occurs before its start date.
     * @throws UnexpectedException    If the command stored is not recognised.
     */
    private void addTask(TaskList taskList, Calendar calendar, Ui ui)
            throws DateTimeParseException, DateOverException, DateOrderException, UnexpectedException {
        switch (command) {
        case COMMAND_TODO_WORD:
            ToDo todo = new ToDo(desc.trim());
            taskList.add(todo);
            checkForDeadlineTask(ui, todo);
            break;
        case COMMAND_DEADLINE_WORD:
            Deadline deadline = new Deadline(desc.trim(), by.trim());
            warnDeadlineClash(ui, taskList, calendar, deadline.getByDate());
            taskList.add(deadline);
            break;
        case COMMAND_EVENT_WORD:
            Event event = new Event(desc.trim(), from.trim(), to.trim());
            if (isClashingEvent(taskList, event.getToDate(), event.getFromDate())) {
                ui.printClashingEventMessage();
            }
            warnEventModuleClash(ui, calendar, event);
            taskList.add(event);
            break;
        default:
            throw new UnexpectedException("Adding Task");
        }
    }

    /**
     * Checks if the user has added a deadline task that is similar to the format of a deadline task.
     * @param ui    For printing warning message.
     * @param todo  The task being added.
     */
    private static void checkForDeadlineTask(Ui ui, ToDo todo) {
        if (todo.getDescription().matches(".*\\bby\\b.*")||
                todo.getDescription().matches(".*(/by|\\s+by)\\b.*")||
                todo.getDescription().matches(".*\\bdue\\b.*") ||
                todo.getDescription().matches(".*\\bduedate\\b.*")){
            ui.deadlineSuggestion();
        }
    }


    /**
     * Prints warning message to user with all tasks and lessons that clash with the deadline.
     * Will not print anything if there are no clashes.
     *
     * @param ui       For printing warning message.
     * @param taskList Existing tasks.
     * @param calendar Existing lessons.
     * @param by       Due date of the deadline being added.
     */
    private void warnDeadlineClash(Ui ui, TaskList taskList, Calendar calendar, LocalDateTime by) {
        TaskList clashTasks = taskList.getTasksOnDate(by.toLocalDate());
        DayOfWeek day = by.getDayOfWeek();
        int dayNum = day.getValue() - 1;
        int week = getWeekNumber(by.toLocalDate());
        ArrayList<CalendarModule> clashLessons = calendar.getLessonsForDay(week, dayNum);
        clashTasks.sortTaskByDay();
        ui.printClashingDeadlineMessage(clashTasks, clashLessons);
    }

    /**
     * Checks if an event user wants to add clashes with existing events.
     *
     * @param taskList The ArrayList containing the tasks.
     * @param from     The time that event starts.
     * @param to       The time that event ends.
     * @return {@code true} if there is a clash, {@code false} otherwise.
     */
    private boolean isClashingEvent(TaskList taskList, LocalDateTime from, LocalDateTime to) {
        for (Task task : taskList) {
            if (task instanceof Event) {
                Event event = (Event) task;
                if ((event.getFromDate().isAfter(to) || event.getToDate().isBefore(from))) {
                    continue;
                }

                return true;
            }
        }
        return false;
    }

    /**
     * Checks and warns if an event user wants to add clashes with existing lessons.
     *
     * @param ui       For printing warning message.
     * @param calendar The timetable of lessons stored in an ArrayList.
     * @param event    The event to be added.
     */
    private void warnEventModuleClash(Ui ui, Calendar calendar, Event event) {
        LocalDateTime eventStart = event.getFromDate();
        LocalDateTime eventEnd = event.getToDate();
        LocalDateTime currentDay = eventStart;

        do {
            DayOfWeek currentDayOfWeek = currentDay.getDayOfWeek();
            int currentDayIndex = currentDayOfWeek.getValue() - 1;
            String currentDayString = currentDay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            if (isClashingEventModule(calendar.get(currentDayIndex), eventStart, eventEnd, currentDayString)) {
                ui.printClashingEventModuleMessage();
                return;
            }

            currentDay = currentDay.plusDays(1);
        } while (!currentDay.isAfter(eventEnd));
    }

    /**
     * Checks if an event user wants to add clashes with existing lessons.
     *
     * @param calendarModule The ArrayList containing the lessons.
     * @param eventStart     The time that event starts.
     * @param eventEnd       The time that event ends.
     * @param currentDate    The date of which being checked.
     * @return true if there is a clash, false otherwise.
     */
    private boolean isClashingEventModule(ArrayList<CalendarModule> calendarModule, LocalDateTime eventStart,
                                          LocalDateTime eventEnd, String currentDate) {

        DateTimeFormatter formatterFull = DateTimeFormatter.ofPattern("dd/MM/yyyy HHmm");
        DateTimeFormatter formatterPartial = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (CalendarModule module : calendarModule) {
            if (module.getSchedule() == null) {
                continue;
            }

            LocalDate date = LocalDate.parse(currentDate, formatterPartial);

            int currentWeek = getWeekNumber(date);

            ArrayList<Integer> weeks = module.getSchedule().getWeeks();

            if (!weeks.contains(currentWeek)) {
                continue;
            }

            String moduleStartString = currentDate + " " + module.getSchedule().getStartTime();
            String moduleEndString = currentDate + " " + module.getSchedule().getEndTime();
            LocalDateTime lessonStart = LocalDateTime.parse(moduleStartString, formatterFull);
            LocalDateTime lessonEnd = LocalDateTime.parse(moduleEndString, formatterFull);

            if (isEventLessonClashing(eventStart, eventEnd, lessonStart, lessonEnd) &&
                    isDuringSemester(eventStart,eventEnd)) {

                return true;
            }
        }
        return false;
    }


    /**
     * Checks if event occurs during the academic semester 1 or 2
     * @param eventStart The start time of the event
     * @param eventEnd  The end time of the event
     * @return {@code true} If event occurs during semester, {@code false} otherwise.
     */
    private boolean isDuringSemester(LocalDateTime eventStart,LocalDateTime eventEnd) {
        LocalDateTime startSemesterTwo = LocalDateTime.of(2023, 1, 9, 0, 0);
        LocalDateTime endSemesterTwo= LocalDateTime.of(2023,5,7,0,0);
        LocalDateTime startSemesterOne = LocalDateTime.of(2023,8,14,0,0);
        LocalDateTime endSemesterOne = LocalDateTime.of(2023,11,10,0,0);
        return (!startSemesterTwo.isAfter(eventEnd) && !endSemesterTwo.isBefore(eventStart))
                || (!startSemesterOne.isAfter(eventEnd) && !endSemesterOne.isBefore(eventStart));
    }



    /**
     * Checks if an event and lesson overlap.
     *
     * @param eventStart The start time of the event.
     * @param eventEnd   The end time of the event.
     * @param lessonStart The start time of the lesson.
     * @param lessonEnd   The end time of the lesson.
     * @return true if there is a clash, false otherwise.
     */
    private boolean isEventLessonClashing(LocalDateTime eventStart, LocalDateTime eventEnd, LocalDateTime lessonStart,
                                          LocalDateTime lessonEnd) {

        if (eventStart.isEqual(lessonStart) && eventEnd.isEqual(lessonEnd)) {
            return true;
        }

        if (eventStart.isBefore(lessonStart) && eventEnd.isAfter(lessonEnd)) {
            return true;
        }

        if (eventStart.isAfter(lessonStart) && eventEnd.isBefore(lessonEnd)) {
            return true;
        }

        if (eventStart.isBefore(lessonStart) && eventEnd.isBefore(lessonEnd)) {
            return true;
        }

        return eventStart.isAfter(lessonStart) && eventEnd.isAfter(lessonEnd);
    }

}
