package seedu.apollo.command.module;

import seedu.apollo.calendar.Calendar;
import seedu.apollo.exception.module.DuplicateModuleException;
import seedu.apollo.exception.module.LessonAddedException;
import seedu.apollo.exception.utils.IllegalCommandException;
import seedu.apollo.exception.utils.InvalidSaveFile;
import seedu.apollo.module.CalendarModule;
import seedu.apollo.module.LessonType;
import seedu.apollo.module.Module;
import seedu.apollo.module.ModuleList;
import seedu.apollo.module.Timetable;
import seedu.apollo.storage.Storage;
import seedu.apollo.ui.Ui;
import seedu.apollo.command.Command;
import seedu.apollo.exception.module.InvalidModule;
import seedu.apollo.task.TaskList;
import seedu.apollo.utils.LoggerInterface;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static seedu.apollo.utils.DayTypeUtil.determineDay;
import static seedu.apollo.utils.LessonTypeUtil.determineLessonType;


public class AddModuleCommand extends Command implements LoggerInterface {
    private static Logger logger = Logger.getLogger("AddModuleCommand");
    private Module module;
    private String[] args;

    /**
     * Constructor for AddModuleCommand.
     *
     * @param param The module code of the module to be added.
     * @param allModules The list of all modules.
     * @throws InvalidModule If the module code is invalid.
     */
    public AddModuleCommand(String param, ModuleList allModules) throws InvalidModule, IllegalCommandException {

        setUpLogger();
        assert (param != null) : "AddModuleCommand: Params should not be null!";
        assert (allModules != null) : "AddModuleCommand: Module list should not be null!";

        args = param.split("\\s+");

        if (args.length != 3 && args.length != 1) {
            throw new IllegalCommandException();
        }

        String moduleCode = args[0];
        Module toAdd = allModules.findModule(moduleCode);

        if (toAdd == null) {

            throw new InvalidModule();
        }

        module = new Module(toAdd.getCode(), toAdd.getTitle(), toAdd.getModuleCredits());

    }

    /**
     * Sets up logger for AddModuleCommand class.
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

    /**
     * Checks if the module is already in the module list.
     *
     * @param moduleList The list of modules.
     * @param module The module to be checked.
     * @return True if the module is already in the list of modules.
     */
    public boolean isAdded(ModuleList moduleList, Module module) {
        for (Module mod: moduleList) {
            if (mod.getCode().equals(module.getCode())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage, ModuleList moduleList, ModuleList allModules,
                        Calendar calendar) {
        try {
            if (args.length == 3) {
                handleMultiCommand(moduleList, allModules, args, ui, calendar);
                ui.printClassAddedMessage(args[0].toUpperCase(), getCommand(args[1]), args[2]);
            } else {
                if (isAdded(moduleList, module)) {
                    throw new DuplicateModuleException();
                }

                if (module != null) {
                    moduleList.add(module);
                    moduleList.sortModules();
                    Module referenceModule = allModules.findModule(module.getCode());
                    ui.printAddModuleMessage(module, moduleList, getLessonTypes(referenceModule));

                }
            }

            storage.updateModule(moduleList, calendar);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO Exception", e);
            ui.printErrorForIO();
        } catch (DuplicateModuleException e) {
            ui.printDuplicateModule(module);
        } catch (IllegalCommandException e) {
            ui.printInvalidCommand();
        } catch (ClassNotFoundException e) {
            ui.printInvalidLessonType();
        } catch (LessonAddedException e) {
            ui.printLessonExists();
        } catch (InvalidSaveFile e) {
            ui.printErrorForIO();
        }
    }

    /**
     * Handles the case where the user wants to add a class to a module.
     *
     * @param moduleList The list of modules.
     * @param allModules Module backend data.
     * @param args The arguments of the command.
     * @throws IllegalCommandException If the command is invalid.
     * @throws ClassNotFoundException If the lesson type is invalid.
     * @throws LessonAddedException If the lesson already exists.
     */
    private void handleMultiCommand(ModuleList moduleList, ModuleList allModules, String[] args, Ui ui,
                                    Calendar calendar) throws IllegalCommandException, ClassNotFoundException,
            LessonAddedException {

        LessonType lessonType = this.getCommand(args[1]);
        Module searchModule = null;
        for (Module module: allModules){
            if (module.getCode().equalsIgnoreCase(this.module.getCode())){
                searchModule = module;
                break;
            }
        }

        if (this.isAdded(moduleList, module)) {
            int index = 0;
            for (Module module: moduleList) {
                if (module.getCode().equals(this.module.getCode())){
                    this.module.setTimetable(module.getModuleTimetable());
                    break;
                }
                index++;
            }
            module.setTimetable(moduleList.get(index).getModuleTimetable());
            if (module.hasLessonType(lessonType)){
                throw new LessonAddedException();
            }

            addTimetable(searchModule, lessonType, args[2], ui, calendar);
            moduleList.get(index).setTimetable(module.getModuleTimetable());
        } else {
            module.createNewTimeTable();
            addTimetable(searchModule, lessonType, args[2], ui, calendar);
            moduleList.add(module);
        }
    }

    private void addTimetable(Module searchModule, LessonType lessonType, String args, Ui ui, Calendar calendar)
            throws ClassNotFoundException {
        Boolean isFound = false;
        ArrayList<Timetable> listCopy = new ArrayList<>(searchModule.getModuleTimetable());
        for (Timetable timetable: listCopy){
            LessonType searchLessonType = determineLessonType(timetable.getLessonType());
            if (searchLessonType.equals(lessonType) && timetable.getClassnumber().equals(args)){

                if (module.getModuleTimetable() == null){
                    module.createNewTimeTable();
                }
                module.getModuleTimetable().add(timetable);
                checkClashingLesson(calendar, timetable, ui);
                isFound = true;
            }
        }

        if (!isFound){
            throw new ClassNotFoundException();
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

    /**
     * Checks if the lesson clashes with another lesson.
     *
     * @param calendar The calendar of the user containing timetable information.
     * @param timetable The timetable of the lesson to be checked.
     * @param ui The ui of the user for message printing.
     */
    private void checkClashingLesson(Calendar calendar, Timetable timetable, Ui ui) {
        String day = timetable.getDay();
        int index = determineDay(day);

        if (index == -1) {
            return;
        }

        if (calendar.get(index).size() == 0) {
            return;
        }

        for (CalendarModule lessonModule: calendar.get(index)) {
            Timetable schedule = lessonModule.getSchedule();
            if (isLessonClashing(schedule, timetable)) {
                ui.printClashingLesson();
                break;
            }
        }
    }

    /**
     * Checks if a lesson clashes with another lesson.
     *
     * @param schedule The lesson to be checked.
     * @param timetable The lesson to be checked against.
     * @return True if the timetable clashes with another timetable.
     */
    private boolean isLessonClashing(Timetable schedule, Timetable timetable) {

        SimpleDateFormat format = new SimpleDateFormat("HHmm");
        try {
            Date start1 = format.parse(schedule.getStartTime());
            Date start2 = format.parse(timetable.getStartTime());
            Date end1 = format.parse(schedule.getEndTime());
            Date end2 = format.parse(timetable.getEndTime());

            if (start1.equals(start2) || end1.equals(end2)) {
                return true;
            }

            if (start1.after(start2) && start1.before(end2)) {
                return true;
            }

            if (start2.after(start1) && start2.before(end1)) {
                return true;
            }

        } catch (ParseException e) {
            return false;
        }
        return false;
    }


}
