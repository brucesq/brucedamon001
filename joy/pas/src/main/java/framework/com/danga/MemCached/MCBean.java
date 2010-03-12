/**
 * MemCached Java client, mbean interface
 * Copyright (c) 2007 Greg Whalin
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the BSD license
 *
 * This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 *
 * You should have received a copy of the BSD License along with this
 * library.
 *
 * @author greg whalin <greg@meetup.com> 
 * @version 2.0
 */
package com.danga.MemCached;

public abstract interface MCBean {

	// get server info
	public String[] getServers();
	public Integer[] getWeights();

	// pool settings
	public void setMinConn( int minConn );
	public int getMinConn();

	public void setMaxConn( int maxConn );
	public int getMaxConn();

	public void setMaxIdle( long maxIdle );
	public long getMaxIdle();

	public void setMaxBusyTime( long maxBusyTime );
	public long getMaxBusy();

	public void setMaintSleep( long maintSleep );
	public long getMaintSleep();

	public void setFailover( boolean failover );
	public boolean getFailover();

	public void setFailback( boolean failback );
	public boolean getFailback();

	public void setAliveCheck( boolean aliveCheck );
	public boolean getAliveCheck();

	// hashing algorithm
	public void setHashingAlg( int alg );
	public int getHashingAlg();
}
