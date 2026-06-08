import java.util.HashMap;
import java.util.Map;

public class Client {

    public static void main(String[] args) {

        Map<String, String> adminsAccounts = new HashMap<>();
        adminsAccounts.put("admin", "123qweASD");
        adminsAccounts.put("administrator", "1234");

        Map<String, String> usersAccounts = new HashMap<>();
        usersAccounts.put("andrii", "1234");
        usersAccounts.put("black_knight", "S@ruman1");
        usersAccounts.put("white_knight", "ruman1");

        // Будуємо ланцюг: Guest -> User -> Admin
        AuthHandler chain = new GuestHandler();
        chain.linkWith(new UserHandler(usersAccounts))
             .linkWith(new AdminHandler(adminsAccounts));

        Account[] users = {
            Account.createAdmin("admin", "123qweASD"),
            Account.createAdmin("administrator", "1234"),
            Account.createUser("andrii", "1234"),
            Account.createGuest(),
            Account.createUser("black_knight", "S@ruman1"),
            Account.createUser("white_knight", "S111"),
            Account.createGuest(),
        };

        for (Account user : users) {
            chain.handle(user);
        }
    }
}
