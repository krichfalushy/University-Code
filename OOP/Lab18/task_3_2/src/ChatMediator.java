/**
 * Посередник чату (Mediator). Користувачі не зв'язані напряму —
 * усі повідомлення проходять через посередника.
 */
public interface ChatMediator {
    void addUser(User user);
    void sendToAll(String message, User from);
    void sendToUser(String message, User from, String userToId);
    void sendToGroup(String message, User from, UserRole group);
}
