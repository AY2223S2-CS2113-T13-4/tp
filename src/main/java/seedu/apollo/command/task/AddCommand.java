package seedu.apollo.command.task;

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

import java.io.File;
import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static seedu.apollo.ui.Parser.COMMAND_DEADLINE_WORD;
import static seedu.apollo.ui.Parser.COMMAND_EVENT_WORD;
import static seedu.apollo.ui.Parser.COMMAND_TODO_WORD;

/**
 * Add Command class that adds a Task to the existing TaskList.
 * Handles {@code todo}, {@code deadline}, and {@code event} commands.
 */
public class AddCommand extends Command {

    private static Logger logger = Logger.getLogger("AddCommand");

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
        this.command = command;
        assert (command.equals(COMMAND_TODO_WORD) | command.equals(COMMAND_DEADLINE_WORD) |
                command.equals(COMMAND_EVENT_WORD)) : "AddCommand: Invalid Add Command";
        assert param != null : "AddCommand: param should not be null!";
        switch (command) {
        case COMMAND_TODO_WORD:
            this.desc = param;
            break;
        case COMMAND_DEADLINE_WORD:
            String[] paramAndBy = Parser.parseDeadline(param);
            this.desc = paramAndBy[0];
            this.by = paramAndBy[1];
            break;
        case COMMAND_EVENT_WORD:
            String[] paramAndFromTo = Parser.parseEvent(param);
            this.desc = paramAndFromTo[0];
            this.from = paramAndFromTo[1];
            this.to = paramAndFromTo[2];
            break;
        default:
            throw new UnexpectedException("Adding Task");
        }
        AddCommand.setUpLogger();
    }

    /**
     * Sets up logger for AddCommand class.
     *
     * @throws IOException If logger file cannot be created.
     */
    public static void setUpLogger() {
        LogManager.getLogManager().reset();
        logger.setLevel(Level.ALL);
        ConsoleHandler logConsole = new ConsoleHandler();
        logConsole.setLevel(Level.SEVERE);
        logger.addHandler(logConsole);
        try {

            if (!new File("apollo.log").exists()) {
                new File("apollo.log").createNewFile();
            }

            FileHandler logFile = new FileHandler("apollo.log", true);
            logFile.setLevel(Level.FINE);
            logger.addHandler(logFile);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "File logger not working.", e);
        }

    }

    /**
     * Executes the adding of a Task to the TaskList based on data in the class.
     *
     * @param taskList The TaskList to be added to.
     * @param ui       Prints success or error message to user.
     * @param storage  Gets updated after the Task has been added.
     * @throws UnexpectedException If the command stored is not recognised.
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage, ModuleList moduleList) throws UnexpectedException {
        assert (ui != null & storage != null & taskList != null & moduleList != null) :
                "executing AddCommand";
        
        switch (command) {
        case COMMAND_TODO_WORD:
            taskList.add(new ToDo(desc));
            break;
        case COMMAND_DEADLINE_WORD:
            taskList.add(new Deadline(desc, by));
            break;
        case COMMAND_EVENT_WORD:
            try {
                taskList.add(new Event(desc, from, to));
            } catch (DateOrderException e) {
                ui.printDateOrderException();
                return;
            }
            break;
        default:
            throw new UnexpectedException("Adding Task");
        }
        assert taskList.size() > 0;
        ui.printAddMessage(taskList.get(taskList.size() - 1));
        try {
            storage.updateTask(taskList);
        } catch (IOException e) {
            ui.printErrorForIO();
        }
    }

}
