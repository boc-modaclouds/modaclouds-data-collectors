/**
 * Copyright (C)2014 BOC Information Systems GmbH
 */
package com.boc_eu.modaclouds.monitoring.datacollectors;

/**
 */
import imperial.modaclouds.monitoring.datacollectors.basic.AbstractMonitor;
import imperial.modaclouds.monitoring.datacollectors.basic.Config;
import imperial.modaclouds.monitoring.datacollectors.basic.ConfigurationException;
import it.polimi.tower4clouds.data_collector_library.DCAgent;
import it.polimi.tower4clouds.model.ontology.InternalComponent;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.io.input.Tailer;
import org.slf4j.LoggerFactory;

/**
 */
public class TailerLogFileMonitor extends AbstractMonitor {

    /**
     * Our logger.
     */
    private static final org.slf4j.Logger aLog = LoggerFactory
            .getLogger(TailerLogFileMonitor.class);

    /**
     * Agent for communicating with the rest of the platform.
     */
    private DCAgent aDcAgent;

    /**
     * Tailer log file monitor thread.
     */
    private Thread aThread;

    /**
     * Monitoring period.
     */
    private int nPeriod;

    /**
     * The log file to be tailed.
     */
    private String sLogFile;

    private static final int SLEEP = 500;

    /**
     * Constructor of the class.
     *
     * @param ownURI
     * @param mode
     */
    public TailerLogFileMonitor(final String ownURI, final String mode) {
        super(ownURI, mode);
        this.monitorName = "tailerLogFile"; //$NON-NLS-1$
    }

    @Override
    public void run() {
        Tailer aTailer = null;
        TailerLogFileMonitorListener aListener = null;
        while (!this.aThread.isInterrupted()) {
            if (this.mode.equals("tower4clouds")) //$NON-NLS-1$
            {
                HashMap<String, Pattern> aMetrics = new HashMap<>();
                for (final String metric : getProvidedMetrics()) {
                    try {
                        final InternalComponent resource = new InternalComponent(
                                Config.getInstance().getInternalComponentType(),
                                Config.getInstance().getInternalComponentId());
                        if (this.aDcAgent.shouldMonitor(resource, metric)) {

                            final Map<String, String> parameters = this.aDcAgent.getParameters(resource, metric);
                            final String sFileName = parameters.get("fileName"); //$NON-NLS-1$
                            if (null == sLogFile) {
                                sLogFile = sFileName;
                            }
                            if (!sFileName.equals(sLogFile)) {
                                aLog.error("TailerLogFileMonitor does not support multiple log files");
                                return;
                            }
                            final Pattern aRequestPattern = Pattern.compile(parameters.get("pattern")); //$NON-NLS-1$
                            aMetrics.put(metric, aRequestPattern);
                            aLog.info("Registering listener for metric {} with pattern {}", //$NON-NLS-1$
                                    metric, aRequestPattern);
                            break;
                        }
                    } catch (ConfigurationException ex) {
                        aLog.info("Exception while creating TailerLogFileMonitor", ex);
                    }
                }
                if (null == aListener) {
                    aListener = new TailerLogFileMonitorListener(this.aDcAgent,
                            aMetrics);
                } else {
                    aListener.setMetrics(aMetrics);
                }
                if (null == aTailer) {
                    final File fl = new File(sLogFile);
                    aTailer = Tailer.create(fl, aListener);
                }
            }
            try {
                Thread.sleep(TailerLogFileMonitor.SLEEP);
            } catch (final InterruptedException ex) {
                // we were asked to terminate ...
                if (null != aTailer) {
                    aTailer.stop();
                }
            }
        }
        try {
            Thread.sleep(SLEEP);
        } catch (final InterruptedException ex) {
            if (null != aTailer) {
                aTailer.stop();
            }
            Logger.getLogger(TailerLogFileMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static Set<String> getProvidedMetrics() {
        final Set<String> metrics = new HashSet<>();
        metrics.add("BpmsMetric"); //$NON-NLS-1$
        return metrics;
    }

    @Override
    public void start() {
        this.aThread = new Thread(this, "tailerLogFile"); //$NON-NLS-1$
    }

    @Override
    public void init() {
        this.aThread.start();
        System.out.println("Tailer log file monitor running!"); //$NON-NLS-1$
    }

    @Override
    public void stop() {
        while (!this.aThread.isInterrupted()) {
            this.aThread.interrupt();
        }
        System.out.println("Tailer log file monitor stopped!"); //$NON-NLS-1$
    }

    @Override
    public void setDCAgent(final DCAgent aDcAgent) {
        this.aDcAgent = aDcAgent;

    }

}
