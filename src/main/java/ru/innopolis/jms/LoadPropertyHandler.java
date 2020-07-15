package ru.innopolis.jms;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public interface LoadPropertyHandler {

    /**
     * Загрузить файл с property
     *
     * @param fileName
     *
     * @return
     *
     * @throws IOException
     */
    Properties loadPropertyFile(String fileName) ;


    /**
     * Установить значение в properties
     *
     * @param properties
     * @param appProps
     * @param propertyName
     */
    void setProperty(Map<String, String> properties, Properties appProps, String propertyName);
}
