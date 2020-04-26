package ch.propbuddy.propbuddy.domain;


import java.util.Objects;

public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;

    public ChatMessage(MessageType type, String content, String sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
    }

    public ChatMessage() {}

    public MessageType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return type == that.type &&
                Objects.equals(content, that.content) &&
                Objects.equals(sender, that.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, content, sender);
    }

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        INFO,
        PDF,
        FILTER_CHANGED,
        PDF_PROPS_UPDATED
    }
}