package ru.innopolis.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class LoadPropertyHandlerImpl implements LoadPropertyHandler {

    private static final Logger logger = LogManager.getLogger(LoadPropertyHandlerImpl.class.getName());

    @Override
    public void setProperty(Map<String, String> properties, Properties appProps, String propertyName) {
        String property = appProps.getProperty(propertyName);
        if (property == null) {
            throw new RuntimeException("Property is not found  for " + propertyName);
        }
        properties.put(propertyName, property);
        logger.debug(propertyName + " is " + property);
    }

    @Override
    public Properties loadPropertyFile(String propertyFileName) {
        Properties appProps = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream(propertyFileName);

        if (input == null) {
            throw new RuntimeException("Sorry, unable to find " + propertyFileName);
        }

        try {
            appProps.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return appProps;
    }
}
