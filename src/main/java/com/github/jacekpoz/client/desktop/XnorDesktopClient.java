package com.github.jacekpoz.client.desktop;

import com.github.jacekpoz.client.desktop.gui.ChatWindow;
import com.github.jacekpoz.common.sendables.Chat;
import com.github.jacekpoz.common.sendables.User;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

public class XnorDesktopClient {

    @Getter
    private final Socket socket;
    @Getter
    private final ChatWindow window;
    @Getter @Setter
    private User user;
    @Getter @Setter
    private Chat chat;
    @Getter @Setter
    private boolean isLoggedIn;
    @Getter @Setter
    private boolean isOnline;
    @Getter @Setter
    private boolean isVLCAvailable;

    public XnorDesktopClient(Socket s, boolean isOnline, boolean isVLCAvailable) {
        socket = s;
        this.isOnline = isOnline;
        this.isVLCAvailable = isVLCAvailable;
        window = new ChatWindow(this);
        user = new User(-1, "dupa", "dupa dupa", LocalDateTime.MIN);
        chat = new Chat(-1, "dupa", LocalDateTime.MIN, -1);
    }

    public void start() {
        window.start();
    }

    public void stop() throws IOException {
        if (socket != null) socket.close();
        window.dispose();
    }

}
