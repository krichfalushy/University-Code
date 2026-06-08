public class Client {
    public static void main(String[] args) {

        ChatMediator chat = new ConcreteChatMediator();

        User user1 = new ConcreteUser("User1", UserRole.ADMIN, chat);
        User user2 = new ConcreteUser("User2", UserRole.MODERATOR, chat);
        User user3 = new ConcreteUser("User3", UserRole.MODERATOR, chat);
        User user4 = new ConcreteUser("User4", UserRole.MEMBER, chat);

        // Розсилка всім
        user1.sendMessageAll("Hello, world!");

        System.out.println();
        // Особисте повідомлення
        user2.sendMessage("Hello!", "User1");

        System.out.println();
        // Розсилка окремій групі — модераторам
        user1.sendMessageToGroup("Moderators meeting at 5pm", UserRole.MODERATOR);

        System.out.println();
        // Розсилка адміністраторам
        user4.sendMessageToGroup("Please reset my password", UserRole.ADMIN);
    }
}
