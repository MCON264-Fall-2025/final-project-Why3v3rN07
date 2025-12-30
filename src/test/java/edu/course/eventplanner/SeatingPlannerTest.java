package edu.course.eventplanner;

import edu.course.eventplanner.model.Guest;
import edu.course.eventplanner.model.Venue;
import edu.course.eventplanner.service.SeatingPlanner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SeatingPlannerTest {
    // AAA: Arrange, Act, Assert

    @Test
    void testIfPeopleAreCorrectlyBeingPlaced() {
        // --- ARRANGE ---
        Venue venue = new Venue("Grand Hall", 1000, 50, 5, 10);
        SeatingPlanner planner = new SeatingPlanner(venue);

        List<Guest> guests = new ArrayList<>();
        guests.add(new Guest("Alice", "Family"));
        guests.add(new Guest("Bob", "Family"));

        // --- ACT ---
        Map<Integer, List<Guest>> result = planner.generateSeating(guests);

        // --- ASSERT ---
        assertNotNull(result.get(1));
        assertEquals(2, result.get(1).size());
        assertEquals("Alice", result.get(1).get(0).getName());
        assertEquals("Family", result.get(1).get(1).getGroupTag());
    }

    @Test
    void testWhenMaxTablesAreFull() {
        //Arrange
        // 1 table, 5 seats per table
        Venue smallVenue = new Venue("Tiny Room", 100, 5, 1, 5);
        SeatingPlanner planner = new SeatingPlanner(smallVenue);
        List<Guest> guests = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            guests.add(new Guest("Guest " + i, "Family"));
        }
        //Act
        Map<Integer, List<Guest>> result = planner.generateSeating(guests);
        //Assert
        assertEquals(1, result.size(), "Should only have 1 table");
        assertEquals(5, result.get(1).size(), "Table 1 should be capped at 5");
        assertNull(result.get(2), "Table 2 should not exist even though guests remain");
    }

    @Test
    void testIfEmptyGuestListIsPassedIn() {
        //Arrange
        Venue venue = new Venue("Grand Hall", 1000, 50, 5, 10);
        SeatingPlanner planner = new SeatingPlanner(venue);
        List<Guest> guests = new ArrayList<>();
        // Act
        Map<Integer, List<Guest>> result = planner.generateSeating(guests);
        //Assert
        assertTrue(result.isEmpty(), "The seating plan should be completely empty");
        assertEquals(0, result.size(), "There should be 0 tables created");
    }

    @Test
    void testIfWeHaveAnExactAmountOfSpace() {
        //Arrange
        Venue venue = new Venue("Tiny Hall", 100, 4, 2, 2);
        SeatingPlanner planner = new SeatingPlanner(venue);
        List<Guest> guests = new ArrayList<>();
        guests.add(new Guest("Alice", "Family"));
        guests.add(new Guest("Bob", "Family"));
        guests.add(new Guest("Josh", "Friend"));
        guests.add(new Guest("Greg", "Friend"));
        //act
        Map<Integer, List<Guest>> result = planner.generateSeating(guests);
        //assert
        assertEquals(2, result.size(), "Should have 2 tables");
        assertEquals(2, result.get(1).size(), "Table 1 should be capped at 2");
        assertEquals("Josh", result.get(2).get(0).getName(), "Josh should start Table 2");
    }
    @Test
    void testTableOrderingIsSorted() {
        // arrange
        Venue venue = new Venue("Ordered Hall", 100, 6, 3, 2);
        SeatingPlanner planner = new SeatingPlanner(venue);
        List<Guest> guests = new ArrayList<>();
        guests.add(new Guest("Alice", "Family"));
        guests.add(new Guest("Bob", "Family"));
        guests.add(new Guest("Josh", "CoWorker"));
        guests.add(new Guest("Greg", "Friend"));
        guests.add(new Guest("Ann", "CoWorker"));
        guests.add(new Guest("Susan", "Friend"));

        // act
        Map<Integer, List<Guest>> result = planner.generateSeating(guests);

        // Get the keys (table IDs) from the TreeMap
        List<Integer> keyList = new ArrayList<>(result.keySet());

        // assert
        // Proves the TreeMap (BST) property: keys must be in ascending order
        for (int i = 0; i < keyList.size() - 1; i++) {
            Integer currentTableId = keyList.get(i);
            Integer nextTableId = keyList.get(i + 1);

            assertTrue(currentTableId < nextTableId,
                    "BST Property Failed: Table " + currentTableId + " should come before " + nextTableId);
        }

        assertEquals(3, result.size(), "Should have exactly 3 tables for 6 guests");
    }

    @Test
    void testGuestGroupingByTag() {
        // Arrange
        Venue venue = new Venue("Group Hall", 100, 6, 3, 2);
        SeatingPlanner planner = new SeatingPlanner(venue);
        List<Guest> guests = new ArrayList<>();

        // We add them in "interleaved" order: Family, CoWorker, Family, CoWorker
        guests.add(new Guest("Alice", "Family"));
        guests.add(new Guest("Josh", "CoWorker"));
        guests.add(new Guest("Bob", "Family"));
        guests.add(new Guest("Ann", "CoWorker"));

        // Act
        Map<Integer, List<Guest>> result = planner.generateSeating(guests);

        // Assert
        // Table 1 should have Alice and Bob because they share the "Family" tag
        // Even though Josh was added to the guest list before Bob.
        assertEquals("Alice", result.get(1).get(0).getName());
        assertEquals("Bob", result.get(1).get(1).getName(), "Bob should be pulled into Table 1 with Alice");

        // Table 2 should contain the CoWorkers
        assertEquals("Josh", result.get(2).get(0).getName());
        assertEquals("Ann", result.get(2).get(1).getName(), "Ann should be seated with Josh in Table 2");
    }
}