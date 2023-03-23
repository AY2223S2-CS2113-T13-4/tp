package seedu.apollo.command.module;

import seedu.apollo.calendar.Calendar;
import seedu.apollo.command.Command;
import seedu.apollo.exception.module.DuplicateModuleException;
import seedu.apollo.exception.module.InvalidModule;
import seedu.apollo.exception.module.LessonAddedException;
import seedu.apollo.exception.utils.IllegalCommandException;
import seedu.apollo.exception.utils.InvalidSaveFile;
import seedu.apollo.module.LessonType;
import seedu.apollo.module.Module;
import seedu.apollo.module.ModuleList;
import seedu.apollo.module.Timetable;
import seedu.apollo.storage.Storage;
import seedu.apollo.task.TaskList;
import seedu.apollo.ui.Ui;
import seedu.apollo.utils.LoggerInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static seedu.apollo.utils.LessonTypeUtil.determineLessonType;

public class ShowModuleCommand extends Command implements LoggerInterface {
    private static Logger logger = Logger.getLogger("ShowModuleCommand");
    private Module module;

    public ShowModuleCommand(String moduleCode, ModuleList allModules) throws InvalidModule {

        setUpLogger();
        assert (moduleCode != null) : "ShowModuleCommand: ModuleCode should not be null!";
        assert (allModules != null) : "ShowModuleCommand: Module list should not be null!";

        // check if module exists
        Module checkMod = allModules.findModule(moduleCode);
        if (checkMod == null) {
            throw new InvalidModule();
        }

        module = new Module(checkMod.getCode(), checkMod.getTitle(), checkMod.getModuleCredits());

    }

    /**
     * Sets up logger for ShowModuleCommand class.
     *
     * @throws IOException If logger file cannot be created.
     */
    @Override
    public void setUpLogger() {
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

    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage, ModuleList moduleList, ModuleList allModules,
                        Calendar calendar) {
        if (module != null) {
            Module referenceModule = allModules.findModule(module.getCode());
            ui.printShowModuleMessage(module, getLessonTypes(referenceModule));
        }
    }

    /**
     * Returns the available lesson type of the module.
     *
     * @param module The module being checked.
     * @return The lesson types available for this module.
     */
    public ArrayList<LessonType> getLessonTypes(Module module) {
        ArrayList<LessonType> lessonTypes = new ArrayList<>();
        for (Timetable timetable : module.getModuleTimetable()) {
            LessonType lessonType = determineLessonType(timetable.getLessonType());
            if (!lessonTypes.contains(lessonType) && lessonType != null) {
                lessonTypes.add(lessonType);
            }
        }
        return lessonTypes;
    }

}