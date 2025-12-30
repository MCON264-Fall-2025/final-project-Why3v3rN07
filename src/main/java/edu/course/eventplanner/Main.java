package edu.course.eventplanner;

import edu.course.eventplanner.model.*;
import edu.course.eventplanner.service.*;
import edu.course.eventplanner.util.Generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GuestListManager guestListManager = new GuestListManager();
        TaskManager taskManager = new TaskManager();
        VenueSelector venueSelector = new VenueSelector(new ArrayList<Venue>());
        Venue chosenVenue = null;

        Scanner input = new Scanner(System.in);
        boolean done = false;
        while (!done) {
            System.out.println("Event Planner Mini");
            System.out.print("""
                      ~~~~ Menu ~~~~
                    0. Load sample guests
                    1. Load sample venues
                    2. Add guest
                    3. Remove guest
                    4. Select venue
                    5. Generate seating chart
                    6. Add preparation task
                    7. Execute next task
                    8. Undo last task
                    9. Print event summary
                    10. Exit
                    
                    Enter your choice:\s
                    """);
            switch (input.nextLine()) {
                case "0": loadSampleGuests(input, guestListManager); break;
                case "1": loadSampleVenues(venueSelector); break;
                case "2": addGuest(input, guestListManager); break;
                case "3": removeGuest(input, guestListManager); break;
                case "4": selectVenue(input, venueSelector, guestListManager, chosenVenue); break;
                case "5": generateSeatingChart(chosenVenue, guestListManager); break;
                case "6": addPreparationTask(input, taskManager); break;
                case "7": executeNextTask(taskManager); break;
                case "8": undoLastTask(taskManager); break;
                case "9": printEventSummary(); break;
                case "10": done = true; break;
                default: System.out.println("Invalid entry; try again.");
            }
        }
    }

    private static void loadSampleGuests(Scanner input, GuestListManager guestListManager) {
        System.out.println("Number of guests to load: ");
        int numGuests = Integer.parseInt(input.nextLine()); //need error handling
        for (Guest g : Generators.generateGuests(numGuests))
            guestListManager.addGuest(g);
        System.out.println(numGuests + " guests loaded.");
    }

    private static void loadSampleVenues(VenueSelector venueSelector) {
        venueSelector = new VenueSelector(Generators.generateVenues());
    }

    private static void addGuest(Scanner input, GuestListManager guestListManager) {
        System.out.print("Guest name: ");
        String name = input.nextLine();
        System.out.print("Group tag: ");
        String groupTag = input.nextLine();
        guestListManager.addGuest(new Guest(name, groupTag));
        System.out.println("Guest added.");
    }

    private static void removeGuest(Scanner input, GuestListManager guestListManager) {
        System.out.print("Guest name: ");
        String name = input.nextLine();
        if (guestListManager.findGuest(name) != null) {
            boolean result = guestListManager.removeGuest(name);
            System.out.println(result ? "Guest removed." : "Guest not found.");
        } else System.out.println("Guest not found.");
    }

    private static void selectVenue(Scanner input, VenueSelector venueSelector, GuestListManager guestListManager, Venue chosenVenue) {
        System.out.print("What's your budget? $");
        double budget;
        if (input.hasNextDouble()) {
            budget = input.nextDouble();
            Venue best = venueSelector.selectVenue(budget, guestListManager.getGuestCount());
            System.out.println("Best venue: " +
                    "Name: " + best.getName() +
                    "Cost: " + best.getCost() +
                    "Capacity: " + best.getCapacity()
            );
            System.out.println("""
                    Is this acceptable?
                    1. Yes, set this as my venue
                    2. No, view all options
            """);
            switch(input.nextLine()) {
                case "1":
                    chosenVenue = best;
                    System.out.println("Venue set.");
                    break;
                case "2":
                    //TODO: print other venues
                    break;
                default: System.out.println("Invalid entry; returning to menu.");
            }
        }
        System.out.println("Invalid entry; returning to menu. ");
    }

    private static void generateSeatingChart(Venue chosenVenue, GuestListManager guestListManager) {
        if (chosenVenue != null) {
            SeatingPlanner planner = new SeatingPlanner(chosenVenue);
            Map<Integer, List<Guest>> seating = planner.generateSeating(guestListManager.getAllGuests());
            //TODO: print seating chart
        } else System.out.println("No venue selected; please select a venue first.");
    }

    private static void addPreparationTask(Scanner input, TaskManager taskManager) {
        System.out.print("Task description: ");
        taskManager.addTask(new Task(input.nextLine()));
        System.out.println("Task added.");
    }

    private static void executeNextTask(TaskManager taskManager) {
        if (taskManager.remainingTaskCount() == 0) System.out.println( "No tasks to execute.");
        else {
            Task completedTask = taskManager.executeNextTask();
            System.out.print("Task '" + completedTask.getDescription() + "' completed.");
        }
    }

    private static void undoLastTask(TaskManager taskManager) {
        if (taskManager.completedTaskCount() < 0)
            System.out.println("Task '"+taskManager.undoLastTask().getDescription()+"' undone.");
        else System.out.println("No tasks to undo.");
    }

    private static void printEventSummary() {
        //TODO: print all data
    }
}
