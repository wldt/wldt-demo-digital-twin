package io.github.wldt.demo;

import it.wldt.adapter.digital.event.DigitalActionWldtEvent;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.PhysicalAssetRelationshipInstance;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent;
import it.wldt.core.model.ShadowingFunction;
import it.wldt.core.state.*;

import java.util.Map;

/**
 * Authors:
 *          Marco Picone, Ph.D. (picone.m@gmail.com)
 * Date: 01/09/2023
 * Project: White Label Digital Twin Java Framework - (whitelabel-digitaltwin)
 */
public class DemoShadowingFunction extends ShadowingFunction {

    public DemoShadowingFunction(String id) {
        super(id);
    }

    //// Shadowing Function Management Callbacks ////

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }

    //// Bound LifeCycle State Management Callbacks ////

    @Override
    protected void onDigitalTwinBound(Map<String, PhysicalAssetDescription> adaptersPhysicalAssetDescriptionMap) {

        try{

            System.out.println("[TestShadowingFunction] -> onDigitalTwinBound(): " + adaptersPhysicalAssetDescriptionMap);

            //Iterate over all the received PAD from connected Physical Adapters
            adaptersPhysicalAssetDescriptionMap.values().forEach(pad -> {
                pad.getProperties().forEach(property -> {
                    try {

                        //Create and write the property on the DT's State
                        this.digitalTwinStateManager.createProperty(new DigitalTwinStateProperty<>(property.getKey(),(Double) property.getInitialValue()));

                        //Start observing the variation of the physical property in order to receive notifications
                        //Without this call the Shadowing Function will not receive any notifications or callback about
                        //incoming physical property of the target type and with the target key
                        this.observePhysicalAssetProperty(property);

                        System.out.println("[TestShadowingFunction] -> onDigitalTwinBound() -> Property Created & Observed:" + property.getKey());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                //Iterate over available declared Physical Property only for the target Physical Adapter's PAD
                /*
                pad.getProperties().forEach(property -> {
                    try {

                        //Check property Key and type
                        if(property.getKey().equals("temperature-property-key")
                                && property.getInitialValue() != null
                                &&  property.getInitialValue() instanceof Double) {

                            //Instantiate a new DT State Property of the right type, the same key and initial value
                            DigitalTwinStateProperty<Double> dtStateProperty = new DigitalTwinStateProperty<Double>(property.getKey(),(Double) property.getInitialValue());

                            //Create and write the property on the DT's State
                            this.digitalTwinState.createProperty(dtStateProperty);

                            //Start observing the variation of the physical property in order to receive notifications
                            //Without this call the Shadowing Function will not receive any notifications or callback about
                            //incoming physical property of the target type and with the target key
                            this.observePhysicalAssetProperty(property);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                */

                //Iterate over available declared Physical Events for the target Physical Adapter's PAD
                pad.getEvents().forEach(event -> {
                    try {

                        //Instantiate a new DT State Event with the same key and type
                        DigitalTwinStateEvent dtStateEvent = new DigitalTwinStateEvent(event.getKey(), event.getType());

                        //Create and write the event on the DT's State
                        this.digitalTwinStateManager.registerEvent(dtStateEvent);

                        //Start observing the variation of the physical event in order to receive notifications
                        //Without this call the Shadowing Function will not receive any notifications or callback about
                        //incoming physical events of the target type and with the target key
                        this.observePhysicalAssetEvent(event);

                        System.out.println("[TestShadowingFunction] -> onDigitalTwinBound() -> Event Created & Observed:" + event.getKey());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                //Iterate over available declared Physical Actions for the target Physical Adapter's PAD
                pad.getActions().forEach(action -> {
                    try {

                        //Instantiate a new DT State Action with the same key and type
                        DigitalTwinStateAction dtStateAction = new DigitalTwinStateAction(action.getKey(), action.getType(), action.getContentType());

                        //Enable the action on the DT's State
                        this.digitalTwinStateManager.enableAction(dtStateAction);

                        System.out.println("[TestShadowingFunction] -> onDigitalTwinBound() -> Action Enabled:" + action.getKey());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                pad.getRelationships().forEach(relationship -> {
                    try{
                        if(relationship != null && relationship.getName().equals("insideIn")){

                            DigitalTwinStateRelationship<String> insideInDtStateRelationship = new DigitalTwinStateRelationship<>(relationship.getName(), relationship.getName());

                            this.digitalTwinStateManager.createRelationship(insideInDtStateRelationship);

                            observePhysicalAssetRelationship(relationship);

                            System.out.println("[TestShadowingFunction] -> onDigitalTwinBound() -> Relationship Created & Observed :" + relationship.getName());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });

            });

            //Start observation to receive all incoming Digital Action through active Digital Adapter
            //Without this call the Shadowing Function will not receive any notifications or callback about
            //incoming request to execute an exposed DT's Action
            observeDigitalActionEvents();

            //Notify the DT Core that the Bounding phase has been correctly completed and the DT has evaluated its
            //internal status according to what is available and declared through the Physical Adapters
            notifyShadowingSync();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDigitalTwinUnBound(Map<String, PhysicalAssetDescription> map, String s) {

    }

    @Override
    protected void onPhysicalAdapterBidingUpdate(String s, PhysicalAssetDescription physicalAssetDescription) {

    }

    //// Physical Property Variation Callback ////

    @Override
    protected void onPhysicalAssetPropertyVariation(PhysicalAssetPropertyWldtEvent<?> physicalAssetPropertyWldtEvent) {

        try {

            System.out.println("[TestShadowingFunction] -> onPhysicalAssetPropertyVariation() -> Variation on Property :" + physicalAssetPropertyWldtEvent.getPhysicalPropertyId());

            this.digitalTwinStateManager.updateProperty(new DigitalTwinStateProperty<>(
                    physicalAssetPropertyWldtEvent.getPhysicalPropertyId(),
                    physicalAssetPropertyWldtEvent.getBody()));

            System.out.println("[TestShadowingFunction] -> onPhysicalAssetPropertyVariation() -> DT State UPDATE Property :" + physicalAssetPropertyWldtEvent.getPhysicalPropertyId());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //// Physical Event Notification Callback ////

    @Override
    protected void onPhysicalAssetEventNotification(PhysicalAssetEventWldtEvent<?> physicalAssetEventWldtEvent) {
        try {

            System.out.println("[TestShadowingFunction] -> onPhysicalAssetPropertyVariation() -> Notification for Event :" + physicalAssetEventWldtEvent.getPhysicalEventKey());

            this.digitalTwinStateManager.notifyDigitalTwinStateEvent(new DigitalTwinStateEventNotification<>(
                    physicalAssetEventWldtEvent.getPhysicalEventKey(),
                    physicalAssetEventWldtEvent.getBody(),
                    physicalAssetEventWldtEvent.getCreationTimestamp()));

            System.out.println("[TestShadowingFunction] -> onPhysicalAssetPropertyVariation() -> DT State Notification for Event:" + physicalAssetEventWldtEvent.getPhysicalEventKey());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //// Physical Relationships Notification Callbacks ////

    @Override
    protected void onPhysicalAssetRelationshipEstablished(PhysicalAssetRelationshipInstanceCreatedWldtEvent<?> physicalAssetRelationshipInstanceCreatedWldtEvent) {
        try{

            if(physicalAssetRelationshipInstanceCreatedWldtEvent != null
                    && physicalAssetRelationshipInstanceCreatedWldtEvent.getBody() != null){

                PhysicalAssetRelationshipInstance<?> paRelInstance = physicalAssetRelationshipInstanceCreatedWldtEvent.getBody();

                if(paRelInstance.getTargetId() instanceof String){

                    String relName = paRelInstance.getRelationship().getName();
                    String relKey = paRelInstance.getKey();
                    String relTargetId = (String)paRelInstance.getTargetId();

                    DigitalTwinStateRelationshipInstance<String> instance = new DigitalTwinStateRelationshipInstance<String>(relName, relTargetId, relKey);

                    this.digitalTwinStateManager.addRelationshipInstance(instance);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPhysicalAssetRelationshipDeleted(PhysicalAssetRelationshipInstanceDeletedWldtEvent<?> physicalAssetRelationshipInstanceDeletedWldtEvent) {

    }

    //// Digital Action Received Callbacks ////

    @Override
    protected void onDigitalActionEvent(DigitalActionWldtEvent<?> digitalActionWldtEvent) {
        try {
            this.publishPhysicalAssetActionWldtEvent(digitalActionWldtEvent.getActionKey(), digitalActionWldtEvent.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
