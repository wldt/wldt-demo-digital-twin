package io.github.wldt.demo.digital;

import it.wldt.adapter.digital.DigitalAdapter;
import it.wldt.core.state.*;
import it.wldt.exception.EventBusException;

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
     * Notification about a variation on the DT State with a new Property Created (passed as Parameter)
     * @param digitalTwinStateProperty
     */
    @Override
    protected void onStateChangePropertyCreated(DigitalTwinStateProperty digitalTwinStateProperty) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangePropertyCreated(): " + digitalTwinStateProperty);
    }

    /**
     * Notification about a variation on the DT State with an existing Property updated in terms of description (passed as Parameter)
     * @param digitalTwinStateProperty
     */
    @Override
    protected void onStateChangePropertyUpdated(DigitalTwinStateProperty digitalTwinStateProperty) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangePropertyUpdated(): " + digitalTwinStateProperty);
    }

    /**
     * Notification about a variation on the DT State with an existing Property Deleted (passed as Parameter)
     * @param digitalTwinStateProperty
     */
    @Override
    protected void onStateChangePropertyDeleted(DigitalTwinStateProperty digitalTwinStateProperty) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangePropertyDeleted(): " + digitalTwinStateProperty);
    }

    /**
     * Notification about a variation on the DT State with an existing Property's value updated (passed as Parameter)
     * @param digitalTwinStateProperty
     */
    @Override
    protected void onStatePropertyUpdated(DigitalTwinStateProperty digitalTwinStateProperty) {
        System.out.println("[DemoDigitalAdapter] -> onStatePropertyUpdated(): " + digitalTwinStateProperty);
    }

    /**
     * Notification about a variation on the DT State with an existing Property Deleted (passed as Parameter)
     * @param digitalTwinStateProperty
     */
    @Override
    protected void onStatePropertyDeleted(DigitalTwinStateProperty digitalTwinStateProperty) {
        System.out.println("[DemoDigitalAdapter] -> onStatePropertyDeleted(): " + digitalTwinStateProperty);
    }

    /**
     * Notification of a new Action Enabled on the DT State
     * @param digitalTwinStateAction
     */
    @Override
    protected void onStateChangeActionEnabled(DigitalTwinStateAction digitalTwinStateAction) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeActionEnabled(): " + digitalTwinStateAction);
    }

    /**
     * Notification of an update associated to an existing Digital Action
     * @param digitalTwinStateAction
     */
    @Override
    protected void onStateChangeActionUpdated(DigitalTwinStateAction digitalTwinStateAction) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeActionUpdated(): " + digitalTwinStateAction);
    }

    /**
     * Notification of Digital Action that has been disabled
     * @param digitalTwinStateAction
     */
    @Override
    protected void onStateChangeActionDisabled(DigitalTwinStateAction digitalTwinStateAction) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeActionDisabled(): " + digitalTwinStateAction);
    }

    /**
     * Notification that a new Event has been registered of the DT State
     * @param digitalTwinStateEvent
     */
    @Override
    protected void onStateChangeEventRegistered(DigitalTwinStateEvent digitalTwinStateEvent) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeEventRegistered(): " + digitalTwinStateEvent);
    }

    /**
     * Notification that an existing Event has been updated of the DT State in terms of description
     * @param digitalTwinStateEvent
     */
    @Override
    protected void onStateChangeEventRegistrationUpdated(DigitalTwinStateEvent digitalTwinStateEvent) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeEventRegistrationUpdated(): " + digitalTwinStateEvent);
    }

    /**
     * Notification that an existing Event has been removed from the DT State
     * @param digitalTwinStateEvent
     */
    @Override
    protected void onStateChangeEventUnregistered(DigitalTwinStateEvent digitalTwinStateEvent) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeEventUnregistered(): " + digitalTwinStateEvent);
    }

    /**
     * DT Life Cycle notification that the DT is correctly on Sync
     * @param currentDigitalTwinState
     */
    @Override
    public void onDigitalTwinSync(IDigitalTwinState currentDigitalTwinState) {

        System.out.println("[DemoDigitalAdapter] -> onDigitalTwinSync(): " + currentDigitalTwinState);

        try {

            //Observer all properties
            //observeDigitalTwinStateProperties();

            //Observe only a list of target properties
            /*
            digitalTwinState.getPropertyList().map(eventList -> eventList.stream()
                            .map(DigitalTwinStateProperty::getKey)
                            .collect(Collectors.toList()))
                    .ifPresent(propertyKeys -> {
                        try {
                            observeTargetDigitalTwinProperties(propertyKeys);
                        } catch (EventBusException e) {
                            e.printStackTrace();
                        }
                    });
            */

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
    public void onDigitalTwinUnSync(IDigitalTwinState currentDigitalTwinState) {
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
     * Notification that an existing Relationships Instance has been removed
     * @param digitalTwinStateRelationshipInstance
     */
    @Override
    protected void onStateChangeRelationshipInstanceDeleted(DigitalTwinStateRelationshipInstance digitalTwinStateRelationshipInstance) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeRelationshipInstanceDeleted(): " + digitalTwinStateRelationshipInstance);
    }

    /**
     * Notification that an existing Relationship has been removed from the DT State
     * @param digitalTwinStateRelationship
     */
    @Override
    protected void onStateChangeRelationshipDeleted(DigitalTwinStateRelationship digitalTwinStateRelationship) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeRelationshipDeleted(): " + digitalTwinStateRelationship);
    }

    /**
     * Notification that a new Relationship Instance has been created on the DT State
     * @param digitalTwinStateRelationshipInstance
     */
    @Override
    protected void onStateChangeRelationshipInstanceCreated(DigitalTwinStateRelationshipInstance digitalTwinStateRelationshipInstance) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeRelationshipInstanceCreated(): " + digitalTwinStateRelationshipInstance);
    }

    /**
     * Notification that a new Relationship has been created on the DT State
     * @param digitalTwinStateRelationship
     */
    @Override
    protected void onStateChangeRelationshipCreated(DigitalTwinStateRelationship digitalTwinStateRelationship) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeRelationshipCreated(): " + digitalTwinStateRelationship);
    }

    /**
     * Notification that a Notification for ta specific Event has been received
     * @param digitalTwinStateEventNotification
     */
    @Override
    protected void onDigitalTwinStateEventNotificationReceived(DigitalTwinStateEventNotification digitalTwinStateEventNotification) {
        System.out.println("[DemoDigitalAdapter] -> onDigitalTwinStateEventNotificationReceived(): " + digitalTwinStateEventNotification);
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
