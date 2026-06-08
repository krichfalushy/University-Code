import java.util.Map;

public class AdminHandler extends AuthHandler {
    private static final int MIN_ADMIN_PASSWORD_LENGTH = 8;
    private final Map<String, String> adminsAccounts;

    public AdminHandler(Map<String, String> adminsAccounts) {
        this.adminsAccounts = adminsAccounts;
    }

    @Override
    public void handle(Account account) {
        if (account.getAccessType() == Account.AccessType.Administrator) {
            // адмінський пароль має бути валідним І достатньо довгим
            if (credentialsValid(account, adminsAccounts)
                    && account.getPassword().length() >= MIN_ADMIN_PASSWORD_LENGTH) {
                System.out.println("Administrator access provided: " + account.getLogin());
            } else {
                System.out.println("Access denied: " + account.getLogin());
            }
        } else {
            passToNext(account);
        }
    }
}
