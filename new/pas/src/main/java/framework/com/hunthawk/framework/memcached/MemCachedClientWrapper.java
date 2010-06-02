/**
 * 
 */
package com.hunthawk.framework.memcached;

import java.util.Date;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/**
 * @author BruceSun
 * 
 */
public class MemCachedClientWrapper {

	private static final Logger logger = LoggerFactory
			.getLogger(MemCachedClientWrapper.class);

	public static final int SECOND = 1000;

	public static final int MINUTE = 1000 * 60;

	public static final int HOUR = 1000 * 60 * 60;

	private CacheManager cacheMgr = null;

	private Cache ehcache = null;

	private Cache longEhcache = null;

	private Cache mediumEhcache = null;

	private String ehcacheName = "memcache";

	private String longEhcacheName = "longmemcache";

	private String mediumEhcacheName = "mediummemcache";

	private SockIOPool pool;

	private MemCachedClient mcc;

	private String name;

	private String serverList;

	private String weightList;

	private int initConn = 10;

	private int minConn = 5;

	private int maxConn = 50;

	private long maxIdleTime = 1000 * 60 * 30;

	private long maxBusyTime = 1000 * 60 * 5;

	private long maintThreadSleep = 1000 * 5;

	private int socketTimeOut = 1000 * 5;

	private int socketConnectTO = 1000 * 3;

	private boolean failover = false;

	private boolean nagleAlg = false;

	private boolean aliveCheck = false;

	private int cacheTimeSeconds = 300 * 1000;

	private boolean dogpilePreventionEnabled = true;

	private double dogpilePreventionExpirationFactor = 2;

	public static final Integer DOGPILE_TOKEN = 0;

	public boolean isDogpilePreventionEnabled() {
		return dogpilePreventionEnabled;
	}

	public void setDogpilePreventionEnabled(boolean dogpilePreventionEnabled) {
		this.dogpilePreventionEnabled = dogpilePreventionEnabled;
	}

	public double getDogpilePreventionExpirationFactor() {
		return dogpilePreventionExpirationFactor;
	}

	public void setDogpilePreventionExpirationFactor(
			double dogpilePreventionExpirationFactor) {
		if (dogpilePreventionExpirationFactor < 1.0) {
			throw new IllegalArgumentException(
					"dogpilePreventionExpirationFactor must be greater than 1.0");
		}
		this.dogpilePreventionExpirationFactor = dogpilePreventionExpirationFactor;
	}

	private String dogpileTokenKey(String objectKey) {
		return objectKey + ".dogpileTokenKey";
	}

	public void init() {
		try {
			if (null == pool || !pool.isInitialized()) {
				pool = SockIOPool.getInstance(name);

				pool.setServers(StringUtils.split(serverList, ','));
				String[] tmp = StringUtils.split(weightList, ',');
				Integer[] weights = new Integer[tmp.length];

				for (int i = 0; i < tmp.length; ++i) {
					weights[i] = NumberUtils.toInt(tmp[i], 0);
				}
				pool.setWeights(weights);
				pool.setInitConn(initConn);
				pool.setMinConn(minConn);
				pool.setMaxConn(maxConn);
				pool.setMaxIdle(maxIdleTime);
				pool.setMaxBusyTime(maxBusyTime);
				pool.setMaintSleep(maintThreadSleep);
				pool.setSocketConnectTO(socketConnectTO);
				pool.setSocketTO(socketTimeOut);
				pool.setNagle(nagleAlg);
				pool.setFailover(failover);
				pool.setAliveCheck(aliveCheck);
				pool.setHashingAlg(SockIOPool.NEW_COMPAT_HASH);
				pool.initialize();
			}
			mcc = new MemCachedClient(name);
			mcc.setCompressEnable(true);
			mcc.setCompressThreshold(4096);
			cacheMgr = CacheManager.create();
			ehcache = cacheMgr.getCache(ehcacheName);
			mediumEhcache = cacheMgr.getCache(mediumEhcacheName);
			longEhcache = cacheMgr.getCache(longEhcacheName);
		} catch (Exception e) {
			logger.error("[初始化SocketPool异常=" + e.getMessage() + "]", e);
		}

	}

	public void destroy() {
		if (null != pool) {
			pool.shutDown();
			pool = null;
		}
		if (null != cacheMgr) {
			cacheMgr.shutdown();
			cacheMgr = null;
		}
	}

	public void set(String key, Object value, int expire) {

		int cacheTime = (int) expire;
		if (dogpilePreventionEnabled) {
			String dogpileKey = dogpileTokenKey(key);
			if (logger.isDebugEnabled()) {
				logger
						.debug("Dogpile prevention enabled, setting token and adjusting object cache time. Key: "
								+ dogpileKey);

			}
			mcc.set(dogpileKey, DOGPILE_TOKEN, new Date(expire));
			cacheTime = (int) (expire * dogpilePreventionExpirationFactor);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Memcache.set　" + key);
		}
		mcc.set(key, value, new Date(cacheTime));
	}

	public void setNormal(String key, Object value, int expire) {
		mcc.set(key, value, new Date(expire));
	}

	public void set(String key, Object value) {
		set(key, value, HOUR);
	}

	public void delete(String key) {
		if (mcc != null)
			mcc.delete(key);
	}

	public Object get(String key) throws Exception {
		if (null == mcc)
			throw new Exception("MemCachedClient没有正常初始化");

		if (dogpilePreventionEnabled) {
			String dogpileKey = dogpileTokenKey(key);
			if (logger.isDebugEnabled()) {
				logger.debug("Checking dogpile key: " + dogpileKey);
			}

			if (mcc.get(dogpileKey) == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Dogpile key " + dogpileKey
							+ " not found updating token and returning null");
				}
				mcc.set(dogpileKey, DOGPILE_TOKEN, new Date(cacheTimeSeconds));
				return null;
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Dogpile token found for key " + dogpileKey
							+ ", getting cached object");
				}
			}
		}

		return mcc.get(key);
	}

	public Object getNormal(String key) throws Exception {
		if (null == mcc)
			throw new Exception("MemCachedClient没有正常初始化");
		return mcc.get(key);
	}

	public Object getAndSaveLocal(String key) throws Exception {
		if (ehcache != null) {

			Element element = ehcache.get(key);
			if (element != null) {
				return element.getValue();
			}
		}
		Object obj = get(key);
		if (ehcache != null) {

			Element element = new Element(key, obj);
			ehcache.put(element);

		}
		return obj;
	}

	public void setAndSaveLocal(String key, Object obj, int expire) {
		if (ehcache != null) {
			Element element = new Element(key, obj);
			ehcache.put(element);
		}
		set(key, obj, expire);
	}

	public void deleteForLocal(String key) {
		if (ehcache != null) {
			ehcache.remove(key);
		}
		delete(key);
	}

	public Object getAndSaveLocalMedium(String key) throws Exception {
		if (mediumEhcache != null) {

			Element element = mediumEhcache.get(key);
			if (element != null) {

				return element.getValue();
			}
		}
		Object obj = get(key);
		if (mediumEhcache != null) {

			Element element = new Element(key, obj);
			mediumEhcache.put(element);

		}
		return obj;
	}

	public void setAndSaveLocalMedium(String key, Object obj, int expire) {
		if (mediumEhcache != null) {
			Element element = new Element(key, obj);
			mediumEhcache.put(element);
		}
		set(key, obj, expire);
	}

	public void deleteForLocalMedium(String key) {
		if (mediumEhcache != null) {
			mediumEhcache.remove(key);
		}
		delete(key);
	}

	public Object getAndSaveLocalLong(String key) throws Exception {
		if (longEhcache != null) {

			Element element = longEhcache.get(key);
			if (element != null) {

				return element.getValue();
			}
		}
		Object obj = get(key);
		if (longEhcache != null) {

			Element element = new Element(key, obj);
			longEhcache.put(element);

		}
		return obj;
	}

	public void setAndSaveLong(String key, Object obj, int expire) {
		if (longEhcache != null) {
			Element element = new Element(key, obj);
			longEhcache.put(element);
		}
		set(key, obj, expire);
	}

	public void deleteForLocalLong(String key) {
		if (longEhcache != null) {
			longEhcache.remove(key);
		}
		delete(key);
	}

	public boolean storeCounter(String key, long counter) {
		if (null == mcc)
			return false;
		else
			return mcc.storeCounter(key, counter);
	}

	public long getCounter(String key) throws Exception {
		if (null == mcc)
			throw new Exception("MemCachedClient没有正常初始化");
		return mcc.getCounter(key);
	}

	public long incr(String key) throws Exception {
		if (null == mcc)
			throw new Exception("MemCachedClient没有正常初始化");
		return mcc.incr(key);
	}

	public long incr(String key, long value) throws Exception {
		if (null == mcc)
			throw new Exception("MemCachedClient没有正常初始化");
		return mcc.incr(key, value);
	}

	public long decr(String key) throws Exception {
		if (null == mcc)
			throw new Exception("MemCachedClient没有正常初始化");
		return mcc.decr(key);
	}

	public long decr(String key, long value) throws Exception {
		if (null == mcc)
			throw new Exception("MemCachedClient没有正常初始化");
		return mcc.decr(key, value);
	}

	/**
	 * @return the aliveCheck
	 */
	public boolean isAliveCheck() {
		return aliveCheck;
	}

	/**
	 * @param aliveCheck
	 *            the aliveCheck to set
	 */
	public void setAliveCheck(boolean aliveCheck) {
		this.aliveCheck = aliveCheck;
	}

	/**
	 * @return the failover
	 */
	public boolean isFailover() {
		return failover;
	}

	/**
	 * @param failover
	 *            the failover to set
	 */
	public void setFailover(boolean failover) {
		this.failover = failover;
	}

	/**
	 * @return the initConn
	 */
	public int getInitConn() {
		return initConn;
	}

	/**
	 * @param initConn
	 *            the initConn to set
	 */
	public void setInitConn(int initConn) {
		this.initConn = initConn;
	}

	/**
	 * @return the maintThreadSleep
	 */
	public long getMaintThreadSleep() {
		return maintThreadSleep;
	}

	/**
	 * @param maintThreadSleep
	 *            the maintThreadSleep to set
	 */
	public void setMaintThreadSleep(long maintThreadSleep) {
		this.maintThreadSleep = maintThreadSleep;
	}

	/**
	 * @return the maxBusyTime
	 */
	public long getMaxBusyTime() {
		return maxBusyTime;
	}

	/**
	 * @param maxBusyTime
	 *            the maxBusyTime to set
	 */
	public void setMaxBusyTime(long maxBusyTime) {
		this.maxBusyTime = maxBusyTime;
	}

	/**
	 * @return the maxConn
	 */
	public int getMaxConn() {
		return maxConn;
	}

	/**
	 * @param maxConn
	 *            the maxConn to set
	 */
	public void setMaxConn(int maxConn) {
		this.maxConn = maxConn;
	}

	/**
	 * @return the maxIdleTime
	 */
	public long getMaxIdleTime() {
		return maxIdleTime;
	}

	/**
	 * @param maxIdleTime
	 *            the maxIdleTime to set
	 */
	public void setMaxIdleTime(long maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	/**
	 * @return the minConn
	 */
	public int getMinConn() {
		return minConn;
	}

	/**
	 * @param minConn
	 *            the minConn to set
	 */
	public void setMinConn(int minConn) {
		this.minConn = minConn;
	}

	/**
	 * @return the nagleAlg
	 */
	public boolean isNagleAlg() {
		return nagleAlg;
	}

	/**
	 * @param nagleAlg
	 *            the nagleAlg to set
	 */
	public void setNagleAlg(boolean nagleAlg) {
		this.nagleAlg = nagleAlg;
	}

	/**
	 * @return the serverList
	 */
	public String getServerList() {
		return serverList;
	}

	/**
	 * @param serverList
	 *            the serverList to set
	 */
	public void setServerList(String serverList) {
		this.serverList = serverList;
	}

	/**
	 * @return the socketConnectTO
	 */
	public int getSocketConnectTO() {
		return socketConnectTO;
	}

	/**
	 * @param socketConnectTO
	 *            the socketConnectTO to set
	 */
	public void setSocketConnectTO(int socketConnectTO) {
		this.socketConnectTO = socketConnectTO;
	}

	/**
	 * @return the socketTimeOut
	 */
	public int getSocketTimeOut() {
		return socketTimeOut;
	}

	/**
	 * @param socketTimeOut
	 *            the socketTimeOut to set
	 */
	public void setSocketTimeOut(int socketTimeOut) {
		this.socketTimeOut = socketTimeOut;
	}

	/**
	 * @return the weightList
	 */
	public String getWeightList() {
		return weightList;
	}

	/**
	 * @param weightList
	 *            the weightList to set
	 */
	public void setWeightList(String weightList) {
		this.weightList = weightList;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getEhcacheName() {
		return ehcacheName;
	}

	public void setEhcacheName(String ehcacheName) {
		this.ehcacheName = ehcacheName;
	}

	public String getLongEhcacheName() {
		return longEhcacheName;
	}

	public void setLongEhcacheName(String longEhcacheName) {
		this.longEhcacheName = longEhcacheName;
	}

	public String getMediumEhcacheName() {
		return mediumEhcacheName;
	}

	public void setMediumEhcacheName(String mediumEhcacheName) {
		this.mediumEhcacheName = mediumEhcacheName;
	}

}
