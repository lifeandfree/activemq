package ru.innopolis.jms;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class TransactionGson {

  private static final Logger logger = LogManager.getLogger(TransactionGson.class);

  private static final Gson GSON =
      new GsonBuilder()
          .setLongSerializationPolicy(LongSerializationPolicy.STRING)
          .create();

  /** Returns a configured instance of Gson. */
  public static Gson instance() {
    return GSON;
  }

}
