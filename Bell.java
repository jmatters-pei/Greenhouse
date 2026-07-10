/** 
 * Student Name: Jonathan Matters 
 * Student ID: 3718743 
 * Date: 2024-07-24 
 * Assignment #3 
 * Program Name: Greenhouse.java, 
 * Description: This program runs a greenhouse simulation. It reads a plan file for a list of events to be scheduled and executed. 
 * */

/**
 * Purpose: 
 * This class represents an alarm in the greenhouse simulation. When triggered, it outputs an alarm message to the console to identify something important has happened that requires the user' attention. 
 * 
 * Code behaviours: 
 * - The Bell class extends the Event class and overrides the action method to print an alarm message to the console when the event is triggered.
 * - It inherits the scheduling and priority features from the Event class, allowing it to be scheduled with an initial delay and recurring delay if needed.
 * - It inherits the ability to be prioritized among other events in the simulation.
 * - When the action method is called, it prints "ALARM!!!" to the console to indicate that the alarm has been triggered.
 * 
 * Particulars: 
 * - Bell does not manage its own timing or scheduling; it relies on the Event superclass for that functionality.
 * - Bell is typically created by the Greenhouse class based on entries in the plan file, which specifies when the alarm should be triggered. However, it can be created called by other classes where appropriate. For example, thermostat failure triggers a Bell event (x5) to alert the user of the failure.
 * - The all caps "ALARM!!!" message is used to ensure that the alarm is noticeable and draws attention to the event.
 * 
 * Compiling and executing the program:
 * - Bell is not a standalone program and cannot be executed on its own. It is part of the Greenhouse simulation and is used in conjunction with other classes to create a greenhouse simulation.
 * - To compile and execute the program, you would typically compile the entire Greenhouse simulation project.
 * - To compile and run the entire project, enter the folder where the folder Greenhouse is located and run the following command in the terminal:
 *              1) javac Greenhouse\*.java
 *              2) java Greenhouse
 * Ensure that the plan file is present in the same directory as Greenhouse.java and named greenhouse_plan.txt. If there is no plan file present, the Greenhouse class will not have any events to schedule, and no alarms will be triggered when executing the program.
 * 
 * Classes:
 * - Bell:
 *   - A concrete subclass of Event that reperests an alarm event. Its primary purpoce is to print alarm when it is triggered. 
 * Instance Variables: 
 *   All instance variables are inherited from the Event superclass.
 * 
 *   initialDelay (protected long):
 *          - The initial delay before the event is triggered, in milliseconds. Is used to determine when the alarm should be triggered after the simulation starts.
 *   recurringDelay (protected long):
 *          - The delay between recurring triggers of the event, in milliseconds. Is used to determine how often the alarm should be triggered after the initial trigger.
 *   isRunning (protected volatile boolean):
 *          - A flag indicating whether the event is currently running.
 *   hasFailed (public static volatile boolean):
 *          - A flag indicating whether the event has failed.
 *   eventPriority (protected int):
 *          - The priority of the event, used to determine the thread priority.
 *   threadPriority (protected int):
 *          - The priority of the thread that executes the event. Calculated based on the event priority.
 */

public class Bell extends Event {
    public Bell(long initialDelay, long recurringDelay, int eventPriority) {
        super(initialDelay, recurringDelay, eventPriority);
    }
    public Bell(long initialDelay, int eventPriority) {
        super(initialDelay, eventPriority);
    }

    @Override
    public void action() {
        System.out.println("ALARM!!!");
    }
}
