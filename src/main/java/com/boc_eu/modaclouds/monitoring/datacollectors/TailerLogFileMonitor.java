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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.io.input.Tailer;

/**
 */
public class TailerLogFileMonitor extends AbstractMonitor
{
  
  /**
   * Agent fof communicating with the rest of the platform.
   */
  private DCAgent aDcAgent;
  
  /**
   * Log file name.
   */
  private String sFileName;
  
  /**
   * Tailer log file monitor thread.
   */
  private Thread aThread;
  
  /**
   * Pattern to match.
   */
  private Pattern aRequestPattern;
  
  /**
   * Monitoring period.
   */
  private int nPeriod;
  
  /**
   * The unique monitored target.
   */
  private final String sMonitoredTarget;
  
  /**
   * The sampling probability.
   */
  private double dSamplingProb;
  
  private static final int SLEEP = 500;
  
  /**
   * Constructor of the class.
   *
   * @param ownURI
   * @param mode
   */
  public TailerLogFileMonitor (final String ownURI, final String mode)
  {
    super (ownURI, mode);
    this.sMonitoredTarget = ownURI;
    this.monitorName = "tailerLogFile"; //$NON-NLS-1$
  }
  
  @Override
  public void run ()
  {
    long nStartTime = 0;
    Tailer aTailer = null;
    while (!this.aThread.isInterrupted ())
    {
      if (this.mode.equals ("tower4clouds")) //$NON-NLS-1$
      {
        if (null == aTailer)
        {
          for (final String metric : getProvidedMetrics ())
          {
            try
            {
                final InternalComponent resource = new InternalComponent (
                        Config.getInstance ().getInternalComponentType (),
                        Config.getInstance ().getInternalComponentId ());
                if (this.aDcAgent.shouldMonitor (resource, metric))
              {
                
                final Map <String, String> parameters = this.aDcAgent.getParameters (resource, metric);
                this.sFileName = parameters.get ("fileName"); //$NON-NLS-1$
                this.aRequestPattern = Pattern.compile (parameters.get ("pattern")); //$NON-NLS-1$
                this.nPeriod = Integer.valueOf (parameters.get ("samplingTime")) * 1000; //$NON-NLS-1$
                this.dSamplingProb = Double.valueOf (parameters.get ("samplingProbability"));
                final TailerLogFileMonitorListener aListener = new TailerLogFileMonitorListener (this.aDcAgent,
                                                                                                 this.dSamplingProb,
                                                                                                 this.aRequestPattern,
                                                                                                 this.sMonitoredTarget,
                                                                                                 metric);
                final File fl = new File (this.sFileName);
                aTailer = Tailer.create (fl, aListener);
                System.out.println ("Started Tailer for metric " + //$NON-NLS-1$
                                    metric +
                                    " with pattern " + //$NON-NLS-1$
                                    this.aRequestPattern.toString ());
                break;
              }
            }
            catch (NumberFormatException | ConfigurationException e)
            {
              e.printStackTrace ();
            }
          }
        }
        try
        {
          Thread.sleep (TailerLogFileMonitor.SLEEP);
        }
        catch (final InterruptedException ex)
        {
          // we were asked to terminate ...
          if (null != aTailer)
          {
            aTailer.stop ();
          }
        }
      }
    }
    try
    {
      nStartTime = System.currentTimeMillis ();
      Thread.sleep (SLEEP);
    }
    catch (final InterruptedException ex)
    {
      if (null != aTailer)
      {
        aTailer.stop ();
      }
      Logger.getLogger (TailerLogFileMonitor.class.getName ()).log (Level.SEVERE, null, ex);
    }
    
  }
  
  private static Set <String> getProvidedMetrics ()
  {
    final Set <String> metrics = new HashSet <String> ();
    metrics.add ("BpmsMetric"); //$NON-NLS-1$
    return metrics;
  }
  
  @Override
  public void start ()
  {
    this.aThread = new Thread (this, "tailerLogFile"); //$NON-NLS-1$
  }
  
  @Override
  public void init ()
  {
    this.aThread.start ();
    System.out.println ("Tailer log file monitor running!"); //$NON-NLS-1$
  }
  
  @Override
  public void stop ()
  {
    while (!this.aThread.isInterrupted ())
    {
      this.aThread.interrupt ();
    }
    System.out.println ("Tailer log file monitor stopped!"); //$NON-NLS-1$
  }
  
  @Override
  public void setDCAgent (final DCAgent aDcAgent)
  {
    this.aDcAgent = aDcAgent;
    
  }
  
}
