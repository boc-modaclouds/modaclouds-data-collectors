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
import org.slf4j.LoggerFactory;

/**
 * @author sluca
 */
public class TailerLogFileMonitorListener extends TailerListenerAdapter
{
  
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
  
  public TailerLogFileMonitorListener (final double dSamplingProb,
                                       final Pattern aRequestPattern,
                                       final String sMonitoringTarget,
                                       final String sCollectedMetric)
  {
    super ();
    this.dSamplingProb = dSamplingProb;
    this.aRequestPattern = aRequestPattern;
    this.sMonitoredTarget = sMonitoringTarget;
    this.sCollectedMetric = sCollectedMetric;
    // this.aDcAgent = DataCollectorAgent.getInstance ();
  }
  
  @Override
  public void handle (final String sLine)
  {
    try
    {
      LoggerFactory.getLogger (TailerLogFileMonitorListener.class).info ("got line: {}", sLine); //$NON-NLS-1$
      final Matcher aRequestMatcher = this.aRequestPattern.matcher (sLine);
      if (aRequestMatcher.find ())
      {
        LoggerFactory.getLogger (TailerLogFileMonitorListener.class)
                     .info ("got a match for metric {}", this.sCollectedMetric); //$NON-NLS-1$
        final StringBuilder aSB = new StringBuilder ();
        for (int i = 1; i <= aRequestMatcher.groupCount (); i++)
        {
          aSB.append (aRequestMatcher.group (i));
          if (i != aRequestMatcher.groupCount ())
          {
            aSB.append (", "); //$NON-NLS-1$
          }
        }
        LoggerFactory.getLogger (TailerLogFileMonitorListener.class).info ("extracted value {}", //$NON-NLS-1$
                                                                           aSB.toString ());
        this.aDcAgent.send (new InternalComponent (Config.getInstance ()
                                                         .getInternalComponentType (),
                                                   Config.getInstance ().getInternalComponentId ()),
                            "BpmsMetric", //$NON-NLS-1$
                            aSB);
        LoggerFactory.getLogger (TailerLogFileMonitorListener.class)
                     .info ("sent value {} for metric BpmsMetric", aSB.toString ()); //$NON-NLS-1$
      }
    }
    catch (final Exception ex)
    {
      LoggerFactory.getLogger (TailerLogFileMonitorListener.class).info ("caught exception: ", ex); //$NON-NLS-1$
    }
  }
  
  public void setDCAgent (final DCAgent aDcAgent)
  {
    this.aDcAgent = aDcAgent;
    
  }
}
