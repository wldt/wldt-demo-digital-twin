package io.github.wldt.demo;

import io.github.wldt.demo.digital.DemoConfDigitalAdapter;
import io.github.wldt.demo.digital.DemoDigitalAdapterConfiguration;
import io.github.wldt.demo.logger.DemoEventLogger;
import io.github.wldt.demo.physical.DemoConfPhysicalAdapter;
import io.github.wldt.demo.physical.DemoPhysicalAdapterConfiguration;
import it.wldt.core.engine.DigitalTwin;
import it.wldt.core.engine.DigitalTwinEngine;
import it.wldt.core.event.WldtEventBus;

/**
 * Main class to build and test a demo Digital Twin with the created physical and digital adapters
 *
 * @author Marco Picone, Ph.D. (picone.m@gmail.com)
 */
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
