package com.hunthawk.framework.util;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>国际化</p>
 * @author sunquanzhi
 *
 */
public class I18NUtil
{
    
    private static ThreadLocal<Locale> threadLocale = new ThreadLocal<Locale>();
    
    
    private static ThreadLocal<Locale> threadContentLocale = new ThreadLocal<Locale>();
    
    
    private static Set<String> resouceBundleBaseNames = new HashSet<String>();
    
   
     
    private static Map<Locale, Set<String>> loadedResourceBundles = new HashMap<Locale, Set<String>>();
    
    
    private static Map<Locale, Map<String, String>> cachedMessages = new HashMap<Locale, Map<String, String>>();
    
  
    private static ReadWriteLock lock = new ReentrantReadWriteLock();
    private static Lock readLock = lock.readLock();
    private static Lock writeLock = lock.writeLock();
    
    
    public static void setLocale(Locale locale)
    {
        threadLocale.set(locale);
    }

  
    public static Locale getLocale()
    {
        Locale locale = threadLocale.get(); 
        if (locale == null)
        {
            locale = Locale.getDefault();
        }
        return locale;
    }
    
   
    public static void setContentLocale(Locale locale)
    {
        threadContentLocale.set(locale);
    }

 
    public static Locale getContentLocale()
    {
        Locale locale = threadContentLocale.get(); 
        if (locale == null)
        {
            locale = getLocale();
        }
        return locale;
    }
    
   
     
    public static Locale getNearestLocale(Locale templateLocale, Set<Locale> options)
    {
        if (options.isEmpty())                        
        {
            return null;
        }
        else if (templateLocale == null)
        {
            for (Locale locale : options)
            {
                return locale;
            }
        }
        else if (options.contains(templateLocale))    
        {
            return templateLocale;
        }
      
        Set<Locale> remaining = new HashSet<Locale>(options);
        
       
        Locale lastMatchingOption = null;
        String templateLanguage = templateLocale.getLanguage();
        if (templateLanguage != null && !templateLanguage.equals(""))
        {
            Iterator<Locale> iterator = remaining.iterator();
            while (iterator.hasNext())
            {
                Locale option = iterator.next();
                if (!templateLanguage.equals(option.getLanguage()))
                {
                    iterator.remove();                 
                }
                else
                {
                    lastMatchingOption = option;     
                }
            }
        }
        if (remaining.isEmpty())
        {
            return null;
        }
        else if (remaining.size() == 1 && lastMatchingOption != null)
        {
            return lastMatchingOption;
        }
        
  
        lastMatchingOption = null;
        String templateCountry = templateLocale.getCountry();
        if (templateCountry != null && !templateCountry.equals(""))
        {
            Iterator<Locale> iterator = remaining.iterator();
            while (iterator.hasNext())
            {
                Locale option = iterator.next();
                if (!templateCountry.equals(option.getCountry()))
                {
                
                    iterator.remove();
                }
                else
                {
                    lastMatchingOption = option;      
                }
            }
        }
        if (remaining.isEmpty())
        {
            return null;
        }
        else if (remaining.size() == 1 && lastMatchingOption != null)
        {
            return lastMatchingOption;
        }
        else
        {
           
            for (Locale locale : remaining)
            {
                return locale;
            }
        }
       
        throw new RuntimeException("Logic should not allow code to get here.");
    }
    
   
    public static Locale parseLocale(String localeStr)
    {
        Locale locale = Locale.getDefault();
        
        StringTokenizer t = new StringTokenizer(localeStr, "_");
        int tokens = t.countTokens();
        if (tokens == 1)
        {
           locale = new Locale(t.nextToken());
        }
        else if (tokens == 2)
        {
           locale = new Locale(t.nextToken(), t.nextToken());
        }
        else if (tokens == 3)
        {
           locale = new Locale(t.nextToken(), t.nextToken(), t.nextToken());
        }
        
        return locale;
    }
    
  
    /**
     * <p>注册资源文件</p>
     * @param bundleBaseName
     */
    public static void registerResourceBundle(String bundleBaseName)
    {
        try
        {
            writeLock.lock();
            resouceBundleBaseNames.add(bundleBaseName);
        }
        finally
        {
            writeLock.unlock();
        }
    }
    
    /**
     * <p>获得国际化消息</p>
     * @param messageKey
     * @return
     */
    public static String getMessage(String messageKey)
    {
        return getMessage(messageKey, getLocale());
    }
    
    
    public static String getMessage(String messageKey, Locale locale)
    {
        String message = null;
        Map<String, String> props = getLocaleProperties(locale);
        if (props != null)
        {
            message = props.get(messageKey);
        }                
        return message;
    }
    
   
    public static String getMessage(String messageKey, Object ... params)
    {
        return getMessage(messageKey, getLocale(), params);
    }
    
   
    public static String getMessage(String messageKey, Locale locale, Object ... params)
    {
        String message = getMessage(messageKey, locale);
        if (message != null && params != null)
        {
            message = MessageFormat.format(message, params);
        }
        return message;
    }
    
    
    private static Map<String, String> getLocaleProperties(Locale locale)
    {
        Set<String> loadedBundles = null;
        Map<String, String> props = null;
        int loadedBundleCount = 0;
        try
        {
            readLock.lock();
            loadedBundles = loadedResourceBundles.get(locale);
            props = cachedMessages.get(locale);
            loadedBundleCount = resouceBundleBaseNames.size();
        }
        finally
        {
            readLock.unlock();
        }
        
        if (loadedBundles == null)
        {
            try
            {
                writeLock.lock();
                loadedBundles = new HashSet<String>();
                loadedResourceBundles.put(locale, loadedBundles);
            }
            finally
            {
                writeLock.unlock();
            }
        }
        
        if (props == null)
        {
            try
            {
                writeLock.lock();
                props = new HashMap<String, String>();
                cachedMessages.put(locale, props);
            }
            finally
            {
                writeLock.unlock();
            }
        }
                
        if (loadedBundles.size() != loadedBundleCount)
        {
            try
            {
                writeLock.lock();
                for (String resourceBundleBaseName : resouceBundleBaseNames)
                {
                    if (loadedBundles.contains(resourceBundleBaseName) == false)
                    {
                        ResourceBundle resourcebundle = ResourceBundle.getBundle(resourceBundleBaseName, locale);
                        Enumeration<String> enumKeys = resourcebundle.getKeys();
                        while (enumKeys.hasMoreElements() == true)
                        {
                            String key = enumKeys.nextElement();
                            props.put(key, resourcebundle.getString(key));
                        }
                        loadedBundles.add(resourceBundleBaseName);
                    }
                }
            }
            finally
            {
                writeLock.unlock();
            }
        }
        
        return props;
    }
}
