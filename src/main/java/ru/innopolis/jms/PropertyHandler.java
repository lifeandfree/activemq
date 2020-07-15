package ru.innopolis.jms;

public interface PropertyHandler {

    /**
     * Получить property по key
     *
     * @return
     */
    String getPropertyByKey(String key);
}
