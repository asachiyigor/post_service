package faang.school.postservice.config.redis;

public interface MessageSender {
    void send(final String message);
}