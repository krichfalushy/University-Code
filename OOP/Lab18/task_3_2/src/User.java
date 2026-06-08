/** Користувач чату (Colleague). Спілкується лише через посередника. */
public interface User {
    void sendMessageAll(String message);
    void sendMessage(String message, String userTo);
    void sendMessageToGroup(String message, UserRole group);   // розсилка групі
    void receiveMassage(String message, String userFrom);
    String getUserId();
    UserRole getRole();
}
