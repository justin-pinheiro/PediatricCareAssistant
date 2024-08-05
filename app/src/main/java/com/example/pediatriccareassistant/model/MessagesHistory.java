package com.example.pediatriccareassistant.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MessagesHistory
{
    ArrayList<Message> history;
    int maxSize;

    public MessagesHistory(int maxSize) {
        history = new ArrayList<>();
        this.maxSize = maxSize;
    }

    public void add(Message message)
    {
        if (history.size() >= maxSize)
        {
            history.remove(0);
        }
        history.add(message);
    }

    @NonNull
    @Override
    public String toString()
    {
        if (history.isEmpty())
            return "";

        StringBuilder text = new StringBuilder();
        for (Message message : history)
        {
            if (message.getSender().equals(Message.SenderType.USER))
                text.append(message.getSender()).append(":");

            text.append(message.getContent()).append("\n");
        }
        return text.toString();
    }

    public ArrayList<Message> getHistory() {
        return history;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
