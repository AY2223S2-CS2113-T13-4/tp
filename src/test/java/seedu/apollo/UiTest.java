package seedu.apollo;

import org.junit.jupiter.api.Test;
import seedu.apollo.task.TaskList;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UiTest {

    @Test
    void printList_normalInput_noExceptionThrown() throws IOException {
        Storage storage = new Storage("test.txt", "moduleData.txt", "/data/data.json");
        Ui ui = new Ui();
        TaskList taskList = storage.loadTaskList(ui);
        assertDoesNotThrow(() -> ui.printList(taskList));
    }

    @Test
    void printList_emptyInput_noExceptionThrown() throws IOException {
        Storage storage = new Storage("test.txt");
        Ui ui = new Ui();
        TaskList taskList = new TaskList();
        assertDoesNotThrow(() -> ui.printList(taskList));
    }

    @Test
    void printFoundList_normalInput_noExceptionThrown() throws IOException {
        Storage storage = new Storage("test.txt");
        Ui ui = new Ui();
        TaskList taskList = storage.loadTaskList(ui);
        assertDoesNotThrow(() -> ui.printFoundList(taskList));
    }

    @Test
    void printFoundList_emptyInput_noExceptionThrown() throws IOException {
        Storage storage = new Storage("test.txt");
        Ui ui = new Ui();
        TaskList taskList = new TaskList();
        assertDoesNotThrow(() -> ui.printFoundList(taskList));
    }

}
