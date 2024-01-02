package com.discstock.demo;
import java.net.*;
import java.io.*;
import java.util.*;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

class RepeatedDiscCounter extends TimerTask {
    private URL currentUrl;
    private int initialDiscAmount;
    private Timer currentTimer;
    private PhoneNumber myNumber;
    private PhoneNumber incoming;
    public RepeatedDiscCounter(URL currentUrl, int initialDiscAmount, Timer currentTimer, PhoneNumber myNumber, PhoneNumber incoming){
        this.currentUrl = currentUrl;
        this.initialDiscAmount = initialDiscAmount;
        this.currentTimer = currentTimer;
        this.myNumber = myNumber;
        this.incoming = incoming;
    }
    public void run() {
        try {
            BufferedReader byteToText = new BufferedReader(new InputStreamReader(currentUrl.openStream()));
            CharSequence search = "list-group";
            CharSequence end = "</html>";
            int currentDiscCounter = 0;
            while (!byteToText.readLine().contains(end)) {
                String currentText = byteToText.readLine();
                if (currentText.contains(search)) {
                    currentDiscCounter++;
                }
            }
            currentDiscCounter--;
            if (currentDiscCounter > initialDiscAmount) {
                Message response = Message.creator(incoming, myNumber, "Your disc has been restocked!").create();
                currentTimer.cancel();
            } else if (currentDiscCounter < initialDiscAmount) {
                initialDiscAmount = currentDiscCounter;
            }
        } catch (Exception e) {
            Message response = Message.creator(incoming, myNumber, "There was an unexpected error, your tracker has been terminated.").create();
            currentTimer.cancel();
        }
    }
}

public class discRestockBot {
    public static int discCounter(URL currentUrl) throws IOException {
        BufferedReader byteToText = new BufferedReader(new InputStreamReader(currentUrl.openStream()));
        CharSequence search = "list-group";
        CharSequence end = "</html>";
        int currentDiscCounter = 0;
        while (!byteToText.readLine().contains(end)) {
            String currentText = byteToText.readLine();
            if (currentText.contains(search)) {
                currentDiscCounter++;
            }
        }
        currentDiscCounter--;
        return currentDiscCounter;
    }
    public static void main(String args[]) throws IOException {
        String yo = System.getenv("ACCOUNT_SID");
        String yo2 = System.getenv("AUTH_TOKEN");
        Twilio.init(yo, yo2);
        PhoneNumber myNumber = new com.twilio.type.PhoneNumber("+14842323105");
        TimerTask refresh = new TimerTask() {
            public void run() {
                ResourceSet<Message> messages = Message.reader().limit(20L).read();
                for (Message currentMessage : messages) {
                    PhoneNumber incoming = currentMessage.getFrom();
                    try {
                        URL currentUrl = new URL(currentMessage.getBody());
                        int initialDiscAmount = discCounter(currentUrl);
                        if (initialDiscAmount >= 30) {
                            Message response = Message.creator(incoming, myNumber, "There are already many of these discs in stock. Tracker was not placed.").create();
                        } else {
                            Timer currentTimer = new Timer();
                            RepeatedDiscCounter tracker = new RepeatedDiscCounter(currentUrl, initialDiscAmount, currentTimer, myNumber, incoming);
                            currentTimer.schedule(tracker, 0L , 1000L * 60L * 5L);
                        }
                    } catch (Exception e) {
                        Message response = Message.creator(incoming, myNumber, "URL was most likely invalid, tracker was not created.").create();
                        continue;
                    }
                }
            }
        };
        Timer refresher = new Timer();
        refresher.schedule(refresh, 0L, 1000L * 60L);
    }
}