package seedu.apollo.command.module;

import seedu.apollo.storage.Storage;
import seedu.apollo.ui.Ui;
import seedu.apollo.command.Command;
import seedu.apollo.exception.module.InvalidModule;
import seedu.apollo.module.Module;
import seedu.apollo.module.ModuleList;
import seedu.apollo.task.TaskList;

import java.io.IOException;


public class AddModuleCommand extends Command {

    private Module module;

    /**
     * Constructor for AddModuleCommand.
     *
     * @param moduleCode The module code of the module to be added.
     * @param allModules The list of all modules.
     * @throws InvalidModule If the module code is invalid.
     */
    public AddModuleCommand(String moduleCode, ModuleList allModules) throws InvalidModule {
        Module toAdd = allModules.findModule(moduleCode);
        if (toAdd == null) {
            throw new InvalidModule();
        }

        module = toAdd;
    }

    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage, ModuleList moduleList) {
        try {
            if (module != null) {
                moduleList.add(module);
                moduleList.sortModules();
                ui.printAddModuleMessage(module);
            }
            storage.updateModule(moduleList);
        } catch (IOException e) {
            ui.printErrorForIO();
        }
    }
}