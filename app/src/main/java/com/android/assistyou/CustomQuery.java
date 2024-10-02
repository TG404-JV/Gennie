package com.android.assistyou;

import java.util.HashMap;

public class CustomQuery {
    private HashMap<String, String> questionResponses;

    public void customList() {
        questionResponses = new HashMap<>();

        // Adding questions and responses to the HashMap
        questionResponses.put("What is Your Name", "Hey Dear! \n I'M Gennie Your Personal AI assistant");
        questionResponses.put("name", "Hey Dear! \n I'M Gennie Your Personal AI assistant");
        questionResponses.put("Your Name","hi");
        questionResponses.put("Who designed You", "Genie was designed by Tejas Kale");
        questionResponses.put("What can you do", "Genie can assist you with managing tasks, providing information, setting reminders, and more.");
        questionResponses.put("How do you work", "Genie works by utilizing advanced AI algorithms to understand your commands and provide appropriate responses.");
        questionResponses.put("Where can I download you", "You can download Genie from the App Store or Google Play Store.");
        questionResponses.put("How do I use you", "Using Genie is simple! Just speak or type your commands or questions, and Genie will provide assistance accordingly.");
        questionResponses.put("Can you set reminders?", "Yes, Genie can set reminders for you. Just let me know the details of the reminder, and I'll take care of it.");
        questionResponses.put("Do you support multiple languages?", "Currently, Genie supports English, but we're working on adding support for more languages in the future.");
        questionResponses.put("Can you provide weather updates?", "Yes, Genie can provide weather updates for your location. Just ask, and I'll fetch the latest forecast for you.");

    }


}
