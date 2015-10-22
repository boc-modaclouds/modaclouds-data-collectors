/**
 * Copyright (C)2014 BOC Information Systems GmbH
 */
package com.boc_eu.modaclouds.monitoring.datacollectors;

import imperial.modaclouds.monitoring.datacollectors.basic.Config;
import it.polimi.tower4clouds.data_collector_library.DCAgent;
import it.polimi.tower4clouds.model.ontology.InternalComponent;
import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sluca
 */
public class TailerLogFileMonitorListener extends TailerListenerAdapter {

    /**
     * The logger for our class.
     */
    private static final Logger log = LoggerFactory.getLogger(TailerLogFileMonitor.class);

    /**
     * Agent for communicating with the rest of the platform.
     */
    private DCAgent aDcAgent;

    /**
     * The collected metrics.
     */
    private Map<String, Pattern> aMetrics;

    public TailerLogFileMonitorListener(final DCAgent aDcAgent,
            final Map<String, Pattern> aMetrics) {
        super();
        this.aDcAgent = aDcAgent;
        this.aMetrics = aMetrics;
    }

    @Override
    public void handle(final String sLine) {
        try {
            log.debug("got line: {}", sLine); //$NON-NLS-1$
            Map<String, Pattern> aCurrentMetrics = new HashMap<String, Pattern>();
            synchronized (this) {
                // create a deep copy in a threadsafe way
                aCurrentMetrics.putAll(aMetrics);
            }
            for (Map.Entry<String, Pattern> aMetric : aMetrics.entrySet()) {
                final String sMetric = aMetric.getKey();
                final Matcher aRequestMatcher = aMetric.getValue().matcher(sLine);
                if (aRequestMatcher.find()) {
                    log.debug("got a match for metric {}", sMetric); //$NON-NLS-1$
                    final Float value = Float.valueOf(aRequestMatcher.group(1));
                    log.debug("extracted value {}", value); //$NON-NLS-1$
                    this.aDcAgent.send(new InternalComponent(Config.getInstance()
                            .getInternalComponentType(),
                            Config.getInstance()
                            .getInternalComponentId()),
                            sMetric,
                            value);
                    log.info("sent value {} for metric {}", value, sMetric); //$NON-NLS-1$
                }
            }
        } catch (final Exception ex) {
            log.error("caught exception: ", ex); //$NON-NLS-1$
        }
    }

    public void setMetrics(final Map<String, Pattern> aMetrics) {
        synchronized (this) {
            this.aMetrics = aMetrics;
        }
    }

}
