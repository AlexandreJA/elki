package de.lmu.ifi.dbs.elki.properties;

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.LoggingConfiguration;

/**
 * Provides management of properties.
 * 
 * @author Arthur Zimek
 */
public final class Properties {
  private static Logging logger = Logging.getLogger(Properties.class);

  /**
   * The pattern to split for separate entries in a property string, which is a
   * &quot;,&quot;.
   */
  public static final Pattern PROPERTY_SEPARATOR = Pattern.compile(",");

  /**
   * The Properties for ELKI.
   */
  public static final Properties ELKI_PROPERTIES;

  static {
    File propertiesfile = new File(Properties.class.getPackage().getName().replace('.', File.separatorChar) + File.separatorChar + "ELKI.prp");
    if(propertiesfile.exists() && propertiesfile.canRead()) {
      ELKI_PROPERTIES = new Properties(propertiesfile.getAbsolutePath());
    }
    else // otherwise, the property-file should at least be available within the
         // jar-archive
    {
      ELKI_PROPERTIES = new Properties(Properties.class.getPackage().getName().replace('.', '/') + '/' + "ELKI.prp");
    }
  }

  /**
   * Stores the properties as defined by a property-file.
   */
  private final java.util.Properties properties;

  /**
   * Provides the properties as defined in the designated file.
   * 
   * @param filename name of a file to provide property-definitions.
   */
  private Properties(String filename) {
    LoggingConfiguration.assertConfigured();
    this.properties = new java.util.Properties();
    try {
      properties.load(ClassLoader.getSystemResourceAsStream(filename));
    }
    catch(Exception e) {
      logger.warning("Unable to load properties file " + filename + ".\n");
    }
  }

  /**
   * Provides the entries (as separated by {@link #PROPERTY_SEPARATOR
   * PROPERTY_SEPARATOR}) for a specified PropertyName.
   * 
   * @param propertyName the PropertyName of the property to retrieve
   * @return the entries (separated by {@link #PROPERTY_SEPARATOR
   *         PROPERTY_SEPARATOR}) for the specified PropertyName - if the
   *         property is undefined, the returned array is of length 0
   */
  public String[] getProperty(PropertyName propertyName) {
    String property = propertyName == null ? null : properties.getProperty(propertyName.getName());
    return property == null ? new String[0] : PROPERTY_SEPARATOR.split(property);
  }
  
  /**
   * Get a collection of all property names in this file.
   * 
   * @return Property names in this file.
   */
  public Set<String> getPropertyNames() {
    return properties.stringPropertyNames();
  }
}
