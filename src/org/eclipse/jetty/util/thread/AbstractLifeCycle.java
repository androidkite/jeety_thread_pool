package org.eclipse.jetty.util.thread;
//
//  ========================================================================
//  Copyright (c) 1995-2017 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//


import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Basic implementation of the life cycle interface for components.
 */
public abstract class AbstractLifeCycle implements LifeCycle
{
    
    public static final String STOPPED="STOPPED";
    public static final String FAILED="FAILED";
    public static final String STARTING="STARTING";
    public static final String STARTED="STARTED";
    public static final String STOPPING="STOPPING";
    public static final String RUNNING="RUNNING";

    private final CopyOnWriteArrayList<LifeCycle.Listener> _listeners=new CopyOnWriteArrayList<LifeCycle.Listener>();
    private final Object _lock = new Object();
    private final int __FAILED = -1, __STOPPED = 0, __STARTING = 1, __STARTED = 2, __STOPPING = 3;
    private volatile int _state = __STOPPED;
    private long _stopTimeout = 30000;

    protected void doStart() throws Exception
    {
    }

    protected void doStop() throws Exception
    {
    }
    
    @Override
    public final void start() throws Exception
    {
        synchronized (_lock)
        {
            try
            {
                if (_state == __STARTED || _state == __STARTING)
                    return;
                setStarting();
                doStart();
                setStarted();
            }
            catch (Throwable e)
            {
                setFailed(e);
                throw e;
            }
        }
    }

    @Override
    public final void stop() throws Exception
    {
        synchronized (_lock)
        {
            try
            {
                if (_state == __STOPPING || _state == __STOPPED)
                    return;
                setStopping();
                doStop();
                setStopped();
            }
            catch (Throwable e)
            {
                setFailed(e);
                throw e;
            }
        }
    }

    @Override
    public boolean isRunning()
    {
        final int state = _state;

        return state == __STARTED || state == __STARTING;
    }

    @Override
    public boolean isStarted()
    {
        return _state == __STARTED;
    }

    @Override
    public boolean isStarting()
    {
        return _state == __STARTING;
    }

    @Override
    public boolean isStopping()
    {
        return _state == __STOPPING;
    }

    @Override
    public boolean isStopped()
    {
        return _state == __STOPPED;
    }

    @Override
    public boolean isFailed()
    {
        return _state == __FAILED;
    }

    @Override
    public void addLifeCycleListener(LifeCycle.Listener listener)
    {
        _listeners.add(listener);
    }

    @Override
    public void removeLifeCycleListener(LifeCycle.Listener listener)
    {
        _listeners.remove(listener);
    }

    public String getState()
    {
        switch(_state)
        {
            case __FAILED: return FAILED;
            case __STARTING: return STARTING;
            case __STARTED: return STARTED;
            case __STOPPING: return STOPPING;
            case __STOPPED: return STOPPED;
        }
        return null;
    }

    public static String getState(LifeCycle lc)
    {
        if (lc.isStarting()) return STARTING;
        if (lc.isStarted()) return STARTED;
        if (lc.isStopping()) return STOPPING;
        if (lc.isStopped()) return STOPPED;
        return FAILED;
    }

    private void setStarted()
    {
        _state = __STARTED;
        for (Listener listener : _listeners)
            listener.lifeCycleStarted(this);
    }

    private void setStarting()
    {
        _state = __STARTING;
        for (Listener listener : _listeners)
            listener.lifeCycleStarting(this);
    }

    private void setStopping()
    {
        _state = __STOPPING;
        for (Listener listener : _listeners)
            listener.lifeCycleStopping(this);
    }

    private void setStopped()
    {
        _state = __STOPPED;
        for (Listener listener : _listeners)
            listener.lifeCycleStopped(this);
    }

    private void setFailed(Throwable th)
    {
        _state = __FAILED;
        for (Listener listener : _listeners)
            listener.lifeCycleFailure(this,th);
    }

    public long getStopTimeout()
    {
        return _stopTimeout;
    }

    public void setStopTimeout(long stopTimeout)
    {
        this._stopTimeout = stopTimeout;
    }

    public static abstract class AbstractLifeCycleListener implements LifeCycle.Listener
    {
        @Override public void lifeCycleFailure(LifeCycle event, Throwable cause) {}
        @Override public void lifeCycleStarted(LifeCycle event) {}
        @Override public void lifeCycleStarting(LifeCycle event) {}
        @Override public void lifeCycleStopped(LifeCycle event) {}
        @Override public void lifeCycleStopping(LifeCycle event) {}
    }
}
