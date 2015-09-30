package com.keedio.camus.etl.kafka.coders;

import com.linkedin.camus.coders.CamusWrapper;
import com.linkedin.camus.coders.Message;
import com.linkedin.camus.coders.MessageDecoder;
import com.linkedin.camus.coders.MessageDecoderException;
import org.apache.log4j.Logger;

import java.util.Properties;

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
            long timestamp = System.currentTimeMillis();
            String jsonString = JsonMethods$.MODULE$.compact(jsonValue);
            return new CamusWrapper<String>(jsonString, timestamp);
        } catch (Exception e) {
            log.error("Error decoding Kafka message from JValue", e);
            throw new MessageDecoderException(e);
        }
    }
}
