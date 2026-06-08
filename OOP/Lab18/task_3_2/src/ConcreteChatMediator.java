import java.util.ArrayList;
import java.util.List;

/** Конкретний посередник: зберігає учасників і маршрутизує повідомлення. */
public class ConcreteChatMediator implements ChatMediator {

    private final List<User> users = new ArrayList<>();

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void sendToAll(String message, User from) {
        for (User user : users) {
            if (user != from) {
                user.receiveMassage(message, from.getUserId());
            }
        }
    }

    @Override
    public void sendToUser(String message, User from, String userToId) {
        for (User user : users) {
            if (user.getUserId().equals(userToId)) {
                user.receiveMassage(message, from.getUserId());
                return;
            }
        }
        System.out.println("[Mediator] user '" + userToId + "' not found.");
    }

    @Override
    public void sendToGroup(String message, User from, UserRole group) {
        for (User user : users) {
            if (user != from && user.getRole() == group) {
                user.receiveMassage(message, from.getUserId());
            }
        }
    }
}
