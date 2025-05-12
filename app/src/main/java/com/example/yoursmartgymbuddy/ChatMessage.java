    package com.example.yoursmartgymbuddy;

    public class ChatMessage {
        public static final int SENT_BY_USER = 1;
        public static final int SENT_BY_BOT = 2;

        private String message;
        private int sender;

        public ChatMessage(String message, int sender) {
            this.message = message;
            this.sender = sender;
        }

        public String getMessage() {
            return message;
        }

        public int getSender() {
            return sender;
        }
    }
