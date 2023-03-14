package seedu.apollo.command.task;

import seedu.apollo.storage.Storage;
import seedu.apollo.ui.Ui;
import seedu.apollo.command.Command;
import seedu.apollo.module.ModuleList;
import seedu.apollo.task.TaskList;

import java.util.logging.Logger;

/**
 * Find Command class that shortlists Tasks that contain a given keyword.
 */
public class FindCommand extends Command {
    private static Logger logger = Logger.getLogger("FindCommand");

    protected String keyword;
    /**
     * Initialises the class with the given keyword to shortlist for.
     *
     * @param keyword User input of the keyword.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Shortlists and prints Tasks from the TaskList that contain the given keyword.
     *
     * @param taskList The existing TaskList.
     * @param ui       Prints shortlisted Tasks to user.
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage, ModuleList moduleList) {
        assert (ui != null & storage != null & taskList != null & keyword != null & moduleList != null) :
                "executing FindCommand";
        ui.printFoundList(taskList.findTasks(keyword));
    }

}
