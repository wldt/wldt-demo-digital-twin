package io.github.wldt.demo;

import io.github.wldt.demo.digital.DemoConfDigitalAdapter;
import io.github.wldt.demo.digital.DemoDigitalAdapter;
import io.github.wldt.demo.digital.DemoDigitalAdapterConfiguration;
import io.github.wldt.demo.physical.DemoConfPhysicalAdapter;
import io.github.wldt.demo.physical.DemoPhysicalAdapter;
import io.github.wldt.demo.physical.DemoPhysicalAdapterConfiguration;
import it.wldt.core.engine.WldtEngine;

/**
 * Authors:
 *          Marco Picone, Ph.D. (picone.m@gmail.com)
 * Date: 01/09/2023
 * Project: White Label Digital Twin Java Framework - (whitelabel-digitaltwin)
 */
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
