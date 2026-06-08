import java.util.Map;

public class UserHandler extends AuthHandler {
    private final Map<String, String> usersAccounts;

    public UserHandler(Map<String, String> usersAccounts) {
        this.usersAccounts = usersAccounts;
    }

    @Override
    public void handle(Account account) {
        if (account.getAccessType() == Account.AccessType.User) {
            if (credentialsValid(account, usersAccounts)) {
                System.out.println("User access provided: " + account.getLogin());
            } else {
                System.out.println("Access denied: " + account.getLogin());
            }
        } else {
            passToNext(account);
        }
    }
}
