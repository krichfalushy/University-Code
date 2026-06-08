/** Конкретний користувач. Делегує відправлення посереднику. */
public class ConcreteUser implements User {

    final private String userId;
    final private UserRole role;
    final private ChatMediator mediator;

    public ConcreteUser(String userId, UserRole role, ChatMediator mediator) {
        this.userId = userId;
        this.role = role;
        this.mediator = mediator;
        mediator.addUser(this);
    }

    @Override
    public void sendMessageAll(String message) {
        System.out.println("User " + userId + " sends to ALL: " + message);
        mediator.sendToAll(message, this);
    }

    @Override
    public void sendMessage(String message, String userTo) {
        System.out.println("User " + userId + " sends to " + userTo + ": " + message);
        mediator.sendToUser(message, this, userTo);
    }

    @Override
    public void sendMessageToGroup(String message, UserRole group) {
        System.out.println("User " + userId + " sends to group " + group + ": " + message);
        mediator.sendToGroup(message, this, group);
    }

    @Override
    public void receiveMassage(String message, String userFrom) {
        System.out.println("User " + userId + " receives message: " + message + " from user " + userFrom);
    }

    @Override public String getUserId() { return userId; }
    @Override public UserRole getRole() { return role; }
}
