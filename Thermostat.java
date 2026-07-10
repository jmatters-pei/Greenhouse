/** Student Name: Jonathan Matters 
 * Student ID: 3718743 
 * Date: 2024-07-24 
 * Assignment #3 
 * Program Name: Greenhouse.java, 
 * Description: This program runs a greenhouse simulation. It reads a plan file for a list of events to be scheduled and executed. 
 * */

/**
 * Purpose: 
 * - This class represents a thermostat in the greenhouse simulation. When triggered, it toggles the state of the thermostat between on and off, and outputs a message to the console indicating the current state of the thermostat.
 * 
 * Code behaviours: 
 * - The Thermostat class extends the Event class and overrides the action method to toggle the state of the thermostat and print a message indicating the current state to the console.
 * - It inherits the scheduling and priority features from the Event class, allowing it to be scheduled with an initial delay and recurring delay.
 * - It inherits the ability to be prioritized among other events in the simulation.
 * - When the Thermostat action method is called, it checks if the Thermostat has failed. If the Thermostat has failed, it prints a message indicating that the thermostat has failed. If the Thermostat has not failed, the thermostat toggles its state and prints a message indicating whether it is turned on or off.
 * 
 * Particulars: 
 * - Thermostat does not manage its own timing or scheduling; it relies on the Event superclass for that functionality.
 * - Thermostat is typically created by the Greenhouse class based on entries in the plan file, which specifies when the thermostat should be toggled.
 * 
 * Compiling and executing the program:
 * - Thermostat is not a standalone program and cannot be executed on its own. It is part of the Greenhouse simulation and is used in conjunction with other classes to create a greenhouse simulation. 
 * - To compile and execute the program, you would typically compile the entire Greenhouse simulation project.
 * - To compile and run the entire program, enter the folder where the folder Greenhouse is located and run the following command in the terminal:
 *              1) javac Greenhouse\*.java
 *              2) cd Greenhouse
 *              3) java Greenhouse
 * 
 * Ensure that the plan file is present in the same directory as Greenhouse.java and named greenhouse_plan.txt. If there is no plan file present, the Greenhouse class will not have any events to schedule, and no door toggling will occur when executing the program.
 *
 * Classes:
 * - Thermostat:
 *   - A concrete subclass of Event that represents a thermostat event. Its primary purpose is to toggle the state of the thermostat between on and off when it is triggered.
 *  
 * Instance Variables: 
 *  - on (private boolean):
 *         - A flag indicating whether the thermostat is currently on or off. It is toggled each time the action method is called.
 * 
 * - All other instance variables are inherited from the Event superclass.
 * - InitialDelay (protected long):
 *         - The initial delay before the event is triggered, in milliseconds. Is used to determine when the thermostat should be toggled after the simulation starts.
 * - RecurringDelay (protected long):
 *         - The delay between recurring triggers of the event, in milliseconds. Is used to determine how often the thermostat should be toggled after the initial trigger.
 * - isRunning (protected volatile boolean):
 *         - A flag indicating whether the event is currently running.
 * - hasFailed (public static volatile boolean):
 *         - A flag indicating whether the event has failed.
 * - eventPriority (protected int):
 *        - The priority of the event, used to determine the thread priority.
 * - threadPriority (protected int):
 *       - The priority of the thread that executes the event. Calculated based on the event priority.
 */

public class Thermostat extends Event {
    private boolean on = false;

    public Thermostat(long initialDelay, long recurringDelay, int eventPriority) {
        super(initialDelay, recurringDelay, eventPriority);
    }

    @Override
    public void action() {
        if (hasFailed) {
            System.out.println("Thermostat failed");
            on = false; // turn off the thermostat if it has failed
            stop(); // stop the thermostat from running if it has failed

        } 
        else {
            on = !on;
            System.out.println("Thermostat " + (on ? "turned ON" : "turned OFF"));
        }
    }
}
