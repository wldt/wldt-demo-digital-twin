package io.github.wldt.demo.digital;

import it.wldt.adapter.digital.DigitalAdapter;
import it.wldt.core.state.*;
import it.wldt.exception.EventBusException;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Authors:
 *          Marco Picone, Ph.D. (picone.m@gmail.com)
 * Date: 01/09/2023
 * Project: White Label Digital Twin Java Framework - (whitelabel-digitaltwin)
 */
public class DemoConfDigitalAdapter extends DigitalAdapter<DemoDigitalAdapterConfiguration> {

    public DemoConfDigitalAdapter(String id, DemoDigitalAdapterConfiguration configuration) {
        super(id, configuration);
    }

    /**
     * Callback to notify the adapter on its correct startup
     */
    @Override
    public void onAdapterStart() {
        System.out.println("[TestDigitalAdapter] -> onAdapterStart()");
    }

    /**
     * Callback to notify the adapter that has been stopped
     */
    @Override
    public void onAdapterStop() {
        System.out.println("[DemoDigitalAdapter] -> onAdapterStop()");
    }


    /**
     * DT Life Cycle notification that the DT is correctly on Sync
     * @param currentDigitalTwinState
     */
    @Override
    public void onDigitalTwinSync(DigitalTwinState currentDigitalTwinState) {

        System.out.println("[DemoDigitalAdapter] -> onDigitalTwinSync(): " + currentDigitalTwinState);

        try {

            digitalTwinState.getEventList()
                    .map(eventList -> eventList.stream()
                            .map(DigitalTwinStateEvent::getKey)
                            .collect(Collectors.toList()))
                    .ifPresent(eventKeys -> {
                        try {
                            observeDigitalTwinEventsNotifications(eventKeys);
                        } catch (EventBusException e) {
                            e.printStackTrace();
                        }
                    });

            //Start Digital Action Emulation
            new Thread(emulateIncomingDigitalAction()).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * DT Life Cycle notification that the DT is currently Not Sync
     * @param currentDigitalTwinState
     */
    @Override
    public void onDigitalTwinUnSync(DigitalTwinState currentDigitalTwinState) {
        System.out.println("[DemoDigitalAdapter] -> onDigitalTwinUnSync(): " + currentDigitalTwinState);
    }

    /**
     * DT Life Cycle notification that the DT has been created
     */
    @Override
    public void onDigitalTwinCreate() {
        System.out.println("[DemoDigitalAdapter] -> onDigitalTwinCreate()");
    }

    /**
     * DT Life Cycle Notification that the DT has correctly Started
     */
    @Override
    public void onDigitalTwinStart() {
        System.out.println("[DemoDigitalAdapter] -> onDigitalTwinStart()");
    }

    /**
     * DT Life Cycle Notification that the DT has been stopped
     */
    @Override
    public void onDigitalTwinStop() {
        System.out.println("[DemoDigitalAdapter] -> onDigitalTwinStop()");
    }

    /**
     * DT Life Cycle Notification that the DT has destroyed
     */
    @Override
    public void onDigitalTwinDestroy() {
        System.out.println("[DemoDigitalAdapter] -> onDigitalTwinDestroy()");
    }

    /**
     * Callback method allowing the Digital Adapter to receive the updated Digital Twin State together with
     * the previous state and the list of applied changes
     *
     * @param newDigitalTwinState The new Digital Twin State computed by the Shadowing Function
     * @param previousDigitalTwinState The previous Digital Twin State
     * @param digitalTwinStateChangeList The list of applied changes to compute the new Digital Twin State
     */
    @Override
    protected void onStateUpdate(DigitalTwinState newDigitalTwinState, DigitalTwinState previousDigitalTwinState, ArrayList<DigitalTwinStateChange> digitalTwinStateChangeList) {

        // In newDigitalTwinState we have the new DT State
        System.out.println("New DT State is: " + newDigitalTwinState);

        // The previous DT State is available through the variable previousDigitalTwinState
        System.out.println("Previous DT State is: " + previousDigitalTwinState);

        // We can also check each DT's state change potentially differentiating the behaviour for each change
        if (digitalTwinStateChangeList != null && !digitalTwinStateChangeList.isEmpty()) {

            // Iterate through each state change in the list
            for (DigitalTwinStateChange stateChange : digitalTwinStateChangeList) {

                // Get information from the state change
                DigitalTwinStateChange.Operation operation = stateChange.getOperation();
                DigitalTwinStateChange.ResourceType resourceType = stateChange.getResourceType();
                DigitalTwinStateResource resource = stateChange.getResource();

                // Perform different actions based on the type of operation
                switch (operation) {
                    case OPERATION_UPDATE:
                        // Handle an update operation
                        System.out.println("Update operation on " + resourceType + ": " + resource);
                        break;
                    case OPERATION_UPDATE_VALUE:
                        // Handle an update value operation
                        System.out.println("Update value operation on " + resourceType + ": " + resource);
                        break;
                    case OPERATION_ADD:
                        // Handle an add operation
                        System.out.println("Add operation on " + resourceType + ": " + resource);
                        break;
                    case OPERATION_REMOVE:
                        // Handle a remove operation
                        System.out.println("Remove operation on " + resourceType + ": " + resource);
                        break;
                    default:
                        // Handle unknown operation (optional)
                        System.out.println("Unknown operation on " + resourceType + ": " + resource);
                        break;
                }
            }
        } else {
            // No state changes
            System.out.println("No state changes detected.");
        }
    }

    /**
     * Callback method to receive a new computed Event Notification (associated to event declared in the DT State)
     *
     * @param digitalTwinStateEventNotification The generated Notification associated to a DT Event
     */
    @Override
    protected void onEventNotificationReceived(DigitalTwinStateEventNotification<?> digitalTwinStateEventNotification) {
        System.out.println("[DemoDigitalAdapter] -> Received Event Notification: " + digitalTwinStateEventNotification);
    }

    private Runnable emulateIncomingDigitalAction(){
        return () -> {
            try {

                System.out.println("[DemoDigitalAdapter] -> Sleeping before Emulating Incoming Digital Action ...");
                Thread.sleep(5000);
                Random random = new Random();

                //Emulate the generation on 'n' temperature measurements
                for(int i = 0; i < getConfiguration().getEmulatedActionCount(); i++){

                    //Sleep to emulate sensor measurement
                    Thread.sleep(getConfiguration().getSleepTimeMs());

                    double randomTemperature = getConfiguration().getTemperatureMinValue() + (getConfiguration().getTemperatureMaxValue() - getConfiguration().getTemperatureMinValue()) * random.nextDouble();
                    publishDigitalActionWldtEvent("set-temperature-action-key", randomTemperature);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
