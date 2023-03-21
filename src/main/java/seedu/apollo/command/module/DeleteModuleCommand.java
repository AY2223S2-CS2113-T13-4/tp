package seedu.apollo.command.module;
import seedu.apollo.calendar.Calendar;
import seedu.apollo.exception.module.ClassNotFoundException;
import seedu.apollo.exception.utils.IllegalCommandException;
import seedu.apollo.exception.utils.InvalidSaveFile;
import seedu.apollo.module.LessonType;
import seedu.apollo.module.Timetable;
import seedu.apollo.storage.Storage;
import seedu.apollo.command.Command;
import seedu.apollo.exception.module.ModuleNotFoundException;
import seedu.apollo.module.Module;
import seedu.apollo.module.ModuleList;
import seedu.apollo.ui.Ui;
import seedu.apollo.task.TaskList;
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

/**
 * For {@code delmod} command.
 * Delete Module Command class that finds the module using moduleCode and removes it from the ModuleList
 */
public class DeleteModuleCommand extends Command implements LoggerInterface {
    private static Logger logger = Logger.getLogger("DeleteModuleCommand");
    protected String[] args;

    public DeleteModuleCommand(String params) throws IllegalCommandException {
        String[] args = params.split("\\s+");

        if (args.length != 1 && args.length != 3) {
            throw new IllegalCommandException();
        }
        this.args = args;
        this.setUpLogger();
    }

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
                        Calendar calendar){

        try {
            if (args.length == 3) {
                handleMultiCommand(moduleList, ui, storage);
            } else {
                // module code is the only argument
                String moduleCode = args[0];
                Module toDelete = moduleList.findModule(moduleCode);
                if (toDelete == null) {
                    throw new ModuleNotFoundException();
                }
                moduleList.remove(toDelete);
                ui.printModuleDeleteMessage(moduleCode, moduleList);
            }

            storage.updateModule(moduleList, calendar);

        } catch (ModuleNotFoundException e) {
            ui.printUnsuccessfulModuleDelete(args[0]);
            ui.printTotalModularCredits(moduleList);
        } catch (IOException | InvalidSaveFile e) {
            ui.printErrorForIO();
        }
    }

    private void handleMultiCommand(ModuleList moduleList, Ui ui, Storage storage) throws ModuleNotFoundException {
        String moduleCode = args[0];
        String command = args[1];
        String lessonNumber = args[2];

        try {
            LessonType lessonType = getCommand(command);
            Module toDelete = moduleList.findModule(moduleCode);

            if (toDelete == null) {
                throw new ModuleNotFoundException();
            }

            this.deleteTimetable(toDelete, lessonType, lessonNumber);
            ui.printModuleLessonDeleteMessage(moduleCode, lessonType, lessonNumber);
        } catch (IllegalCommandException e) {
            ui.printInvalidCommand();
        } catch (ClassNotFoundException e) {
            ui.printClassNotAdded();
        }
    }

    private void deleteTimetable(Module module, LessonType lessonType, String lessonNumber)
            throws ClassNotFoundException {

        Boolean isFound = false;
        ArrayList<Timetable> copyList = new ArrayList<>(module.getModuleTimetable());

        //searches for lesson of specified type and number
        for (Timetable timetable :copyList) {
            String classNumber = timetable.getClassnumber();
            String type = timetable.getLessonType();
            LessonType lessonType1 = determineLessonType(type);

            if (lessonType1 == lessonType && classNumber.equals(lessonNumber)) {
                module.getModuleTimetable().remove(timetable);
                isFound = true;
            }
        }

        // if the lesson is not found, throws an exception
        if (!isFound) {
            throw new ClassNotFoundException();
        }
    }

    private LessonType getCommand(String arg) throws IllegalCommandException {
        switch (arg) {
        case "-lec":
            return LessonType.LECTURE;
        case "-plec":
            return LessonType.PACKAGED_LECTURE;
        case "-st":
            return LessonType.SECTIONAL_TEACHING;
        case "-dlec":
            return LessonType.DESIGN_LECTURE;
        case "-tut":
            return LessonType.TUTORIAL;
        case "-ptut":
            return LessonType.PACKAGED_TUTORIAL;
        case "-rcit":
            return LessonType.RECITATION;
        case "-lab":
            return LessonType.LABORATORY;
        case "-ws":
            return LessonType.WORKSHOP;
        case "-smc":
            return LessonType.SEMINAR_STYLE_MODULE_CLASS;
        case "-mp":
            return LessonType.MINI_PROJECT;
        case "-tt2":
            return LessonType.TUTORIAL_TYPE_2;
        default:
            throw new IllegalCommandException();
        }
    }

}
