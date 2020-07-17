package ru.innopolis.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class TransactionGson {

    private static final Logger logger = LogManager.getLogger(TransactionGson.class);

    private static final Gson GSON =
            new GsonBuilder()
                    .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                    .create();

    public static Gson instance() {
        return GSON;
    }

}
