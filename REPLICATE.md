# Demo Instructions

This document provides commands for running the demo.

The software is a maven based project. Please install Java and Maven packages. The installation instructions for Ubuntu OS are:

```bash
apt-get install openjdk-17-jdk
apt-get install maven
```

Package and execute the software

```
mvn package
java -jar target/WLDT-Demo-DigitalTwin-1.0-SNAPSHOT-jar-with-dependencies.jar
```

The following execution log will be produced.

```log
12:44:35.940 [main] DEBUG it.wldt.core.engine.DigitalTwin - [WLDT-DigitalTwin] New PhysicalAdapter (io.github.wldt.demo.physical.DemoConfPhysicalAdapter) Added to the Worker List ! Physical Adapters - Worker List Size: 1
12:44:35.942 [main] DEBUG it.wldt.core.engine.DigitalTwin - [WLDT-DigitalTwin] New DigitalAdapter (io.github.wldt.demo.digital.DemoConfDigitalAdapter) Added to the Worker List ! Digital Adapters - Worker List Size: 1
12:44:35.942 [main] DEBUG i.wldt.core.engine.DigitalTwinEngine - Adding Digital Twin: test-dt-id to the Engine ...
12:44:35.942 [main] DEBUG i.wldt.core.engine.DigitalTwinEngine - Digital Twin: test-dt-id added to the Engine !
12:44:35.942 [main] DEBUG i.wldt.core.engine.DigitalTwinEngine - Starting Digital Twin: test-dt-id ...
12:44:35.944 [main] DEBUG it.wldt.core.model.ModelEngine - ModelEngine-Listener-DT-LifeCycle: onCreate()
[DemoDigitalAdapter] -> onDigitalTwinCreate()
12:44:35.945 [main] INFO  it.wldt.core.engine.DigitalTwin - Executing PhysicalAdapter: class io.github.wldt.demo.physical.DemoConfPhysicalAdapter
12:44:35.945 [main] INFO  it.wldt.core.engine.DigitalTwin - Executing DigitalAdapter: class io.github.wldt.demo.digital.DemoConfDigitalAdapter
12:44:35.946 [main] DEBUG it.wldt.core.model.ModelEngine - ModelEngine-Listener-DT-LifeCycle: onCreate()
[DemoDigitalAdapter] -> onDigitalTwinStart()
[TestDigitalAdapter] -> onAdapterStart()
[DemoPhysicalAdapter] -> Sleeping before Publishing Physical Asset Description ...
[DemoPhysicalAdapter] -> Sleeping before Starting Physical Device Emulation ...
12:44:35.946 [main] DEBUG i.wldt.core.engine.DigitalTwinEngine - Digital Twin: test-dt-id STARTED !
[DemoPhysicalAdapter] -> Publishing Physical Asset Description ...
12:44:40.952 [Thread-1] DEBUG i.w.a.p.ConfigurablePhysicalAdapter - test-physical-adapter -> Subscribed to: dt.physical.event.action.set-temperature-action-key
12:44:40.952 [Thread-1] INFO  it.wldt.core.engine.DigitalTwin - PhysicalAdapter test-physical-adapter BOUND ! PhysicalAssetDescription: PhysicalAssetDescription{actions=[PhysicalAssetAction{key='set-temperature-action-key', type='temperature.actuation', contentType='text/plain'}], properties=[PhysicalAssetProperty{key='temperature-property-key', value=0.0, readable=false, writable=true}], events=[PhysicalAssetEvent{key='overheating-event-key', type='text/plain'}], relationships=[PhysicalAssetRelationship{name='insideIn'}]}
12:44:40.952 [Thread-1] DEBUG it.wldt.core.model.ModelEngine - ModelEngine-Listener-DT-LifeCycle: onPhysicalAdapterBound(test-physical-adapter)
12:44:40.953 [Thread-1] INFO  it.wldt.core.engine.DigitalTwin - Digital Twin BOUND !
12:44:40.953 [Thread-1] DEBUG it.wldt.core.model.ModelEngine - ModelEngine-Listener-DT-LifeCycle: onDigitalTwinBound()
[TestShadowingFunction] -> onDigitalTwinBound(): {test-physical-adapter=PhysicalAssetDescription{actions=[PhysicalAssetAction{key='set-temperature-action-key', type='temperature.actuation', contentType='text/plain'}], properties=[PhysicalAssetProperty{key='temperature-property-key', value=0.0, readable=false, writable=true}], events=[PhysicalAssetEvent{key='overheating-event-key', type='text/plain'}], relationships=[PhysicalAssetRelationship{name='insideIn'}]}
.....
.....
```

