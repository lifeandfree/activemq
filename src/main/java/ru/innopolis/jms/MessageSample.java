package ru.innopolis.jms;

import java.util.Random;
import java.util.UUID;

/**
 * MesssageSample.
 *
 * @author Ilya_Sukhachev
 */
public class MessageSample implements MessageBrokerMessage {

    private UUID id = UUID.randomUUID();

    @Override
    public String toString() {
        return "MessageSample{" +
                "id=" + id +
                '}';
    }
}
