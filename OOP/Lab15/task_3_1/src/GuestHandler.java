public class GuestHandler extends AuthHandler {
    @Override
    public void handle(Account account) {
        if (account.getAccessType() == Account.AccessType.Guest) {
            System.out.println("Guest access provided!");
        } else {
            passToNext(account);
        }
    }
}
