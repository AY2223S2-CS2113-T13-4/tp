package seedu.apollo.task;

import seedu.apollo.exception.task.EventEventClashException;
import seedu.apollo.exception.module.EventModuleClashException;
import seedu.apollo.exception.task.DateOverException;
import seedu.apollo.exception.task.DateOrderException;
import seedu.apollo.module.Module;
import seedu.apollo.module.ModuleList;
import seedu.apollo.module.Timetable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Events are a type of Task that have a set start date and end date,
 * along with the default description and status.
 */
public class Event extends Task {
    public static final String EVENT_LABEL = "E";
    protected LocalDateTime from;
    protected LocalDateTime to;


    /**
     * Initialises as in Task, with added parsing for start and end dates.
     * If parsing is not possible, save date(s) as String(s).
     *
     * @param description String describing the Task.
     * @param fromString  String describing the start date.
     * @param toString    String describing the end date.
     * @throws DateTimeParseException If either date is not entered in right format.
     * @throws DateOrderException     If end date occurs before the start date.
     * @throws DateOverException      If start date occurs before the current date.
     */
    public Event(String description, String fromString, String toString, TaskList taskList, ModuleList moduleList)
            throws DateTimeParseException, DateOrderException, DateOverException,
            EventEventClashException, EventModuleClashException {
        super(description);
        this.from = LocalDateTime.parse(fromString);
        this.to = LocalDateTime.parse(toString);

        if (from.isAfter(to)) {
            throw new DateOrderException();
        }
        if (clashTaskList(to, from, taskList)) {
            throw new EventEventClashException();
        }
        if (clashModuleList(to, from, moduleList)) {

            throw new EventModuleClashException();
        }


        if (from.isBefore(LocalDateTime.now())) {
            throw new DateOverException(getType(), description, null, from, to);
        }
    }

    public boolean clashTaskList(LocalDateTime to, LocalDateTime from, TaskList tasksList) {
        for (Task task : tasksList) {
            if (task instanceof Event) {
                Event event = (Event) task;
                if (from.isBefore(event.to) || to.isAfter(event.from)) {
                    return true; // Overlap detected
                }
            }
        }
        return false;
    }

    public boolean clashModuleList(LocalDateTime to, LocalDateTime from, ModuleList moduleList) {
        for (Module module : moduleList) {
            ArrayList<Timetable> timetableList = module.getModuleTimetable();
            for (Timetable timetable : timetableList) {
                LocalDateTime start = LocalDateTime.parse(timetable.getStartTime());
                LocalDateTime end = LocalDateTime.parse(timetable.getEndTime());
                if (from.isBefore(end) || to.isAfter(start)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get a String describing the start date of the Event.
     *
     * @param pattern Desired format for String after parsing.
     * @return Parsed start date.
     */
    public String getFrom(DateTimeFormatter pattern) {
        return from.format(pattern);
    }

    /**
     * Get a String describing the end date of the Event.
     *
     * @param pattern Desired format for String after parsing.
     * @return Parsed end date.
     */
    public String getTo(DateTimeFormatter pattern) {
        return to.format(pattern);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "event";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isOnDate(LocalDate date) {
        boolean fromExists = (from != null);
        boolean toExists = (to != null);
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (fromExists) {
            fromDate = from.toLocalDate();
        }
        if (toExists) {
            toDate = to.toLocalDate();
        }
        boolean isOnFrom = fromExists && date.isEqual(fromDate);
        boolean isOnTo = toExists && date.isEqual(toDate);
        boolean isBetween = fromExists && toExists && date.isAfter(fromDate) && date.isBefore(toDate);

        return isOnFrom || isOnTo || isBetween;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[" + EVENT_LABEL + "][" + getStatus() + "] " + description +
                " (from: " + getFrom(printPattern) + " to: " + getTo(printPattern) + ")";
    }
}
