# WLDT Library - Demo Digital Twin 

In this project we create a demo Digital Twin using the WLDT Library going through the following main steps: 

- Physical Adapter Creation: Implementation of a demo physical adapter emulating the connection with a physical object generating
sensors measurements data, events and accepting incoming actions
- Shadowing Function Implementation: Modeling and definition of the main shadowing function to shape the behaviour of the 
twin and its interaction with both physical and digital adapters
- Digital Adapter Creation: Definition of a demo digital adapter emulating the interaction with the
shadowing function to handle DT'State variation and notify external digital services (just logging at the moment) and 
managing emulated incomging digital actions that will be then handled by the shadowing function and then forward to the 
physical adapter for their execution on the physical asset

The current project version is aligned with WLDT Library version ```0.2.1``` and it is based on Mave import the Library 
in the``<dependencies></dependencies>`` tag using the following snippet:

```xml
<dependency>
    <groupId>io.github.wldt</groupId>
    <artifactId>wldt-core</artifactId>
    <version>0.4.0</version>
</dependency>
```

For a complete documentation on the WLDT library please point the official website or the main repository at the following address:
[https://github.com/wldt/wldt-core-java](https://github.com/wldt/wldt-core-java)

### Physical Adapter

The developer can use an existing Physical Adapter or create a new one to handle the communication with a specific physical twin. 
In this documentation we focus on the creation of a new Physical Adapter in order to explain library core functionalities. 
However, existing Physical Adapters can be found on the official repository and linked in the core documentation and webpage ([WLDT-GitHub](https://github.com/wldt)). 

In general WLDT Physical Adapter extends the class ``PhysicalAdapter`` and it is responsible to talk with the physical world and handling the following main tasks:
  - Generate a PAD describing the properties, events, actions and relationships available on the physical twin using the class ``PhysicalAssetDescription``
  - Generate Physical Event using the class ``PhysicalAssetEventWldtEvent`` associated to the variation of any aspect of the physical state (properties, events, and relationships)
  - Handle action request coming from the Digital World through the DT Shadowing Function by implementing the method ``onIncomingPhysicalAction`` and processing events modeled through the class ``PhysicalAssetActionWldtEvent``

Create a new class called ``DemoPhysicalAdapter`` extending the library class ``PhysicalAdapter`` and implement the following methods: 
- ``onAdapterStart``: A callback method used to notify when the adapter has been effectively started withing the DT's life cycle
- ``onAdapterStop``: A call method invoked when the adapter has been stopped and will be dismissed by the core
- ``onIncomingPhysicalAction``: The callback method called when a new ``PhysicalAssetActionWldtEvent`` is sent by the Shadowing Function upon the receiving of a valid Digital Action through a Digital Adapter

Then you have to create a constructor for your Physical Adapter with a single String parameter representing the id of the adapter. 
This id will be used internally by the library to handle and coordinate multiple adapters, adapts logs and execute functions upon the arrival of a new event. 
The resulting empty class will the following: 

```java
public class DemoPhysicalAdapter extends PhysicalAdapter {

    public DemoPhysicalAdapter(String id) {
        super(id);
    }

    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalAssetActionWldtEvent) {
        
    }

    @Override
    public void onAdapterStart() {

    }

    @Override
    public void onAdapterStop() {

    }
}
```
In our test Physical Adapter example we are going to emulate the communication with an Internet of Things device with the following sensing and actuation characteristics: 

- A Temperature Sensor generating data about new measurements
- The possibility to generate OVER-HEATING events
- An action to set the target desired temperature value

The first step will be to generate and publish the ``PhysicalAssetDescription`` (PAD) to describe the capabilities and the characteristics of our object allowing 
the Shadowing Function to decide how to digitalize its physical counterpart.
***Of course in our case the PAD is generated manually but according to the nature of the 
connected physical twin it can be automatically generated starting from a discovery or a configuration passed to the adapter.***

The generation of the PAD for each active Physical Adapter is the fundamental DT process to handle the binding procedure 
and to allow the Shadowing Function and consequently the core of the twin to be aware of what is available in the physical world and 
consequently decide what to observe and digitalize.

In order to publish the PAD we can update the onAdapterStart method with the following lines of code: 

```java
private final static String TEMPERATURE_PROPERTY_KEY = "temperature-property-key";
private final static String OVERHEATING_EVENT_KEY = "overheating-event-key";
private final static String SET_TEMPERATURE_ACTION_KEY = "set-temperatura-action-key";

@Override
public void onAdapterStart() {
    try {
        //Create an empty PAD
        PhysicalAssetDescription pad = new PhysicalAssetDescription();
        
        //Add a new Property associated to the target PAD with a key and a default value
        PhysicalAssetProperty<Double> temperatureProperty = new PhysicalAssetProperty<Double>(TEMPERATURE_PROPERTY_KEY, 0.0);
        pad.getProperties().add(temperatureProperty);
        
        //Add the declaration of a new type of generated event associated to a event key
        // and the content type of the generated payload
        PhysicalAssetEvent overheatingEvent = new PhysicalAssetEvent(OVERHEATING_EVENT_KEY, "text/plain");
        pad.getEvents().add(overheatingEvent);
        
        //Declare the availability of a target action characterized by a Key, an action type
        // and the expected content type and the request body
        PhysicalAssetAction setTemperatureAction = new PhysicalAssetAction(SET_TEMPERATURE_ACTION_KEY, "temperature.actuation", "text/plain");
        pad.getActions().add(setTemperatureAction);
        
        //Notify the new PAD to the DT's Shadowing Function
        this.notifyPhysicalAdapterBound(pad);
        
        //TODO add here the Device Emulation method 
        
    } catch (PhysicalAdapterException | EventBusException e) {
        e.printStackTrace();
    }
}
```

Now we need a simple code to emulate the generation of new temperature measurements and over-heating events.
In a real Physical Adapter implementation we have to implement the real communication with the physical twin in
order to read its state variation over time according to the supported protocols. 
In our simplified Physical Adapter we can the following function:

```java
private final static int MESSAGE_UPDATE_TIME = 1000;
private final static int MESSAGE_UPDATE_NUMBER = 10;
private final static double TEMPERATURE_MIN_VALUE = 20;
private final static double TEMPERATURE_MAX_VALUE = 30;

private Runnable deviceEmulation(){
    return () -> {
        try {

            //Sleep 5 seconds to emulate device startup
            Thread.sleep(5000);
            
            //Create a new random object to emulate temperature variations
            Random r = new Random();
            
            //Publish an initial Event for a normal condition
            publishPhysicalAssetEventWldtEvent(new PhysicalAssetEventWldtEvent<>(OVERHEATING_EVENT_KEY, "normal"));
            
            //Emulate the generation on 'n' temperature measurements
            for(int i = 0; i < MESSAGE_UPDATE_NUMBER; i++){

                //Sleep to emulate sensor measurement
                Thread.sleep(MESSAGE_UPDATE_TIME);
                
                //Update the 
                double randomTemperature = TEMPERATURE_MIN_VALUE + (TEMPERATURE_MAX_VALUE - TEMPERATURE_MIN_VALUE) * r.nextDouble(); 
                
                //Create a new event to notify the variation of a Physical Property
                PhysicalAssetPropertyWldtEvent<Double> newPhysicalPropertyEvent = new PhysicalAssetPropertyWldtEvent<>(TEMPERATURE_PROPERTY_KEY, randomTemperature);
                
                //Publish the WLDTEvent associated to the Physical Property Variation
                publishPhysicalAssetPropertyWldtEvent(newPhysicalPropertyEvent);
            }
            
            //Publish a demo Physical Event associated to a 'critical' overheating condition
            publishPhysicalAssetEventWldtEvent(new PhysicalAssetEventWldtEvent<>(OVERHEATING_EVENT_KEY, "critical"));

        } catch (EventBusException | InterruptedException e) {
            e.printStackTrace();
        }
    };
}
```

Now we have to call the ``deviceEmulationFunction()`` inside the ``onAdapterStart()`` triggering its execution and emulating the physical counterpart of our DT.
To do that add the following line at the end of the ``onAdapterStart()`` method after the ``this.notifyPhysicalAdapterBound(pad);``.

The last step will be to handle an incoming action trying to set a new temperature on the device by implementing the method ``onIncomingPhysicalAction()``.
This method will receive a ``PhysicalAssetActionWldtEvent<?> physicalAssetActionWldtEvent`` associated to the action request generated by the shadowing function. 
Since a Physical Adapter can handle multiple action we have to check both ``action-key`` and ``body`` type in order to properly process the action (in our case just logging the request).
The new update method will result like this:

```java
@Override
public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalAssetActionWldtEvent) {
    try{

        if(physicalAssetActionWldtEvent != null
                && physicalAssetActionWldtEvent.getActionKey().equals(SET_TEMPERATURE_ACTION_KEY)
                && physicalAssetActionWldtEvent.getBody() instanceof String) {
            System.out.println("Received Action Request: " + physicalAssetActionWldtEvent.getActionKey()
                    + " with Body: " + physicalAssetActionWldtEvent.getBody());
        }
        else
            System.err.println("Wrong Action Received !");

    }catch (Exception e){
        e.printStackTrace();
    }
}
```

The overall class will result as following: 

```java
import it.wldt.adapter.physical.*;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.exception.EventBusException;
import it.wldt.exception.PhysicalAdapterException;

import java.util.Random;

public class DemoPhysicalAdapter extends PhysicalAdapter {

    private final static String TEMPERATURE_PROPERTY_KEY = "temperature-property-key";
    private final static String OVERHEATING_EVENT_KEY = "overheating-event-key";
    private final static String SET_TEMPERATURE_ACTION_KEY = "set-temperature-action-key";

    private final static int MESSAGE_UPDATE_TIME = 1000;
    private final static int MESSAGE_UPDATE_NUMBER = 10;
    private final static double TEMPERATURE_MIN_VALUE = 20;
    private final static double TEMPERATURE_MAX_VALUE = 30;

    public DemoPhysicalAdapter(String id) {
        super(id);
    }

    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalAssetActionWldtEvent) {
        try{

            if(physicalAssetActionWldtEvent != null
                    && physicalAssetActionWldtEvent.getActionKey().equals(SET_TEMPERATURE_ACTION_KEY)
                    && physicalAssetActionWldtEvent.getBody() instanceof Double) {
                System.out.println("Received Action Request: " + physicalAssetActionWldtEvent.getActionKey()
                        + " with Body: " + physicalAssetActionWldtEvent.getBody());
            }
            else
                System.err.println("Wrong Action Received !");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAdapterStart() {
        try {
            //Create an empty PAD
            PhysicalAssetDescription pad = new PhysicalAssetDescription();

            //Add a new Property associated to the target PAD with a key and a default value
            PhysicalAssetProperty<Double> temperatureProperty = new PhysicalAssetProperty<Double>(GlobalKeywords.TEMPERATURE_PROPERTY_KEY, 0.0);
            pad.getProperties().add(temperatureProperty);

            //Add the declaration of a new type of generated event associated to a event key
            // and the content type of the generated payload
            PhysicalAssetEvent overheatingEvent = new PhysicalAssetEvent(GlobalKeywords.OVERHEATING_EVENT_KEY, "text/plain");
            pad.getEvents().add(overheatingEvent);

            //Declare the availability of a target action characterized by a Key, an action type
            // and the expected content type and the request body
            PhysicalAssetAction setTemperatureAction = new PhysicalAssetAction(GlobalKeywords.SET_TEMPERATURE_ACTION_KEY, "temperature.actuation", "text/plain");
            pad.getActions().add(setTemperatureAction);

            //Notify the new PAD to the DT's Shadowing Function
            this.notifyPhysicalAdapterBound(pad);

            //Start Device Emulation
            new Thread(deviceEmulation()).start();

        } catch (PhysicalAdapterException | EventBusException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAdapterStop() {

    }

    private Runnable deviceEmulation(){
        return () -> {
            try {

                //Sleep 5 seconds to emulate device startup
                Thread.sleep(5000);

                //Create a new random object to emulate temperature variations
                Random r = new Random();

                //Publish an initial Event for a normal condition
                publishPhysicalAssetEventWldtEvent(new PhysicalAssetEventWldtEvent<>(GlobalKeywords.OVERHEATING_EVENT_KEY, "normal"));

                //Emulate the generation on 'n' temperature measurements
                for(int i = 0; i < GlobalKeywords.MESSAGE_UPDATE_NUMBER; i++){

                    //Sleep to emulate sensor measurement
                    Thread.sleep(GlobalKeywords.MESSAGE_UPDATE_TIME);

                    //Update the
                    double randomTemperature = GlobalKeywords.TEMPERATURE_MIN_VALUE + (GlobalKeywords.TEMPERATURE_MAX_VALUE - GlobalKeywords.TEMPERATURE_MIN_VALUE) * r.nextDouble();

                    //Create a new event to notify the variation of a Physical Property
                    PhysicalAssetPropertyWldtEvent<Double> newPhysicalPropertyEvent = new PhysicalAssetPropertyWldtEvent<>(GlobalKeywords.TEMPERATURE_PROPERTY_KEY, randomTemperature);

                    //Publish the WLDTEvent associated to the Physical Property Variation
                    publishPhysicalAssetPropertyWldtEvent(newPhysicalPropertyEvent);
                }

                //Publish a demo Physical Event associated to a 'critical' overheating condition
                publishPhysicalAssetEventWldtEvent(new PhysicalAssetEventWldtEvent<>(GlobalKeywords.OVERHEATING_EVENT_KEY, "critical"));

            } catch (EventBusException | InterruptedException e) {
                e.printStackTrace();
            }
        };
    }
}
```

Both Physical Adapters and Digital Adapters can be defined natively with a custom configuration provided by the developer
as illustrated in the dedicated Section: [Configurable Physical & Digital Adapters](#configurable-physical-and-digital-adapters).

### Shadowing Function

After the definition of the Physical Adapter it is time to start implementing the core of our DT through the definition of 
its shadowing function in charge of: 

- Handle received PAD from Physical Adapters in order to device which properties, events, relationships or actions available on connected physical twins should be mapped and managed into the DT State
- Manage incoming notifications/callbacks associated to the variation of physical properties (e.g, temperature variation) or the generation of physical event (e.g., overheating) 
- Process action requests from the digital world that should be validated and forward to the correct Physical Adapter in order to trigger the associated actions on the physical world 

The Shadowing Function has the responsibility to build and maintain the updated state of the Digital Twin
The internal variable of any WLDT Shadowing Function (available through the base class `ShadowingFunction`) used to do that is ```DigitalTwinStateManager``` accessible through the variable: `this.digitalTwinStateManager`

When the Shadowing Function has to compute the new DT State it can now work with the following method to handle DT State Transition:
- Start the DT State Transaction:  `startStateTransaction()`
- DT State variation methods such as:
  - `createProperty()`
  - `updateProperty()`  
  - `updatePropertyValue()`
  - `deleteProperty()`
  - `enableAction()`
  - `updateAction()`
  - `disableAction()`
  - `registerEvent()`
  - `updateRegisteredEvent()`
  - `unRegisterEvent()`
  - `createRelationship()`
  - `addRelationshipInstance()`
  - `deleteRelationship()`
  - `deleteRelationshipInstance()`
- At the end the transaction can be committed using the method: `commitStateTransaction()`

To access the current DT State the Shadowing Function implementation can use the method `this.digitalTwinStateManager.getDigitalTwinState()`
The information available on the DT State are: 

- `properties`: List of Properties with their values (if available)
- `actions`: List of Actions that can be called on the DT
- `events`: List of Events that can be generated by the DT
- `relationships`: List of Relationships and their instances (if available)
- `evaluationInstant`: The timestamp representing the evaluation instant of the DT state

Available main methods on that class instance are: 

- Properties: 
  - ```getProperty(String propertyKey)```: Retrieves if present the target DigitalTwinStateProperty by Key
  - ```containsProperty(String propertyKey)```: Checks if a target Property Key is already available in the current Digital Twin's State
  - ```getPropertyList()```: Loads the list of available Properties (described by the class DigitalTwinStateProperty) available on the Digital Twin's State
  - ```createProperty(DigitalTwinStateProperty<?> dtStateProperty)```: Allows the creation of a new Property on the Digital Twin's State through the class DigitalTwinStateProperty
  - ```readProperty(String propertyKey)```: Retrieves if present the target DigitalTwinStateProperty by Key
  - ```updateProperty(DigitalTwinStateProperty<?> dtStateProperty)```: Updates the target property using the DigitalTwinStateProperty and the associated Property Key field
  - ```deleteProperty(String propertyKey)```: Deletes the target property identified by the specified key
- Actions: 
  - ```containsAction(String actionKey)```: Checks if a Digital Twin State Action with the specified key is correctly registered
  - ```getAction(String actionKey)```: Loads the target DigitalTwinStateAction by key
  - ```getActionList()```: Gets the list of available Actions registered on the Digital Twin's State
  - ```enableAction(DigitalTwinStateAction digitalTwinStateAction)```: Enables and registers the target Action described through an instance of the DigitalTwinStateAction class
  - ```updateAction(DigitalTwinStateAction digitalTwinStateAction)```: Update the already registered target Action described through an instance of the DigitalTwinStateAction class
  - ```disableAction(String actionKey)```: Disables and unregisters the target Action described through an instance of the DigitalTwinStateAction class
- Events:
  - ```containsEvent(String eventKey)```: Check if a Digital Twin State Event with the specified key is correctly registered
  - ```getEvent(String eventKey)```: Return the description of a registered Digital Twin State Event according to its Key
  - ```getEventList()```: Return the list of existing and registered Digital Twin State Events
  - ```registerEvent(DigitalTwinStateEvent digitalTwinStateEvent)```: Register a new Digital Twin State Event
  - ```updateRegisteredEvent(DigitalTwinStateEvent digitalTwinStateEvent)```: Update the registration and signature of an existing Digital Twin State Event
  - ```unRegisterEvent(String eventKey)```: Un-register a Digital Twin State Event
  - ```notifyDigitalTwinStateEvent(DigitalTwinStateEventNotification<?> digitalTwinStateEventNotification)```: Method to notify the occurrence of the target Digital Twin State Event
- Relationships:
  - ```containsRelationship(String relationshipName)```: Checks if a Relationship Name is already available in the current Digital Twin's State
  - ```createRelationship(DigitalTwinStateRelationship<?> relationship)```: Creates a new Relationships (described by the class DigitalTwinStateRelationship) in the Digital Twin's State
  - ```addRelationshipInstance(String name, DigitalTwinStateRelationshipInstance<?> instance)```: Adds a new Relationship instance described through the class DigitalTwinStateRelationshipInstance and identified through its name
  - ```getRelationshipList()```: Loads the list of existing relationships on the Digital Twin's State through a list of DigitalTwinStateRelationship
  - ```getRelationship(String name)```: Gets a target Relationship identified through its name and described through the class DigitalTwinStateRelationship
  - ```deleteRelationship(String name)```: Deletes a target Relationship identified through its name
  - ```deleteRelationshipInstance(String relationshipName, String instanceKey)```: Deletes the target Relationship Instance using relationship name and instance Key

The basic library class that we are going to extend is called ```ShadowingFunction``` and creating a new class named ```DemoShadowingFunction``` the resulting 
code is the same after implementing required methods the basic constructor with the id String parameter. 

```java
import it.wldt.adapter.digital.event.DigitalActionWldtEvent;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent;
import it.wldt.core.model.ShadowingModelFunction;
import java.util.Map;

public class DemoShadowingFunction extends ShadowingModelFunction {

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

    }

    //// Physical Event Notification Callback ////

    @Override
    protected void onPhysicalAssetEventNotification(PhysicalAssetEventWldtEvent<?> physicalAssetEventWldtEvent) {

    }

    //// Physical Relationships Notification Callbacks ////

    @Override
    protected void onPhysicalAssetRelationshipEstablished(PhysicalAssetRelationshipInstanceCreatedWldtEvent<?> physicalAssetRelationshipInstanceCreatedWldtEvent) {

    }

    @Override
    protected void onPhysicalAssetRelationshipDeleted(PhysicalAssetRelationshipInstanceDeletedWldtEvent<?> physicalAssetRelationshipInstanceDeletedWldtEvent) {

    }

    //// Digital Action Received Callbacks ////

    @Override
    protected void onDigitalActionEvent(DigitalActionWldtEvent<?> digitalActionWldtEvent) {

    }
}
```

The methods ```onCreate()```, ```onStart()``` and ```onStop()``` are used to receive callbacks from the DT's core when the Shadowing Function has been effectively created within the twin, is started or stopped 
according to the evolution of its life cycle. In our initial implementation we are not implementing any of them but they can be useful to trigger specific behaviours according to the different phases.

The first method that we have to implement in order to analyze received PAD and build the Digital Twin State in terms of properties, events, relationships and available actions is 
the ```onDigitalTwinBound(Map<String, PhysicalAssetDescription> map)``` method. In our initial implementation we just pass through all the received characteristics recevied from each connected 
Physical Adapter mapping every physical entity into the DT's state without any change or adaptation (Of course complex behaviour can be implemented to customized the digitalization process). 

Through the following method we implement the following behaviour:

- Analyze each received PAD from each connected and active Physical Adapter (in our case we will have just 1 Physical Adapter)
- Iterate over all the received Properties for each PAD and create the same Property on the Digital Twin State
- Start observing target Physical Properties in order to receive notification callback about physical variation through the method ```observePhysicalAssetProperty(property);```
- Analyze received PAD's Events declaration and recreates them also on the DT's State
- Start observing target Physical Event in order to receive notification callback about physical event generation through the method ```observePhysicalAssetEvent(event);```
- Check available Physical Action and enable them on the DT's State. Enabled Digital Action are automatically observed by the Shadowing Function in order to receive action requests from active Digital Adapters

The possibility to manually observe Physical Properties and Event has been introduced to allow the Shadowing Function to decide what 
to do according to the nature of the property or of the target event. For example in some cases with static properties it will not be necessary 
to observe any variation, and it will be enough to read the initial value to build the digital replica of that specific property.

Since the DT State is managed through the `DigitalTwinStateManager` class all the changes and variation should be applied on the 
DT ShadowingFunction using the previously presented transaction management and the correct call of methods `startStateTransaction()` and `commitStateTransaction()`.

```java
@Override
protected void onDigitalTwinBound(Map<String, PhysicalAssetDescription> adaptersPhysicalAssetDescriptionMap) {

    try{

      // NEW from 0.3.0 -> Start DT State Change Transaction  
      this.digitalTwinStateManager.startStateTransaction();

      //Iterate over all the received PAD from connected Physical Adapters
        adaptersPhysicalAssetDescriptionMap.values().forEach(pad -> {

            //Iterate over all the received PAD from connected Physical Adapters
            adaptersPhysicalAssetDescriptionMap.values().forEach(pad -> {
                pad.getProperties().forEach(property -> {
                try {
                    
                    //Create and write the property on the DT's State
                    this.digitalTwinState.createProperty(new DigitalTwinStateProperty<>(property.getKey(),(Double) property.getInitialValue()));
            
                    //Start observing the variation of the physical property in order to receive notifications
                    //Without this call the Shadowing Function will not receive any notifications or callback about
                    //incoming physical property of the target type and with the target key
                    this.observePhysicalAssetProperty(property);
        
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            //Iterate over available declared Physical Events for the target Physical Adapter's PAD
            pad.getEvents().forEach(event -> {
                try {

                    //Instantiate a new DT State Event with the same key and type
                    DigitalTwinStateEvent dtStateEvent = new DigitalTwinStateEvent(event.getKey(), event.getType());

                    //Create and write the event on the DT's State
                    this.digitalTwinState.registerEvent(dtStateEvent);

                    //Start observing the variation of the physical event in order to receive notifications
                    //Without this call the Shadowing Function will not receive any notifications or callback about
                    //incoming physical events of the target type and with the target key
                    this.observePhysicalAssetEvent(event);

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
                    this.digitalTwinState.enableAction(dtStateAction);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        });
            
        // NEW from 0.3.0 -> Commit DT State Change Transaction to apply the changes on the DT State and notify about the change
        this.digitalTwinStateManager.commitStateTransaction();

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
```

In particular the method ```observeDigitalActionEvents()``` should be called start the observation of digital actions and 
to receive all incoming Digital Action through active Digital Adapters. 
Without this call the Shadowing Function will not receive any notifications or callback about 
incoming request to execute an exposed DT's Action. Of course, we have to call this method if we are mapping any digital 
action in our DT. 

Another fundamental method is ```notifyShadowingSync()``` used to notify the DT Core that 
the Bounding phase has been correctly completed and the DT has evaluated its  internal status according 
to what is available and declared through the Physical Adapters.

As mentioned, in the previous example the Shadowing Function does not apply any control or check on the nature of declared 
physical property. Of course in order to have a more granular control, it will be possible to use property ``Key`` or any other field or even
the type of the instance through an ```instanceof``` check to implement different controls and behaviours.

A variation (only for the property management code) to the previous method can be the following:

```java
//Iterate over available declared Physical Property for the target Physical Adapter's PAD 
pad.getProperties().forEach(property -> {
    try {

        //Check property Key and Instance of to validate that is a Double
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
```

The next method that we have to implement in order to properly define and implement the behaviour of our DT through its
ShadowingModelFunction are: 

- ```onPhysicalAssetPropertyVariation```: Method called when a new variation for a specific Physical Property has been detected
by the associated Physical Adapter. The method receive as parameter a specific WLDT Event called ```PhysicalAssetPropertyWldtEvent<?> physicalPropertyEventMessage``` 
containing all the information generated by the Physical Adapter upon the variation of the monitored physical counterpart.
- ```onPhysicalAssetEventNotification```: Callback method used to be notified by a PhysicalAdapter about the generation of a Physical Event.
As for the previous method, also this function receive a WLDT Event parameter of type ```onPhysicalAssetEventNotification(PhysicalAssetEventWldtEvent<?> physicalAssetEventWldtEvent)```)
containing all the field of the generated physical event.
- ```onDigitalActionEvent```: On the opposite this method is triggered from one of the active Digital Adapter when an Action request has been received on the Digital Interface. 
The method receive as parameter an instance of the WLDT Event class ```DigitalActionWldtEvent<?> digitalActionWldtEvent``` describing the target digital action request and the associated
body.

For the ```onPhysicalAssetPropertyVariation``` a simple implementation in charge ONLY of mapping the new Physical Property value 
into the corresponding DT'State property can be implemented as follows: 

The DT State transaction management should be applied in the point of the code where the 
Shadowing Function receive a variation from the Physical world through a target adapter and the callback method `onPhysicalAssetPropertyVariation(...)`

```java
@Override
protected void onPhysicalAssetPropertyVariation(PhysicalAssetPropertyWldtEvent<?> physicalPropertyEventMessage) {
    try {

      //Update Digital Twin State  
      //NEW from 0.3.0 -> Start State Transaction
      this.digitalTwinStateManager.startStateTransaction();

      this.digitalTwinState.updateProperty(new DigitalTwinStateProperty<>(physicalPropertyEventMessage.getPhysicalPropertyId(), physicalPropertyEventMessage.getBody()));

      //NEW from 0.3.0 -> Commit State Transaction
      this.digitalTwinStateManager.commitStateTransaction();
      
    } catch (WldtDigitalTwinStatePropertyException | WldtDigitalTwinStatePropertyBadRequestException | WldtDigitalTwinStatePropertyNotFoundException | WldtDigitalTwinStateException e) {
        e.printStackTrace();
    }
}
```

In this case as reported in the code, we call the method ```this.digitalTwinState.updateProperty``` on the Shadowing Function 
in order to update an existing DT'State property (previously created in the ```onDigitalTwinBound``` method). 
To update the value we directly use the received data on the ```PhysicalAssetPropertyWldtEvent``` without any additional check 
or change that might be instead needed in advanced examples.

Following the same principle, a simplified digital mapping between physical and digital state upon the receving of a physical event variation can be the following: 

```java
@Override
protected void onPhysicalAssetEventNotification(PhysicalAssetEventWldtEvent<?> physicalAssetEventWldtEvent) {
    try {
        this.digitalTwinStateManager.notifyDigitalTwinStateEvent(new DigitalTwinStateEventNotification<>(physicalAssetEventWldtEvent.getPhysicalEventKey(), physicalAssetEventWldtEvent.getBody(), physicalAssetEventWldtEvent.getCreationTimestamp()));
    } catch (WldtDigitalTwinStateEventNotificationException | EventBusException e) {
        e.printStackTrace();
    }
}
```

With respect to events management, we use the Shadowint Function method ```this.digitalTwinState.notifyDigitalTwinStateEvent``` to notify
the other DT Components (e.g., Digital Adapters) the incoming Physical Event by creating a new instance of a ```DigitalTwinStateEventNotification``` class
containing all the information associated to the event. Of course, additional controls and checks can be introduced in this
method validating and processing the incoming physical message to define complex behaviours.

The last method that we are going to implement is the ```onDigitalActionEvent``` one where we have to handle an incoming
Digital Action request associated to an Action declared on the DT's State in the ```onDigitalTwinBound``` method.
In that case the Digital Action should be forwarded to the Physical Interface in order to be sent to the physical counterpart
for the effective execution. 

```java
@Override
protected void onDigitalActionEvent(DigitalActionWldtEvent<?> digitalActionWldtEvent) {
    try {
        this.publishPhysicalAssetActionWldtEvent(digitalActionWldtEvent.getActionKey(), digitalActionWldtEvent.getBody());
    } catch (EventBusException e) {
        e.printStackTrace();
    }
}
```

Also in that case we are forwarding the incoming Digital Action request described through the class ```DigitalActionWldtEvent```
to the Physical Adapter with the method of the Shadowing Function denoted as ```this.publishPhysicalAssetActionWldtEvent``` and
passing directly the action key and the target Body. No additional processing or validation have been introduced here, but they might
be required in advanced scenario in order to properly adapt incoming digital action request to what is effectively expected on the 
physical counterpart.

### Digital Adapter 

The las component that we have to implement to complete our first simple Digital Twin definition through the WLDT library is a
Digital Adapter in charge of: 

- Receiving event from the DT's Core related to the variation of properties, events, available actions and relationships
- Expose received information to the external world according to its implementation and the supported protocol
- Handle incoming digital action and forward them to the Core in order to be validated and processed by the Shadowing Function

The basic library class that we are going to extend is called ```DigitalAdapter``` and creating a new class 
named ```DemoDigitalAdapter```. The DigitalTwinAdapter class can take as Generic Type the type of Configuration used to configure its behaviours.
In this simplified example we are defining a DigitalAdapter without any Configuration.

A Digital Adapter has direct access to the current DT's State through callbacks or directly in a synchronous way using the 
internal variable called: ```digitalTwinState```. Through it is possibile to navigate all the fields currently composing the state of our Digital Twin.

The Digital Adapter class has e long list of callback and notification method to allow 
the adapter to be updated about all the variation and changes on the twin.
Available callbacks can be summarized as follows:

- Digital Adapter Start/Stop:
    - ```onAdapterStart()```: Feedback when the Digital Adapter correctly starts 
    - ```onAdapterStop()```: Feedback when the Digital Adapter has been stopped
- Digital Twin Life Cycle Notifications:
    - ```onDigitalTwinCreate()```: The DT has been created
    - ```onDigitalTwinStart()```: The DT started
    - ```onDigitalTwinSync(IDigitalTwinState digitalTwinState)```: The DT is Synchronized with its physical counterpart. 
    The current DigitalTwinState is passed as parameter to allow the Digital Adapter to know the current state and consequently
    implement its behaviour
    - ```onDigitalTwinUnSync(IDigitalTwinState digitalTwinState)```: The DT is not synchronized anymore with its physical counterpart.
      The last current DigitalTwinState is passed as parameter to allow the Digital Adapter to know the last state and consequently
      implement its behaviour
    - ```onDigitalTwinStop()```: The DT is stopped
    - ```onDigitalTwinDestroy()```: The DT has been destroyed and the application stopped

The Digital Adapter DT State variations and DT events are received by the Adapter from the DT core belongs to the following categories:

- **Digital Twin State Update** through the method `onStateUpdate(...)` providing information about the new state of the Digital Twin, 
the previous state, and a list of changes that occurred between these two states. In the previous version each variation of a property, 
relationships, actions or events were notified. In the new version only a committed DT'State variation is notified to listeners.
- **Event Notifications** through the method `onEventNotificationReceived(...)` whenever there is a notification about an event related to the 
Digital Twin's state coming from the physical world, generated by the twin and processed by the Shadowing Function. 
For example in the DT State we can have the declaration of the `over-heating-alert` structured and received in the 
DT State while the effective occurrence of the event and the associated notification is notified through this dedicated callback

The `onStateUpdate` method is an abstract method that must be implemented by any class extending the `DigitalAdapter` class. 
This method is called whenever there is an update to the Digital Twin's state. It provides information about the new state of the Digital Twin, 
the previous state, and a list of changes that occurred between these two states.

The explanation of the parameters is the following:

1. `newDigitalTwinState`: This parameter represents the updated state of the Digital Twin. It is an instance of the `DigitalTwinState` class, which encapsulates the current state information.
2. `previousDigitalTwinState`: This parameter represents the state of the Digital Twin before the update. It is also an instance of the `DigitalTwinState` class.
3. `digitalTwinStateChangeList`: This parameter is an `ArrayList` containing `DigitalTwinStateChange` objects. Each `DigitalTwinStateChange` object encapsulates information about a specific change that occurred between the previous and new states. It includes details such as the property or aspect of the state that changed, the previous value, and the new value.

Another core method where a Digital Adapter receive the description of the DT'State is ```onDigitalTwinSync(IDigitalTwinState digitalTwinState)```. 
The Adapter using the parameter ```digitalTwinState``` can analyze available properties, actions, events and relationships and decide how to implement its internal behaviour with the methods presented in [ShadowingFunction](#shadowing-function).
The DT State is automatically monitored by each Digital Adapter while for the Events potentially generated by the DT can be observed by each adapter using: 

- `observeAllDigitalTwinEventsNotifications`: Enable the observation of available Digital Twin State Events Notifications.
- `unObserveAllDigitalTwinEventsNotifications`: Cancel the observation of Digital Twin State Events Notifications
- `observeDigitalTwinEventsNotifications`: Enable the observation of the notification associated to a specific list of Digital Twin State events. With respect to event a notification contains the new associated value
- `unObserveDigitalTwinEventsNotifications`: Cancel the observation of a target list of properties
- `observeDigitalTwinEventNotification`: Enable the observation of the notification associated to a single Digital Twin State event. With respect to event a notification contains the new associated value
- `unObserveDigitalTwinEventNotification`: Cancel the observation of a single target event

The resulting code will be the following after adding the required
methods (still empty) and the basic constructor with the id String parameter is the following:

```java
import it.wldt.adapter.digital.DigitalAdapter;
import it.wldt.core.state.*;

public class DemoDigitalAdapter extends DigitalAdapter<Void> {

    public DemoDigitalAdapter(String id) {
        super(id);
    }

    /**
     * Callback to notify the adapter on its correct startup
     */
    @Override
    public void onAdapterStart() {}

    /**
     * Callback to notify the adapter that has been stopped
     */
    @Override
    public void onAdapterStop() {}

    /**
     * DT Life Cycle notification that the DT is correctly on Sync
     * @param digitalTwinState
     */
    @Override
    public void onDigitalTwinSync(DigitalTwinState digitalTwinState) {}

    /**
     * DT Life Cycle notification that the DT is currently Not Sync
     * @param digitalTwinState
     */
    @Override
    public void onDigitalTwinUnSync(DigitalTwinState digitalTwinState) {}

    /**
     * DT Life Cycle notification that the DT has been created
     */
    @Override
    public void onDigitalTwinCreate() {}

    /**
     * DT Life Cycle Notification that the DT has correctly Started
     */
    @Override
    public void onDigitalTwinStart() {}

    /**
     * DT Life Cycle Notification that the DT has been stopped
     */
    @Override
    public void onDigitalTwinStop() {}

    /**
     * DT Life Cycle Notification that the DT has destroyed
     */
    @Override
    public void onDigitalTwinDestroy() {}

    /**
     * Callback method allowing the Digital Adapter to receive the updated Digital Twin State together with
     * the previous state and the list of applied changes
     *
     * @param newDigitalTwinState The new Digital Twin State computed by the Shadowing Function
     * @param previousDigitalTwinState The previous Digital Twin State
     * @param digitalTwinStateChangeList The list of applied changes to compute the new Digital Twin State
     */
    @Override
    protected void onStateUpdate(DigitalTwinState newDigitalTwinState, DigitalTwinState previousDigitalTwinState, ArrayList<DigitalTwinStateChange> digitalTwinStateChangeList) {}
  
    /**
     * Callback method to receive a new computed Event Notification (associated to event declared in the DT State)
     *
     * @param digitalTwinStateEventNotification The generated Notification associated to a DT Event
     */
    @Override
    protected void onEventNotificationReceived(DigitalTwinStateEventNotification<?> digitalTwinStateEventNotification) {}
}
```

By default, a Digital Adapter observes all the variation on the DT's State in terms of Properties, Relationships, Actions and Events.
As previously mentioned the observation of DT's State Properties allows to receive also properties variation on the method since a property is natively composed by its description (e.g., type) and its 
current value. On the opposite the observation on DT's State Action, Relationships and Events allow ONLY to receive callbacks when a new entity is added or an update is occurred without receiving updates on values variation.

The only thing that we should add in the ```onDigitalTwinSync(IDigitalTwinState currentDigitalTwinState)``` callback is the direct observation for Events.
Following this approach we can change our Digital Adapter in the following methods: 

In ```onDigitalTwinSync``` we observe in this first simple implementation only the incoming values for declared Events in the DT'State.
As previously mentioned the observation of any variation of the State structure together with Properties Values are by default observed by any Digital Adapter.
In this method we use the internal variable ```digitalTwinState``` to access the DT's state and find available Events declaration that we would like to observe.

````java
public void onDigitalTwinSync(IDigitalTwinState currentDigitalTwinState) {

      try {
          
          //Retrieve the list of available events and observe all variations
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

      } catch (Exception e) {
          e.printStackTrace();
      }

  }
````

Developers extending the `DigitalAdapter` class should implement the `onStateUpdate` method to define custom logic that 
needs to be executed whenever the state of the Digital Twin is updated. This could include tasks such as processing state changes, updating internal variables, triggering specific actions, or notifying other components about the state update.

Here's an example of how the method might be implemented in a concrete subclass of `DigitalAdapter`:

```java
@Override
protected void onStateUpdate(DigitalTwinState newDigitalTwinState,
                              DigitalTwinState previousDigitalTwinState,
                              ArrayList<DigitalTwinStateChange> digitalTwinStateChangeList) {

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
```

In this example, the method iterates over the list of state changes, extracts information about each change, and performs custom actions based on the changes. 
Developers can adapt this method to suit the specific requirements of their Digital Twin application.

## Digital Twin Process

Now that we have created the main fundamental element of a DT (Physical Adapter, Shadowing Function and Digital Adapter) we can create Class file with a main to create the WLDT Engine
with the created components and start the DT. 

Create a new Java file called ```DemoDigitalTwin``` adding the following code: 

With the following code we now create a new Digital Twin Instance

```java
// Create the new Digital Twin with its Shadowing Function  
DigitalTwin digitalTwin = new DigitalTwin(digitalTwinId, new DemoShadowingFunction());  
  
// Physical Adapter with Configuration  
digitalTwin.addPhysicalAdapter(  
        new DemoPhysicalAdapter(  
                String.format("%s-%s", digitalTwinId, "test-physical-adapter"),  
                new DemoPhysicalAdapterConfiguration(),  
                true));  
  
// Digital Adapter with Configuration  
digitalTwin.addDigitalAdapter(  
        new DemoDigitalAdapter(  
                String.format("%s-%s", digitalTwinId, "test-digital-adapter"),  
                new DemoDigitalAdapterConfiguration())  
);
```

DTs cannot be directly run but it should be added to the `DigitalTwinEngine` in order to be executed through the WLDT Library

```java
// Create the Digital Twin Engine
DigitalTwinEngine digitalTwinEngine = new DigitalTwinEngine();  

// Add the Digital Twin to the Engine
digitalTwinEngine.addDigitalTwin(digitalTwin);
```

In order to start a DT from the Engine you can:

```java
// Directly start when you add it passing a second boolean value = true
digitalTwinEngine.addDigitalTwin(digitalTwin. true);

// Starting the single DT on the engine through its id
digitalTwinEngine.startDigitalTwin(DIGITAL_TWIN_ID);

// Start all the DTs registered on the engine
digitalTwinEngine.startAll();
```

To stop a single twin or all the twin registered on the engine:

```java
// Stop a single DT on the engine through its id
digitalTwinEngine.stopDigitalTwin(DIGITAL_TWIN_ID);

// Stop all the DTs registered on the engine
digitalTwinEngine.stopAll();
```

It is also possible to remove a DT from the Engine with a consequent stop if it is active and the deletion of its reference from the engine:

```java
// Remove a single DT on the engine through its id
digitalTwinEngine.removeDigitalTwin(DIGITAL_TWIN_ID);

// Remove all the DTs registered on the engine
digitalTwinEngine.removeAll();
```

The resulting code in our case is: 

```java
public class DemoDigitalTwin {

    public static void main(String[] args)  {
        try{

            // Create the new Digital Twin
            DigitalTwin digitalTwin = new DigitalTwin(
                    "test-dt-id",
                    new DemoShadowingFunction("test-shadowing-function")
            );

            //Default Physical and Digital Adapter
            //digitalTwin.addPhysicalAdapter(new DemoPhysicalAdapter("test-physical-adapter"));
            //digitalTwin.addDigitalAdapter(new DemoDigitalAdapter("test-digital-adapter"));

            //Physical and Digital Adapters with Configuration
            digitalTwin.addPhysicalAdapter(new DemoConfPhysicalAdapter("test-physical-adapter", new DemoPhysicalAdapterConfiguration()));
            digitalTwin.addDigitalAdapter(new DemoConfDigitalAdapter("test-digital-adapter", new DemoDigitalAdapterConfiguration()));

            // Create the Digital Twin Engine
            DigitalTwinEngine digitalTwinEngine = new DigitalTwinEngine();

            // Add the Digital Twin to the Engine
            digitalTwinEngine.addDigitalTwin(digitalTwin);

            // Set a new Event-Logger to a Custom One that we created with the class 'DemoEventLogger'
            WldtEventBus.getInstance().setEventLogger(new DemoEventLogger());

            // Start all the DTs registered on the engine
            digitalTwinEngine.startAll();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

## Digital Action Management

In this demo implementation, we are going to emulate an incoming Digital Action on the Digital Adapter in order to show how it can be handled by the adapter and 
properly forwarded to the Shadowing Function for validation and the consequent interaction with the Physical Adapter and then with the physical twin.

In order to add a demo Digital Action trigger on the Digital Adapter we add the following method to the ```DemoDigitalAdapter``` class:

```java
private Runnable emulateIncomingDigitalAction(){
    return () -> {
        try {

            System.out.println("Sleeping before Emulating Incoming Digital Action ...");
            Thread.sleep(5000);
            Random random = new Random();

            //Emulate the generation on 'n' temperature measurements
            for(int i = 0; i < 10; i++){

                //Sleep to emulate sensor measurement
                Thread.sleep(1000);

                double randomTemperature = 25.0 + (30.0 - 25.0) * random.nextDouble();
                publishDigitalActionWldtEvent("set-temperature-action-key", randomTemperature);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
```

This method uses the Digital Adapter internal function denoted as ```publishDigitalActionWldtEvent(String actionKey, T body)``` allowing the adapter 
to send a notification to the DT's Core (and consequently the Shadowing Function) about the arrival of a Digital Action with a specific ```key``` and ```body```.
In our case the ```key``` is ```set-temperature-action-key``` as declared in the Physical Adapter and in the PAD and the value is a simple Double with the new temperature value. 

Then we call this method in the following way at the end ot the ```onDigitalTwinSync(IDigitalTwinState currentDigitalTwinState)``` method.

```java
//Start Digital Action Emulation
new Thread(emulateIncomingDigitalAction()).start();
```

Now the Shadowing Function should be updated in order to handle the incoming Action request from the Digital Adapter.
In our case the shadowing function does not apply any validation or check and just forward to action to the Physical Adapter
in order to be then forwarded to the physical twin. Of course advanced implementation can be introduced for example to 
validate action, adapt payload and data-formats or to augment functionalities (e.g., trigger multiple physical actions from a single digital request).

In our simple demo implementation the updated Shadowing Function method ```onDigitalActionEvent(DigitalActionWldtEvent<?> digitalActionWldtEvent)``` results as follows:

```java
@Override
protected void onDigitalActionEvent(DigitalActionWldtEvent<?> digitalActionWldtEvent) {
    try {
        this.publishPhysicalAssetActionWldtEvent(digitalActionWldtEvent.getActionKey(), digitalActionWldtEvent.getBody());
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

This forwarding of the action triggers the corresponding Physical Adapter method ```onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalAssetActionWldtEvent)```
that in our case is emulated just with a Log on the console. Also in that case advanced Physical Adapter implementation can be introduced
for example to adapt the request from a high-level (and potentially standard) DT action description to the custom requirements of the 
specific physical twin managed by the adapter.

```java
@Override
public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalAssetActionWldtEvent) {
    try{

        if(physicalAssetActionWldtEvent != null
                && physicalAssetActionWldtEvent.getActionKey().equals(SET_TEMPERATURE_ACTION_KEY)
                && physicalAssetActionWldtEvent.getBody() instanceof Double) {
            System.out.println("Received Action Request: " + physicalAssetActionWldtEvent.getActionKey()
                    + " with Body: " + physicalAssetActionWldtEvent.getBody());
        }
        else
            System.err.println("Wrong Action Received !");

    }catch (Exception e){
        e.printStackTrace();
    }
}
```

## Handling Physical & Digital Relationships

The same management that we have illustrated for Properties, Events and Action can be applied also to Digital Twin Relationships.
Relationships represent the links that exist between the modeled physical assets and other physical entity 
of the organizations through links to their corresponding Digital Twins. 
Like properties, relationships can be observed, dynamically created, and change over time, 
but unlike properties, they are not properly part of the PA's state but of its operational context 
(e.g., a DT of a robot within a production line).

It is necessary to distinguish between two concepts: i) ```Relationship```; and ii) ```Relationship Instance```. 
The first one models the relationship from a semantic point of view, 
defining its ```name``` and target ```type```. 
The second one represents an instantiation of the concept in reality. 
For example, in the context of a Smart Home, 
the Home Digital Twin (DT) will define a Relationship called ```has_room``` 
which has possible targets represented by DTs that represent different rooms of the house. 
The actual link between the Home DT and the Bedroom DT 
will be modeled by a specific Relationship Instance of the ```has_room``` relationship.

Within the state of the DT, it is necessary to 
differentiate between the concept of a relationship and that of an instance of a relationship. 
In the first case, we refer to a semantic concept where each relationship, 
through its ```name``` and the semantic ```type``` of its target, 
determines the different type of link that the DT can establish. 
On the other hand, an ```instanc``` of a relationship represents the concrete 
link present between the DT that establishes it and the target DT. 
For instance, in the case of a Smart Home, 
the Bedroom DT may have two relationships in its model: one named ```is_room_of``` and another called ```has_device```. 
An instance of the first type of relationship could, for example, 
have the Home DT as its target, while the ```has_device``` relationship could have 
multiple instances, one for each device present in the room. 
An example of a possible instance is one targeting the Air Conditioner DT.

From an implementation perspective, in the Physical Adapter and in particular where we handle the definition of the PAD we can also 
specify the existing relationships. In our case, since the Relationship is useful also to define its future instance we 
keep a reference of the relationship as in internal variable called ```insideInRelationship```.

Then we can update the code as follows:

```java
private PhysicalAssetRelationship<String> insideInRelationship = null;

@Override
public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalAssetActionWldtEvent) {
    try{
        
        [...]
        
        //Create Test Relationship to describe that the Physical Device is inside a building
        this.insideInRelationship=new PhysicalAssetRelationship<>("insideId");
        pad.getRelationships().add(insideInRelationship);
        
        [...]
        
    } catch (Exception e){
        e.printStackTrace();
    }
}
```

Of course always in the Physical Adapter we need to publish an effective instance of the definite Relationship.
To do that, we have defined a dedicated method that we can call inside the adapter to notify the DT's Core and in 
particular the Shadowing Function on the presence of a new Relationship. 

The following method can be added for example at the beginning of the Device Emulation: 

```java
private void publishPhysicalRelationshipInstance() {
    try{

        String relationshipTarget = "building-hq";

        Map<String, Object> relationshipMetadata = new HashMap<>();
        relationshipMetadata.put("floor", "f0");
        relationshipMetadata.put("room", "r0");

        PhysicalAssetRelationshipInstance<String> relInstance = this.insideInRelationship.createRelationshipInstance(relationshipTarget, relationshipMetadata);

        PhysicalAssetRelationshipInstanceCreatedWldtEvent<String> relInstanceEvent = new PhysicalAssetRelationshipInstanceCreatedWldtEvent<>(relInstance);
        publishPhysicalAssetRelationshipCreatedWldtEvent(relInstanceEvent);

    }catch (Exception e){
        e.printStackTrace();
    }
}
```

On the other hand, as already done for all the other Properties, Actions and Events we have to handle them on the
Shadowing Function and in particular updating the ```onDigitalTwinBound(...)``` method managing Relationship declaration.
Also for the Relationships there is the method denoted as ```observePhysicalAssetRelationship(relationship)``` to observe the variation
of the target entity.

```java
@Override
protected void onDigitalTwinBound(Map<String, PhysicalAssetDescription> adaptersPhysicalAssetDescriptionMap) {

    try{

        //Iterate over all the received PAD from connected Physical Adapters
        adaptersPhysicalAssetDescriptionMap.values().forEach(pad -> {
            
            [...]

            //Iterate over Physical Relationships
            pad.getRelationships().forEach(relationship -> {
                try{
                    if(relationship != null && relationship.getName().equals(GlobalKeywords.INSIDE_IN_RELATIONSHIP)){
                        DigitalTwinStateRelationship<String> insideInDtStateRelationship = new DigitalTwinStateRelationship<>(relationship.getName(), relationship.getName());
                        this.digitalTwinState.createRelationship(insideInDtStateRelationship);
                        observePhysicalAssetRelationship(relationship);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        });

        [...]

    }catch (Exception e){
        e.printStackTrace();
    }
}
```

When an Instance for a target observed Relationship has been notified by the Physical Adapter, we will receive a call back on the 
Shadowing Function method called: ```onPhysicalAssetRelationshipEstablished(PhysicalAssetRelationshipInstanceCreatedWldtEvent<?> physicalAssetRelationshipInstanceCreatedWldtEvent)```.
The object ```PhysicalAssetRelationshipInstanceCreatedWldtEvent``` describes the events and contains an object ```PhysicalAssetRelationshipInstance``` 
with all the information about the new Relationship Instance.

The Shadowing Function analyzes the instance and create the corresponding Digital Relationship instance on the DT'State
through the class ```DigitalTwinStateRelationshipInstance``` and the method ```this.digitalTwinState.addRelationshipInstance(relName, instance);```.
The resulting implemented method is the following: 

```java
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

                //Update Digital Twin State
                //NEW from 0.3.0 -> Start State Transaction
                this.digitalTwinStateManager.startStateTransaction();
  
                this.digitalTwinStateManager.addRelationshipInstance(instance);
  
                //NEW from 0.3.0 -> Commit State Transaction
                this.digitalTwinStateManager.commitStateTransaction();
            }
        }
    }catch (Exception e){
        e.printStackTrace();
    }
}

@Override
protected void onPhysicalAssetRelationshipDeleted(PhysicalAssetRelationshipInstanceDeletedWldtEvent<?> physicalAssetRelationshipInstanceDeletedWldtEvent) {

}
```

At the end the new DT's Relationships and the associated instances can be managed 
on a Digital Adapter using the ```onDigitalTwinSync(IDigitalTwinState currentDigitalTwinState)``` method and 
the following DT state callback method: `onStateUpdate()`.

For example a simple implementation logging on the console can be:

```java
@Override
protected void onStateUpdate(DigitalTwinState newDigitalTwinState, DigitalTwinState previousDigitalTwinState, ArrayList<DigitalTwinStateChange> digitalTwinStateChangeList) {

  // In newDigitalTwinState we have the new DT State
  System.out.println("New DT State is: " + newDigitalTwinState);

  // The previous DT State is available through the variable previousDigitalTwinState
  System.out.println("Previous DT State is: " + previousDigitalTwinState);

  // We can also check each DT's state change potentially differentiating the behaviour for each change
  if (digitalTwinStateChangeList != null && !digitalTwinStateChangeList.isEmpty()) {

    // Iterate through each state change in the list
    [...]

      // Specific log example for Relationships Instance Variation
      if(resourceType.equals(DigitalTwinStateChange.ResourceType.RELATIONSHIP_INSTANCE))
        System.out.println("New Relationship Instance operation:" + operation + " Resource:" + resource);
    }
  } else {
    // No state changes
    System.out.println("No state changes detected.");
  }
}
```

## Configurable Physical and Digital Adapters

The WLDT library provides a native method to define Configurable Physical ad Digital Adapters specifying a 
custom configuration class passed as parameter in the constructor. 

Starting with the Physical Adapter created in the previous example ```DemoPhysicalAdapter ``` instead of extending the
base class ```PhysicalAdapter``` we can extend now ```ConfigurablePhysicalAdapter<C>``` where ```C``` is the name of the 
that we would like to use as configuration. 

In our example we can create a simple configuration class called ```DemoPhysicalAdapterConfiguration``` where we move 
the constant variable used to implement the behaviour of our demo physical adapter. The resulting class will be the
following: 

```java
public class DemoPhysicalAdapterConfiguration {

    private int messageUpdateTime = GlobalKeywords.MESSAGE_UPDATE_TIME;

    private int messageUpdateNumber = GlobalKeywords.MESSAGE_UPDATE_NUMBER;

    private double temperatureMinValue = GlobalKeywords.TEMPERATURE_MIN_VALUE;

    private double temperatureMaxValue = GlobalKeywords.TEMPERATURE_MAX_VALUE;

    public DemoPhysicalAdapterConfiguration() {
    }

    public DemoPhysicalAdapterConfiguration(int messageUpdateTime, int messageUpdateNumber, double temperatureMinValue, double temperatureMaxValue) {
        this.messageUpdateTime = messageUpdateTime;
        this.messageUpdateNumber = messageUpdateNumber;
        this.temperatureMinValue = temperatureMinValue;
        this.temperatureMaxValue = temperatureMaxValue;
    }

    public int getMessageUpdateTime() {
        return messageUpdateTime;
    }

    public void setMessageUpdateTime(int messageUpdateTime) {
        this.messageUpdateTime = messageUpdateTime;
    }

    public int getMessageUpdateNumber() {
        return messageUpdateNumber;
    }

    public void setMessageUpdateNumber(int messageUpdateNumber) {
        this.messageUpdateNumber = messageUpdateNumber;
    }

    public double getTemperatureMinValue() {
        return temperatureMinValue;
    }

    public void setTemperatureMinValue(double temperatureMinValue) {
        this.temperatureMinValue = temperatureMinValue;
    }

    public double getTemperatureMaxValue() {
        return temperatureMaxValue;
    }

    public void setTemperatureMaxValue(double temperatureMaxValue) {
        this.temperatureMaxValue = temperatureMaxValue;
    }
}
```

Now we can create or update our Physical Adapter extending ```ConfigurablePhysicalAdapter<DemoPhysicalAdapterConfiguration>```
as illustrated in the following snippet:

```java
public class DemoPhysicalAdapter extends ConfigurablePhysicalAdapter<DemoPhysicalAdapterConfiguration> { 
  [...]
}
```

Extending this class also the constructor should be updated getting as a parameter the expected configuration instance.
Our constructor will be the following: 

```java
public DemoConfPhysicalAdapter(String id, DemoPhysicalAdapterConfiguration configuration) {
    super(id, configuration);
}
```

After that change since we removed and moved the used constant values into the new Configuration class we have also to
update the ```deviceEmulation()``` method having access to the configuration through the method ```getConfiguration()``` or ```this.getConfiguration()```
directly on the adapter.

```java
private Runnable deviceEmulation(){
    return () -> {
        try {


            System.out.println("[DemoPhysicalAdapter] -> Sleeping before Starting Physical Device Emulation ...");

            //Sleep 5 seconds to emulate device startup
            Thread.sleep(10000);

            System.out.println("[DemoPhysicalAdapter] -> Starting Physical Device Emulation ...");

            //Create a new random object to emulate temperature variations
            Random r = new Random();

            //Publish an initial Event for a normal condition
            publishPhysicalAssetEventWldtEvent(new PhysicalAssetEventWldtEvent<>(GlobalKeywords.OVERHEATING_EVENT_KEY, "normal"));

            //Emulate the generation on 'n' temperature measurements
            for(int i = 0; i < getConfiguration().getMessageUpdateNumber(); i++){

                //Sleep to emulate sensor measurement
                Thread.sleep(getConfiguration().getMessageUpdateTime());

                //Update the
                double randomTemperature = getConfiguration().getTemperatureMinValue() + (getConfiguration().getTemperatureMaxValue() - getConfiguration().getTemperatureMinValue()) * r.nextDouble();

                //Create a new event to notify the variation of a Physical Property
                PhysicalAssetPropertyWldtEvent<Double> newPhysicalPropertyEvent = new PhysicalAssetPropertyWldtEvent<>(GlobalKeywords.TEMPERATURE_PROPERTY_KEY, randomTemperature);

                //Publish the WLDTEvent associated to the Physical Property Variation
                publishPhysicalAssetPropertyWldtEvent(newPhysicalPropertyEvent);
            }

            //Publish a demo Physical Event associated to a 'critical' overheating condition
            publishPhysicalAssetEventWldtEvent(new PhysicalAssetEventWldtEvent<>(GlobalKeywords.OVERHEATING_EVENT_KEY, "critical"));

        } catch (EventBusException | InterruptedException e) {
            e.printStackTrace();
        }
    };
}
```

A similar approach can be adopted also for the Digital Adapter with the small difference that the base class
```DigitalAdapter``` already allow the possibility to specify a configuration. For this reason in the previous example 
we extended ```DigitalAdapter<Void>``` avoiding to specifying a configuration.

In this updated version we can create a new ```DemoDigitalAdapterConfiguration``` class containing the parameter association
to the emulation of the action and then update our adapter to support the new configuration. Our new configuration class will be: 

```java
public class DemoDigitalAdapterConfiguration {

  private int sleepTimeMs = GlobalKeywords.ACTION_SLEEP_TIME_MS;

  private int emulatedActionCount = GlobalKeywords.EMULATED_ACTION_COUNT;

  private double temperatureMinValue = GlobalKeywords.TEMPERATURE_MIN_VALUE;

  private double temperatureMaxValue = GlobalKeywords.TEMPERATURE_MAX_VALUE;

  public DemoDigitalAdapterConfiguration() {
  }

  public DemoDigitalAdapterConfiguration(int sleepTimeMs, int emulatedActionCount, double temperatureMinValue, double temperatureMaxValue) {
    this.sleepTimeMs = sleepTimeMs;
    this.emulatedActionCount = emulatedActionCount;
    this.temperatureMinValue = temperatureMinValue;
    this.temperatureMaxValue = temperatureMaxValue;
  }

  public int getSleepTimeMs() {
    return sleepTimeMs;
  }

  public void setSleepTimeMs(int sleepTimeMs) {
    this.sleepTimeMs = sleepTimeMs;
  }

  public int getEmulatedActionCount() {
    return emulatedActionCount;
  }

  public void setEmulatedActionCount(int emulatedActionCount) {
    this.emulatedActionCount = emulatedActionCount;
  }

  public double getTemperatureMinValue() {
    return temperatureMinValue;
  }

  public void setTemperatureMinValue(double temperatureMinValue) {
    this.temperatureMinValue = temperatureMinValue;
  }

  public double getTemperatureMaxValue() {
    return temperatureMaxValue;
  }

  public void setTemperatureMaxValue(double temperatureMaxValue) {
    this.temperatureMaxValue = temperatureMaxValue;
  }
}
```

After that we can update the declaration of our Digital Adapter and modify its constructor to accept the configuration. 
The resulting class will be: 

```java
public class DemoDigitalAdapter extends DigitalAdapter<DemoDigitalAdapterConfiguration> { 
  
  public DemoDigitalAdapter(String id, DemoDigitalAdapterConfiguration configuration) {
    super(id, configuration);
  }
  
  [...]
}
```

Of course the possibility to have this configuration will allow us to improve the ```emulateIncomingDigitalAction``` method 
in the following way having access to the configuration through the method ```getConfiguration()``` or ```this.getConfiguration()```
directly on the adapter: 

```java
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
```

When we have updated both adapters making them configurable we can update our ```main``` function in the process
that we have previouly device using the updated adapters and passing the configurations:

````java
public class DemoDigitalTwin {

    public static void main(String[] args)  {
        try{

            WldtEngine digitalTwinEngine = new WldtEngine(new DemoShadowingFunction("test-shadowing-function"), "test-digital-twin");

            //Default Physical and Digital Adapter
            //digitalTwinEngine.addPhysicalAdapter(new DemoPhysicalAdapter("test-physical-adapter"));
            //digitalTwinEngine.addDigitalAdapter(new DemoDigitalAdapter("test-digital-adapter"));

            //Physical and Digital Adapters with Configuration
            digitalTwinEngine.addPhysicalAdapter(new DemoConfPhysicalAdapter("test-physical-adapter", new DemoPhysicalAdapterConfiguration()));
            digitalTwinEngine.addDigitalAdapter(new DemoConfDigitalAdapter("test-digital-adapter", new DemoDigitalAdapterConfiguration()));

            digitalTwinEngine.startLifeCycle();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
````