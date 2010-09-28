package com.hapiware.jmx.log4j;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.LoggingMXBean;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

/**
 * Logging provides a management access to {@link org.apache.log4j.Logger} objects at runtime. 
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 * @see com.hapiware.jmx.log4j.JmxLog4jLogger
 * @see java.util.logging.Logging
 */
public class Logging
	implements
		LoggingMXBean
{
	private final LoggerRepository _loggerRepository;
	
	
	/**
	 * Constructs a {@code Logging} object. You should <b>never</b> create {@code Logging} object
	 * by yourself. {@link JmxLog4jLogger#register(Logger)} method creates {@code Logging} for
	 * you when needed.
	 * 
	 * @param loggerRepository
	 * 		A registry for log4j loggers. 
	 * 
	 * @see com.hapiware.jmx.log4j.JmxLog4jLogger
	 */
	public Logging(LoggerRepository loggerRepository)
	{
		_loggerRepository = loggerRepository;
	}
	
	public String getLoggerLevel(String loggerName)
	{
		Level level = getLogger(loggerName).getLevel();
		return level == null ? null : level.toString();
	}

	public List<String> getLoggerNames()
	{
		Enumeration<?> e = _loggerRepository.getCurrentLoggers();
		List<String> retVal = new ArrayList<String>();
		while(e.hasMoreElements())
			retVal.add(((Logger)e.nextElement()).getName());
		return retVal;
	}

	public String getParentLoggerName(String loggerName)
	{
		return getLogger(loggerName).getParent().getName();
	}

	public void setLoggerLevel(String loggerName, String levelName)
	{
		getLogger(loggerName).setLevel(
			levelName == null ? null : Level.toLevel(levelName)
		);
	}
	
	/**
	 * Returns a root logger if the {@code loggerName} is "" (i.e. an empty {@code String}).
	 * 
	 * @param loggerName
	 * 
	 * @return
	 * 		A logger.
	 */
	private Logger getLogger(String loggerName)
	{
		if(loggerName == null)
			loggerName = "";
		return
			loggerName.length() == 0 ? 
				_loggerRepository.getRootLogger() : _loggerRepository.getLogger(loggerName);
	}
}
