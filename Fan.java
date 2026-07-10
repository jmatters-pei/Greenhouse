/** Student Name: Jonathan Matters 
 * Student ID: 3718743 
 * Date: 2024-07-24 
 * Assignment #3 
 * Program Name: Greenhouse.java, 
 * Description: This program runs a greenhouse simulation. It reads a plan file for a list of events to be scheduled and executed. 
 * */

/**
 * Purpose: 
 * - This class represents a fan in the greenhouse simulation. When triggered, it toggles the state of the fan between on and off, and outputs a message to the console indicating the current state of the fan.
 * 
 * Code behaviours: 
 * - The fan class extends the Event class and overrides the action method to toggle the state of the fan and print a message indicating the current state to the console.
 * - It inherits the scheduling and priority features from the Event class, allowing it to be scheduled with an initial delay and recurring delay.
 * - It inherits the ability to be prioritized among other events in the simulation.
 * - When the Fan action method is called, it checks if the Thermostat has failed. If the Thermostat has failed, the fan is turned off and a message is printed indicating that the fan controls are disabled. If the Thermostat has not failed, the fan toggles its state and prints a message indicating whether it is turned on or off.
 * 
 * Particulars: 
 * - Fan does not manage its own timing or scheduling; it relies on the Event superclass for that functionality.
 * - Fan is typically created by the Greenhouse class based on entries in the plan file, which specifies when the fan should be toggled.
 * 
 * Compiling and executing the program:
 * - Fan is not a standalone program and cannot be executed on its own. It is part of the Greenhouse simulation and is used in conjunction with other classes to create a greenhouse simulation. 
 * - To compile and execute the program, you would typically compile the entire Greenhouse simulation project.
 * - To compile and run the entire project, enter the folder where the folder Greenhouse is located and run the following command in the terminal:
 *              1) javac Greenhouse\*.java
 *              2) java Greenhouse
 * Ensure that the plan file is present in the same directory as Greenhouse.java and named greenhouse_plan.txt. If there is no plan file present, the Greenhouse class will not have any events to schedule, and no door toggling will occur when executing the program.
 *
 * Classes:
 * - Fan:
 *   - A concrete subclass of Event that represents a fan event. Its primary purpose is to toggle the state of the fan between on and off when it is triggered.
 *  
 * Instance Variables: 
 *  - on (private boolean):
 *         - A flag indicating whether the fan is currently on or off. It is toggled each time the action method is called.
 * 
 * - All other instance variables are inherited from the Event superclass.
 * - InitialDelay (protected long):
 *         - The initial delay before the event is triggered, in milliseconds. Is used to determine when the fan should be toggled after the simulation starts.
 * - RecurringDelay (protected long):
 *         - The delay between recurring triggers of the event, in milliseconds. Is used to determine how often the fan should be toggled after the initial trigger.
 * - isRunning (protected volatile boolean):
 *         - A flag indicating whether the event is currently running.
 * - hasFailed (public static volatile boolean):
 *         - A flag indicating whether the event has failed.
 * - eventPriority (protected int):
 *        - The priority of the event, used to determine the thread priority.
 * - threadPriority (protected int):
 *       - The priority of the thread that executes the event. Calculated based on the event priority.
 */

public class Fan extends Event {
    private boolean on = false;

    public Fan(long initialDelay, long recurringDelay, int eventPriority) {
        super(initialDelay, recurringDelay, eventPriority);
    }

    @Override
    public void action() {
            if (Thermostat.hasFailed) {
                on = false;
                System.out.println("Fan controls attempted. Fan controls are disabled because Thermostat has failed. Fan is OFF.");
        } else {
            on = !on;
            System.out.println("Fan " + (on ? "turned ON" : "turned OFF"));
        }
    }
}
