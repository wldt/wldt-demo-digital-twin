package io.github.wldt.demo.logger;

import it.wldt.core.event.IWldtEventLogger;
import it.wldt.core.event.WldtEvent;

/**
 * An example on how to implement and use a WLDT Event Logger to keep track of events passing through the DT.
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 */
public class DemoEventLogger implements IWldtEventLogger {

    @Override
    public void logEventPublished(String publisherId, WldtEvent<?> wldtEvent) {

    }

    @Override
    public void logEventForwarded(String publisherId, String subscriberId, WldtEvent<?> wldtEvent) {

    }

    @Override
    public void logClientSubscription(String eventType, String subscriberId) {

    }

    @Override
    public void logClientUnSubscription(String eventType, String subscriberId) {

    }
}
