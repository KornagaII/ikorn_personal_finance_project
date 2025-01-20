import java.io.*;
import java.util.*;

public class FinanceManager {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String DATA_FILE = "finance_data.txt";
    private static final Map<String, User> users = new HashMap<>();
    private static User currentUser;

    public static void main(String[] args) {
        loadData();
        while (true) {
            System.out.println("\n1. Авторизация\n2. Регистрация\n3. Выход");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    saveData();
                    System.exit(0);
                default:
                    System.out.println("Неверный ввод. Попробуйте снова.");
            }
        }
    }

    private static void login() {
        System.out.print("Логин: ");
        String login = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        if (users.containsKey(login) && users.get(login).validatePassword(password)) {
            currentUser = users.get(login);
            System.out.println("Добро пожаловать, " + currentUser.getUsername() + "!");
            manageWallet();
        } else {
            System.out.println("Неверный логин или пароль.");
        }
    }

    private static void register() {
        System.out.print("Введите логин: ");
        String login = scanner.nextLine();
        if (users.containsKey(login)) {
            System.out.println("Пользователь с таким логином уже существует.");
            return;
        }
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        User user = new User(login, password);
        users.put(login, user);
        System.out.println("Регистрация завершена.");
    }

    private static void manageWallet() {
        while (true) {
            System.out.println("\n1. Добавить доход\n2. Добавить расход\n3. Установить бюджет\n4. Показать статистику\n5. Выйти");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    addIncome();
                    break;
                case "2":
                    addExpense();
                    break;
                case "3":
                    setBudget();
                    break;
                case "4":
                    showStatistics();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Неверный ввод. Попробуйте снова.");
            }
        }
    }

    private static void addIncome() {
        System.out.print("Категория дохода: ");
        String category = scanner.nextLine();
        System.out.print("Сумма: ");
        double amount = Double.parseDouble(scanner.nextLine());
        currentUser.addIncome(category, amount);
        System.out.println("Доход добавлен.");
    }

    private static void addExpense() {
        System.out.print("Категория расхода: ");
        String category = scanner.nextLine();
        System.out.print("Сумма: ");
        double amount = Double.parseDouble(scanner.nextLine());
        if (currentUser.addExpense(category, amount)) {
            System.out.println("Расход добавлен.");
        } else {
            System.out.println("Недостаточно средств.");
        }
    }

    private static void setBudget() {
        System.out.print("Категория: ");
        String category = scanner.nextLine();
        System.out.print("Бюджет: ");
        double budget = Double.parseDouble(scanner.nextLine());
        currentUser.setBudget(category, budget);
        System.out.println("Бюджет установлен.");
    }

    private static void showStatistics() {
        currentUser.showStatistics();
    }

    private static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных.");
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Map<String, User> loadedUsers = (Map<String, User>) ois.readObject();
                users.putAll(loadedUsers);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Ошибка при загрузке данных.");
            }
        }
    }
}
