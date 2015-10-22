/**
 * Copyright (C)2014 BOC Information Systems GmbH
 */
package com.boc_eu.modaclouds.monitoring.datacollectors;

import imperial.modaclouds.monitoring.datacollectors.basic.Config;
import it.polimi.tower4clouds.data_collector_library.DCAgent;
import it.polimi.tower4clouds.model.ontology.InternalComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sluca
 */
public class TailerLogFileMonitorListener extends TailerListenerAdapter
{
  /**
   * The logger for our class.
   */
  private static final Logger log = LoggerFactory.getLogger(TailerLogFileMonitor.class);
    
  /**
   * Agent fof communicating with the rest of the platform.
   */
  private DCAgent aDcAgent;
  /**
   * The sampling probability.
   */
  private final double dSamplingProb;
  /**
   * Extract the request information according the regular expression.
   */
  private final Pattern aRequestPattern;
  /**
   * The unique monitored target.
   */
  private final String sMonitoredTarget;
  /**
   * The collected metric.
   */
  private final String sCollectedMetric;
  
  public TailerLogFileMonitorListener (final DCAgent aDcAgent,
                                       final double dSamplingProb,
                                       final Pattern aRequestPattern,
                                       final String sMonitoringTarget,
                                       final String sCollectedMetric)
  {
    super ();
    this.aDcAgent = aDcAgent;
    this.dSamplingProb = dSamplingProb;
    this.aRequestPattern = aRequestPattern;
    this.sMonitoredTarget = sMonitoringTarget;
    this.sCollectedMetric = sCollectedMetric;
  }
  
  @Override
  public void handle (final String sLine)
  {
    try
    {
      log.info ("got line: {}", sLine); //$NON-NLS-1$
      final Matcher aRequestMatcher = this.aRequestPattern.matcher (sLine);
      if (aRequestMatcher.find ())
      {
        log.info ("got a match for metric {}", this.sCollectedMetric); //$NON-NLS-1$
        final Float value = Float.valueOf(aRequestMatcher.group(1));
        log.info ("extracted value {}", value); //$NON-NLS-1$
        this.aDcAgent.send (new InternalComponent (Config.getInstance ()
                                                         .getInternalComponentType (),
                                                   Config.getInstance ().getInternalComponentId ()),
                            this.sCollectedMetric,
                            value);                            
        log.info ("sent value {} for metric BpmsMetric", value); //$NON-NLS-1$
      }
    }
    catch (final Exception ex)
    {
      log.info ("caught exception: ", ex); //$NON-NLS-1$
    }
  }
  
  public void setDCAgent (final DCAgent aDcAgent)
  {
    this.aDcAgent = aDcAgent;
    
  }
}
