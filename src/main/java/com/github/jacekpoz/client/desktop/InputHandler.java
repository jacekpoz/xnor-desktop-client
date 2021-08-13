package com.github.jacekpoz.client.desktop;

import com.github.jacekpoz.client.desktop.gui.ChatWindow;
import com.github.jacekpoz.common.sendables.Chat;
import com.github.jacekpoz.common.sendables.Message;
import com.github.jacekpoz.common.sendables.Sendable;
import com.github.jacekpoz.common.sendables.database.results.Result;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InputHandler {


    private final ChatWindow window;

    public InputHandler(ChatWindow w) {
        window = w;
    }

    public void start() {
        ExecutorService service = Executors.newCachedThreadPool();

        service.submit(() -> {
            String inputJSON;
            try {
                while ((inputJSON = window.getIn().readLine()) != null) {
//                    System.out.println("inputJSON: " + inputJSON + "\n");
                    Sendable input = window.getMapper().readValue(inputJSON, Sendable.class);
//                    System.out.println("input: " + input + "\n");
                    handleSendable(input);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    private void handleSendable(Sendable input) {
        if (input instanceof Message) {
            window.getMessageScreen().handleSendable(input);
        } else if (input instanceof Chat) {
            window.getClient().setChat((Chat) input);
        } else if (input instanceof Result) {
            Result<?> r = (Result<?>) input;
            long screenID = r.getQuery().getCallerID();
            window.getScreen(screenID).handleSendable(r);
        }
    }

}
