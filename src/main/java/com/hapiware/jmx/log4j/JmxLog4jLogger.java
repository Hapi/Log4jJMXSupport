package com.hapiware.jmx.log4j;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;


/**
 * Adds a JMX support for log4j loggers by implementing java.util.logging.LoggingMXBean interface.
 * This way JMX clients can manage log4j similarly than Java loggers. Log4j has it's own JMX support
 * but according to the documentation it is not at the production level.
 * 
 * <h3>Usage</h3>
 * <h4>Modifying source code</h4>
 * {@code JmxLog4jLogger} is a factory class to get or register {@link org.apache.log4j.Logger}
 * objects. You either use one of the {@code getLogger()} methods or the {@code register()}
 * method, not both. The easiest way is to replace your current log4j {@code getLogger()} calls
 * with respective methods from this class and this is the preferred way. Here are some examples.
 * So, instead of this:
 * <pre>
 * 	private final static Logger LOGGER = Logger.getLogger(MyClass.class);
 * </pre>
 * 
 * a logger is fetched (created) like this:
 * <pre>
 * 	private final static Logger LOGGER = JmxLog4jLogger.getLogger(MyClass.class);
 * </pre>
 * 
 * The other option is to use {@link JmxLog4jLogger#register(Logger)} method in {@code static}
 * block like this:
 * <pre>
 * 	private final static Logger LOGGER = Logger.getLogger(MyClass.class);
 * 	...
 * 	static {
 * 		JmxLog4jLogger.register(LOGGER);
 * 	}
 * 	...
 * </pre>
 * 
 * 
 * <h4>Without modifying source code</h4>
 * If you have no access to source code (or does not want to change it) you can use
 * {@link com.hapiware.asm.log4j.Log4jJmxSupportAgentDelegate}.
 * 
 * 
 * <h3>Object name</h3>
 * Object name for the MBean is {@code "com.hapiware.log4j:type=Logging"}.
 * 
 * 
 * <h3>Root logger</h3>
 * Notice that using this library unifies the root logger handling with Java logging.
 * In practice this means that if logger name is an empty {@code String} ("") it is
 * then interpreted as a root logger.
 * 
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 * @see Logging
 */
public class JmxLog4jLogger
{
	private static final String LOGGING_NAME = "com.hapiware.log4j:type=Logging";
	
	
	/**
	 * Registers a logger to the management server.
	 * 
	 * @param logger
	 * 		A logger to be registered for management.
	 */
	synchronized public static void register(Logger logger)
	{
		try {
			ManagementFactory.getPlatformMBeanServer().createMBean(
				"com.hapiware.jmx.log4j.Logging",
				new ObjectName(LOGGING_NAME),
				new Object[] { logger.getLoggerRepository() },
				new String[] { "org.apache.log4j.spi.LoggerRepository" }
			);
		}
		catch(MBeanException e) {
			e.printStackTrace();
		}
		catch(InstanceAlreadyExistsException e) {
			e.printStackTrace();
		}
		catch(NotCompliantMBeanException e) {
			e.printStackTrace();
		}
		catch(MalformedObjectNameException e) {
			e.printStackTrace();
		}
		catch(ReflectionException e) {
			e.printStackTrace();
		}
		catch(NullPointerException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Retrieve the appropriate {@link org.apache.log4j.Logger} instance.
	 * 
	 * @see <a href="http://logging.apache.org/log4j/" target="_blank">log4j documentation</a>
	 */
	public static Logger getLogger(Class<?> c)
	{
		Logger logger = Logger.getLogger(c);
		register(logger);
		return logger;
	}

	
	/**
	 * Retrieve the appropriate {@link org.apache.log4j.Logger} instance.
	 * 
	 * @see <a href="http://logging.apache.org/log4j/" target="_blank">log4j documentation</a>
	 */
	public static Logger getLogger(String name)
	{
		Logger logger = Logger.getLogger(name);
		register(logger);
		return logger;
	}

	
	/**
	 * Retrieve the appropriate {@link org.apache.log4j.Logger} instance.
	 * 
	 * @see <a href="http://logging.apache.org/log4j/" target="_blank">log4j documentation</a>
	 */
	public static Logger getLogger(String name, LoggerFactory factory)
	{
		Logger logger = Logger.getLogger(name, factory);
		register(logger);
		return logger;
	}
}
