/** Student Name: Jonathan Matters 
 * Student ID: 3718743 
 * Date: 2024-07-24 
 * Assignment #3 
 * Program Name: Greenhouse.java, 
 * Description: This program runs a greenhouse simulation. It reads a plan file for a list of events to be scheduled and executed. 
 * */

/**
 * Purpose: 
 * - This class represents the rotation of plant trays in the greenhouse simulation. When triggered, it toggles the orientation of the plant trays, and outputs a message to the console indicating the current orientation of the trays.
 * - The trays of plants need to be rotated to ensure that they do not grow in a specific direction, which could have a negative effect on the price people are willing to pay for the plants if they are all lopsided to grow more on the side facing the light.  The trays of plants just need to be turned to prevent this. 
 * 
 * Code behaviours: 
 * - The Rotate class extends the Event class and overrides the action method to toggle the state of the rotation and print a message indicating the current orientation of the trays to the console.
 * - It inherits the scheduling and priority features from the Event class, allowing it to be scheduled with an initial delay and recurring delay.
 * - It inherits the ability to be prioritized among other events in the simulation.
 * 
 * Particulars: 
 * - Rotate does not manage its own timing or scheduling; it relies on the Event superclass for that functionality.
 * - Rotate is typically created by the Greenhouse class based on entries in the plan file, which specifies when the rotation should be toggled.
 * 
 * Compiling and executing the program:
 * - Rotate is not a standalone program and cannot be executed on its own. It is part of the Greenhouse simulation and is used in conjunction with other classes to create a greenhouse simulation. 
 * - To compile and execute the program, you would typically compile the entire Greenhouse simulation project.
 * - To compile and run the entire project, enter the folder where the folder Greenhouse is located and run the following command in the terminal:
 *              1) javac Greenhouse\*.java
 *              2) java Greenhouse
 * Ensure that the plan file is present in the same directory as Greenhouse.java and named greenhouse_plan.txt. If there is no plan file present, the Greenhouse class will not have any events to schedule, and no rotation toggling will occur when executing the program.
 *
 * Classes:
 * - Rotate:
 *   - A concrete subclass of Event that represents a rotation event. Its primary purpose is to toggle the orientation of the plant trays when it is triggered.
 *  
 * Instance Variables: 
 *  - trayDirection (private boolean):
 *         - A flag indicating whether the plant trays are currently rotated or not. It is toggled each time the action method is called.
 * 
 * - All other instance variables are inherited from the Event superclass.
 * - InitialDelay (protected long):
 *         - The initial delay before the event is triggered, in milliseconds. Is used to determine when the door should be toggled after the simulation starts.
 * - RecurringDelay (protected long):
 *         - The delay between recurring triggers of the event, in milliseconds. Is used to determine how often the door should be toggled after the initial trigger.
 * - isRunning (protected volatile boolean):
 *         - A flag indicating whether the event is currently running.
 * - hasFailed (public static volatile boolean):
 *         - A flag indicating whether the event has failed.
 * - eventPriority (protected int):
 *        - The priority of the event, used to determine the thread priority.
 * - threadPriority (protected int):
 *       - The priority of the thread that executes the event. Calculated based on the event priority.
 */

public class Rotate extends Event {
        private boolean trayDirection = false;

    public Rotate(long initialDelay, long recurringDelay, int eventPriority) {
        super(initialDelay, recurringDelay, eventPriority);
    }

    @Override
    public void action() {
        trayDirection = !trayDirection;
        System.out.println("Rotated tray orientation to " + (trayDirection ? "NORTH-SOUTH" : "EAST-WEST"));
    }
    
}