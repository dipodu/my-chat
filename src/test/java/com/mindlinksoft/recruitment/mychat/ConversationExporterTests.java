package com.mindlinksoft.recruitment.mychat;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link ConversationExporter}.
 */
public class ConversationExporterTests {

    public static final int HIDE_WORDS_SELECTION = 3;
    public static final int FILTER_USER_SELECTION = 1;
    public static final int WRITE_WITH_NO_FLAG = 0;
    public static final int HIDE_CC_PHONE_SELECTION = 4;

     @Test
    public void testConversationExporterConfiguration() {

        ConversationExporter exporter = new ConversationExporter();
        String[] inputs = {"/User/Documents/myProject/chat.txt", "/User/Documents"};

        ConversationExporterConfiguration configuration = new CommandLineArgumentParser().parseCommandLineArguments(inputs);

        String actualInputDestination = configuration.inputFilePath;
        String actualOutputDestination = configuration.outputFilePath;
        
        String expectedInputDestination = "/User/Documents/myProject/chat.txt";
        String expectedOutputDestination = "/User/Documents";

        Assert.assertEquals(expectedInputDestination, actualInputDestination);
        Assert.assertEquals(expectedOutputDestination, actualOutputDestination);
    }
    
    
    
    @Test
    public void testHidePhoneNumberAndCreditCardInConversationMessage() {

        ConversationExporter exporter = new ConversationExporter();
        List<String> arguments = new ArrayList<>();

        String timestamp = "1568470912";
        String name = "John";
        String message = "my number is +447222425965 call me back";
        int filterFlags = HIDE_CC_PHONE_SELECTION;

        List<String> processedMessage = exporter.filterConversation(timestamp, name, message, filterFlags, arguments);

        String actualMessage = processedMessage.get(2);
        String expectedMessage = "my number is +**redacted** call me back";

        Assert.assertEquals(expectedMessage, actualMessage);
        String testMessage2 = "my both my debit card: 9876987652637487 9874-9876-9876-6535";
        List<String> processedMessage2 = exporter.filterConversation(timestamp, name, testMessage2, filterFlags, arguments);

        String actualMessage2 = processedMessage2.get(2);
        String expectedMessage2 = "my both my debit card: **redacted** **redacted**";

        Assert.assertEquals(expectedMessage2, actualMessage2);

    }

    @Test
    public void testHideWordsInConversationMessage() {

        ConversationExporter exporter = new ConversationExporter();
        List<String> arguments = new ArrayList<>();

        String timestamp = "1568470912";
        String name = "John";
        String message = "the weather is always sunny in london. it never rains";
        int filterFlags = HIDE_WORDS_SELECTION;
        arguments.add("sunny");
        arguments.add("never");

        List<String> processedMessage = exporter.filterConversation(timestamp, name, message, filterFlags, arguments);

        String actualMessage = processedMessage.get(2);
        String expectedMessage = "the weather is always **redacted** in london. it **redacted** rains";

        Assert.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testFilterUserInConversationMessage() {

        ConversationExporter exporter = new ConversationExporter();
        List<String> arguments = new ArrayList<>();

        String timestamp = "1448470912";
        String name = "prince";
        String message = "the pizza is coming. relax";
        int filterFlags = FILTER_USER_SELECTION;
        arguments.add("prince");

        List<String> processedConversationData = exporter.filterConversation(timestamp, name, message, filterFlags, arguments);

        String expectedName = "prince";
        String actualName = processedConversationData.get(1);

        Assert.assertEquals(expectedName, actualName);

    }
    
    class InstantDeserializer implements JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonPrimitive()) {
                throw new JsonParseException("Expected instant represented as JSON number, but no primitive found.");
            }

            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

            if (!jsonPrimitive.isNumber()) {
                throw new JsonParseException("Expected instant represented as JSON number, but different primitive found.");
            }

            return Instant.ofEpochSecond(jsonPrimitive.getAsLong());
        }
    }
}
