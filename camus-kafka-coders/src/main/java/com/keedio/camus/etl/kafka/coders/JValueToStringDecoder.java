package com.keedio.camus.etl.kafka.coders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.linkedin.camus.coders.CamusWrapper;
import com.linkedin.camus.coders.Message;
import com.linkedin.camus.coders.MessageDecoder;
import com.linkedin.camus.coders.MessageDecoderException;
import org.apache.log4j.Logger;

import java.util.Properties;

import org.joda.time.DateTime;
import org.json4s.JsonAST;
import org.keedio.kafka.serializers.JValueDecoder;
import org.json4s.jackson.JsonMethods$;

public class JValueToStringDecoder extends MessageDecoder<Message, String> {

    private static final org.apache.log4j.Logger log = Logger.getLogger(JValueToStringDecoder.class);

    @Override
    public void init(Properties props, String topicName) {
        this.props     = props;
        this.topicName = topicName;
    }

    @Override
    public CamusWrapper<String> decode(Message message) {
        JValueDecoder decoder = new JValueDecoder();
        try {
            JsonAST.JValue jsonValue = decoder.fromBytes(message.getPayload());
            String jsonString = JsonMethods$.MODULE$.compact(jsonValue);
            long timestamp = extractTimestamp(jsonString);
            return new CamusWrapper<String>(jsonString, timestamp);
        } catch (Exception e) {
            log.error("Error decoding Kafka message from JValue", e);
            throw new MessageDecoderException(e);
        }
    }

    static private long extractTimestamp(String jsonString) {
        JsonParser gsonParser = new JsonParser();
        JsonElement jsonElement = gsonParser.parse( jsonString );

        long timestamp = System.currentTimeMillis();
        JsonElement dataElement = jsonElement.getAsJsonObject().get("Data");
        if ( dataElement != null && dataElement.isJsonArray() ) {
            JsonArray data = dataElement.getAsJsonArray();
            for (int i = 0; i < data.size(); i++) {
                try {
                    String sampleDateString = data.get(i).getAsJsonObject().getAsJsonPrimitive("SampleDate").getAsString();
                    DateTime sampleDate = new DateTime(sampleDateString);
                    timestamp = Math.min(timestamp, sampleDate.getMillis());
                } catch (Exception e) {
                    log.debug("Error parsing SampleDate", e);
                }
            }
        }
        return timestamp;
    }
}
