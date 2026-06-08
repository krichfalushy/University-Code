import java.util.Map;

/**
 * Базовий обробник ланцюга обов'язків для аутентифікації.
 * Кожен обробник або обробляє акаунт свого типу, або передає далі.
 */
public abstract class AuthHandler {

    private AuthHandler next;

    /** Додати наступний обробник у ланцюг (повертає його для зручного зчеплення). */
    public AuthHandler linkWith(AuthHandler next) {
        this.next = next;
        return next;
    }

    /** Обробити запит на аутентифікацію. */
    public abstract void handle(Account account);

    /** Передати запит далі по ланцюгу (або відмовити, якщо ланцюг скінчився). */
    protected void passToNext(Account account) {
        if (next != null) {
            next.handle(account);
        } else {
            System.out.println("Access denied: " + account.getLogin());
        }
    }

    /** Допоміжна перевірка логіна/пароля у сховищі. */
    protected boolean credentialsValid(Account account, Map<String, String> storage) {
        String stored = storage.get(account.getLogin());
        return stored != null && stored.equals(account.getPassword());
    }
}
