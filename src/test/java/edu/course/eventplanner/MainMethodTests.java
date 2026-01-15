package edu.course.eventplanner;

import edu.course.eventplanner.service.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainMethodTests {

    private ByteArrayOutputStream out;
    @BeforeEach
    void setup() {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    private Scanner scanOf(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        return new Scanner(System.in);
    }

    @Test
    @Order(1)
    void testSelectVenueWithNoGuestsLoaded() {
        Main.selectVenue(scanOf(""));
        assertTrue(out.toString().contains("No guests loaded; please upload guestlist first."));
    }

    @Test
    @Order(2)
    void testGenerateSeatingChartWithNoGuestsLoaded() {
        Main.generateSeatingChart();

        assertTrue(out.toString().contains("No guests loaded; please upload guestlist first."));
        assertNull(Main.seating);
    }

    @Test
    @Order(3)
    void testLoadSampleGuests() {
        Main.loadSampleGuests(scanOf("50\n"));

        assertEquals(50, Main.guestListManager.getGuestCount());
        assertTrue(out.toString().contains("50 guests loaded."));
        assertFalse(out.toString().contains("Invalid entry; returning to menu."));
    }

    @Test
    @Order(4)
    void testGenerateSeatingChartWithNoVenueChosen() {
        Main.generateSeatingChart();

        assertTrue(out.toString().contains("No venue selected; please select a venue first."));
        assertNull(Main.seating);
    }

    @Test
    @Order(5)
    void testSelectVenueWithNoVenuesLoaded() {
        Main.selectVenue(scanOf(""));

        assertTrue(out.toString().contains("No venues loaded; please load venues first."));
    }

    @Test
    @Order(6)
    void testLoadSampleVenues() {
        Main.loadSampleVenues();

        assertNotNull(Main.venueSelector);
        assertTrue(out.toString().contains("Venues loaded."));
    }

    @Test
    @Order(7)
    void testSelectVenueInsufficientBudget() {
        Main.selectVenue(scanOf("100\n2\n"));

        assertTrue(out.toString().contains("Sorry, no venues found that fit your budget. \nAvailable venues with high enough capacity: \n"));
        assertTrue(out.toString().contains("Which venue would you like to use?"));
        assertTrue(out.toString().contains("0)\nVenue: Garden Hall"));
        assertTrue(out.toString().contains("3)\nVenue: Museum Atrium"));
        assertTrue(out.toString().contains("Venue set."));
        assertFalse(out.toString().contains("Invalid entry; returning to menu."));
        assertEquals("Grand Ballroom", Main.chosenVenue.getName());
    }

    @Test
    @Order(8)
    void testReSelectVenueSufficientBudget() {
        Main.selectVenue(scanOf("y\n3000\n2\n1\n"));

        assertTrue(out.toString().contains("You previously selected this venue: \nVenue: Grand Ballroom"));
        assertTrue(out.toString().contains("Would you like to change your selection? (y/n) "));
        assertTrue(out.toString().contains("Best Option: \nVenue: Garden Hall"));
        assertTrue(out.toString().contains("Is this acceptable?"));
        assertTrue(out.toString().contains("1)\nVenue: Warehouse Event Space"));
        assertTrue(out.toString().contains("Which venue would you like to use?"));
        assertTrue(out.toString().contains("Venue set."));
        assertFalse(out.toString().contains("Invalid entry; returning to menu."));
        assertEquals("Warehouse Event Space", Main.chosenVenue.getName());
    }

    @Test
    @Order(9)
    void testGenerateSeatingChart() {
        Main.generateSeatingChart();

        assertTrue(out.toString().contains("---Seating Chart---"));
        for (int i = 1; i < Main.chosenVenue.getTables(); i++) {
            assertTrue(out.toString().contains("Table " + i + ":"));
        }
        assertNotNull(Main.seating);
    }

    @Test
    @Order(10)
    void testAddTasks() {
        assertEquals(0, Main.taskManager.remainingTaskCount());

        Main.addPreparationTask(scanOf("Task 0\n"));

        assertTrue(out.toString().contains("Task description: "));
        assertTrue(out.toString().contains("Task added."));
        assertTrue(out.toString().contains("1 task(s) left to do; 0 completed."));
        assertEquals(1, Main.taskManager.remainingTaskCount());

    }

    @Test
    @Order(11)
    void testExecuteTasks() {
        Main.executeNextTask();
        assertTrue(out.toString().contains("Task 'Task 0' completed."));
        assertEquals(0, Main.taskManager.remainingTaskCount());
        assertEquals(1, Main.taskManager.completedTaskCount());

        Main.addPreparationTask(scanOf("Task 1\n"));
        Main.addPreparationTask(scanOf("Task 2\n"));

        assertEquals(2, Main.taskManager.remainingTaskCount());

        Main.executeNextTask();
        assertTrue(out.toString().contains("Task 'Task 1' completed."));
        assertEquals(1, Main.taskManager.remainingTaskCount());
        assertEquals(2, Main.taskManager.completedTaskCount());

        Main.executeNextTask();
        assertTrue(out.toString().contains("Task 'Task 2' completed."));
        assertEquals(0, Main.taskManager.remainingTaskCount());
        assertEquals(3, Main.taskManager.completedTaskCount());
    }

    @Test
    @Order(12)
    void testUndoTasks() {
        Main.undoLastTask();
        assertTrue(out.toString().contains("Task 'Task 2' undone."));
        assertEquals(1, Main.taskManager.remainingTaskCount());
        assertEquals(2, Main.taskManager.completedTaskCount());

        Main.undoLastTask();
        assertTrue(out.toString().contains("Task 'Task 1' undone."));
        assertEquals(2, Main.taskManager.remainingTaskCount());
        assertEquals(1, Main.taskManager.completedTaskCount());

        Main.executeNextTask();//redo task 2

        Main.undoLastTask();
        assertTrue(out.toString().contains("Task 'Task 2' undone."));
        assertEquals(2, Main.taskManager.remainingTaskCount());
        assertEquals(1, Main.taskManager.completedTaskCount());
    }

    @Test
    @Order(13)
    void testPrintSummary() {
        Main.printEventSummary();

        assertTrue(out.toString().contains("---Event Summary---"));
        assertTrue(out.toString().contains("Venue: Warehouse Event Space"));
        assertTrue(out.toString().contains("Guests: 50"));
        assertTrue(out.toString().contains("---Seating Chart---"));
        for (int i = 1; i < Main.chosenVenue.getTables(); i++) {
            assertTrue(out.toString().contains("Table " + i + ":"));
        }
        assertTrue(out.toString().contains("Task Status:"));
        assertTrue(out.toString().contains("2 upcoming task(s)"));
        assertTrue(out.toString().contains("1 task(s) completed"));
    }

    @Test
    @Order(14)
    void testAddGuest() {
        assertNull(Main.guestListManager.findGuest("Strawberry Shortcake"));

        Main.addGuest(scanOf("Strawberry Shortcake\ntest\n"));

        assertNotNull(Main.guestListManager.findGuest("Strawberry Shortcake"));
        assertTrue(out.toString().contains("Guest name: "));
        assertTrue(out.toString().contains("Group tag: "));
        assertTrue(out.toString().contains("Guest added."));
    }

    @Test
    @Order(15)
    void testRemoveExistingGuest() {
        assertNotNull(Main.guestListManager.findGuest("Strawberry Shortcake"));

        Main.removeGuest(scanOf("Strawberry Shortcake\n"));

        assertNull(Main.guestListManager.findGuest("Strawberry Shortcake"));
        assertTrue(out.toString().contains("Guest name: "));
        assertTrue(out.toString().contains("Guest removed."));
    }

    @Test
    void testRemoveNonexistentGuest() {
        Main.guestListManager = new GuestListManager();

        assertNull(Main.guestListManager.findGuest("Cheese"));

        Main.removeGuest(scanOf("Cheese\n"));

        assertTrue(out.toString().contains("Guest name: "));
        assertTrue(out.toString().contains("Guest not found."));
    }

    @Test
    void testExecuteNonexistentTask() {
        Main.taskManager = new TaskManager();

        assertEquals(0, Main.taskManager.remainingTaskCount());

        Main.executeNextTask();

        assertTrue(out.toString().contains("No tasks to execute."));
        assertEquals(0, Main.taskManager.remainingTaskCount());
    }

    @Test
    void testUndoNonexistentTask() {
        Main.taskManager = new TaskManager();

        assertEquals(0, Main.taskManager.remainingTaskCount());

        Main.undoLastTask();

        assertTrue(out.toString().contains("No tasks to undo."));
    }

    @Test
    void testSelectVenueAboveMaxCapacity() {
        Main.guestListManager = new GuestListManager();
        Main.venueSelector = null;
        Main.chosenVenue = null;

        Main.loadSampleGuests(scanOf("5000\n"));
        Main.loadSampleVenues();
        Main.selectVenue(scanOf("10000\n"));

        System.out.println(out.toString());
        assertTrue(out.toString().contains("Sorry, no venues fitting 5000 guests found."));
        assertFalse(out.toString().contains("Invalid entry; returning to menu."));
    }

    @Test
    void testPrintSummaryNoData() {
        Main.guestListManager = new GuestListManager();
        Main.taskManager = new TaskManager();
        Main.venueSelector = null;
        Main.chosenVenue = null;
        Main.seating = null;

        Main.printEventSummary();

        assertTrue(out.toString().contains("---Event Summary---"));
        assertTrue(out.toString().contains("No venue selected."));
        assertTrue(out.toString().contains("Guests: 0"));
        assertTrue(out.toString().contains("Seating chart not generated."));
        assertTrue(out.toString().contains("Task Status:"));
        assertTrue(out.toString().contains("0 upcoming task(s)"));
        assertTrue(out.toString().contains("0 task(s) completed"));
    }

    @Test
    void testMenu() {
        Main.guestListManager = new GuestListManager();
        Main.taskManager = new TaskManager();
        Main.venueSelector = null;
        Main.chosenVenue = null;
        Main.seating = null;

        String input =
                "hi\n" +    // invalid command
                "0\n" +     // load sample guests
                "10\n" +    // number of guests
                "1\n" +     // load sample venues
                "4\n" +     // select venue
                "2000\n" +  // budget
                "1\n" +     // accept
                "5\n" +     // generate seating chart
                "9\n" +     // print event summary
                "10\n";     // exit

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Main.main(new String[]{});

        assertEquals(10, Main.guestListManager.getGuestCount());
        assertNotNull(Main.venueSelector);
        assertNotNull(Main.chosenVenue);
        assertNotNull(Main.seating);
        assertTrue(out.toString().contains("Invalid entry; returning to menu."));
        assertTrue(out.toString().contains("10 guests loaded."));
        assertTrue(out.toString().contains("Venues loaded."));
        assertTrue(out.toString().contains("What's your budget? $"));
        assertTrue(out.toString().contains("---Seating Chart---"));
        assertTrue(out.toString().contains("---Event Summary---"));
        assertTrue(out.toString().contains("Goodbye."));
    }
}
