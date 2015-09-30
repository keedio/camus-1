package com.keedio.camus.etl.kafka.coders;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.linkedin.camus.coders.CamusWrapper;
import com.linkedin.camus.etl.kafka.coders.TestMessage;
import org.json4s.JsonAST;
import org.json4s.ReaderInput;
import org.json4s.jackson.JsonMethods$;
import org.junit.Assert;
import org.junit.Test;
import org.keedio.kafka.serializers.JValueEncoder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class TestJValueToStringDecoder {

    @Test
    public void testDecode() {

        JsonParser gsonParser = new JsonParser();
        JValueEncoder encoder = new JValueEncoder();

        JValueToStringDecoder testDecoder = new JValueToStringDecoder();
        testDecoder.init(new Properties(), "testTopic");

        JsonAST.JValue jValue = null;
        byte[] expectedBytes = new byte[0];
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/test/resources/example.json"));
            jValue = JsonMethods$.MODULE$.parse(new ReaderInput(br), false);
            expectedBytes = Files.readAllBytes(Paths.get("src/test/resources/example.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] actualBytes = encoder.toBytes(jValue);
        CamusWrapper decodedMessage = testDecoder.decode(new TestMessage().setPayload(actualBytes));

        JsonElement actual = gsonParser.parse(decodedMessage.getRecord().toString());
        JsonElement expected = gsonParser.parse( new String(expectedBytes, Charset.defaultCharset()) );

        Assert.assertEquals("JSONs shouldn't differ", expected, actual);
    }
}
