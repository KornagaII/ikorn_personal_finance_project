import java.io.*;
import java.util.*;

public class FinancialApp {
    private static final String FILE_NAME = "users_data.dat";
    private static final Map<String, UserAccount> userAccounts = new HashMap<>();
    private static UserAccount activeUser;
    private static final Scanner inputScanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadUserAccounts();
        while (true) {
            System.out.println("\n1. Войти\n2. Создать аккаунт\n3. Выход");
            String option = inputScanner.nextLine();
            switch (option) {
                case "1" -> loginUser();
                case "2" -> createAccount();
                case "3" -> {
                    saveUserAccounts();
                    System.exit(0);
                }
                default -> System.out.println("Выберите правильный пункт.");
            }
        }
    }

    private static void loginUser() {
        System.out.print("Введите имя пользователя: ");
        String username = inputScanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = inputScanner.nextLine();

        UserAccount user = userAccounts.get(username);
        if (user != null && user.checkPassword(password)) {
            activeUser = user;
            System.out.println("Здравствуйте, " + activeUser.getName() + "!");
            manageFinances();
        } else {
            System.out.println("Неверные учетные данные.");
        }
    }

    private static void createAccount() {
        System.out.print("Введите новый логин: ");
        String username = inputScanner.nextLine();
        if (userAccounts.containsKey(username)) {
            System.out.println("Пользователь уже существует.");
            return;
        }
        System.out.print("Введите пароль: ");
        String password = inputScanner.nextLine();
        userAccounts.put(username, new UserAccount(username, password));
        System.out.println("Аккаунт успешно создан.");
    }

    private static void manageFinances() {
        while (true) {
            System.out.println("\n1. Добавить доход\n2. Добавить расход\n3. Установить лимит\n4. Просмотреть отчет\n5. Назад");
            String choice = inputScanner.nextLine();
            switch (choice) {
                case "1" -> recordIncome();
                case "2" -> recordExpense();
                case "3" -> setSpendingLimit();
                case "4" -> displayReport();
                case "5" -> {
                    activeUser = null;
                    return;
                }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private static void recordIncome() {
        System.out.print("Введите категорию дохода: ");
        String category = inputScanner.nextLine();
        System.out.print("Введите сумму: ");
        double amount = Double.parseDouble(inputScanner.nextLine());
        activeUser.addIncome(category, amount);
        System.out.println("Доход добавлен.");
    }

    private static void recordExpense() {
        System.out.print("Введите категорию расхода: ");
        String category = inputScanner.nextLine();
        System.out.print("Введите сумму: ");
        double amount = Double.parseDouble(inputScanner.nextLine());
        if (activeUser.addExpense(category, amount)) {
            System.out.println("Расход записан.");
        } else {
            System.out.println("Недостаточно средств для этой операции.");
        }
    }

    private static void setSpendingLimit() {
        System.out.print("Категория: ");
        String category = inputScanner.nextLine();
        System.out.print("Введите лимит: ");
        double limit = Double.parseDouble(inputScanner.nextLine());
        activeUser.setLimit(category, limit);
        System.out.println("Лимит установлен.");
    }

    private static void displayReport() {
        activeUser.showReport();
    }

    private static void saveUserAccounts() {
        try (ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            outStream.writeObject(userAccounts);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения данных.");
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadUserAccounts() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(file))) {
                Map<String, UserAccount> accounts = (Map<String, UserAccount>) inStream.readObject();
                userAccounts.putAll(accounts);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Ошибка загрузки данных.");
            }
        }
    }
}

class UserAccount implements Serializable {
    private final String name;
    private final String password;
    private final Map<String, Double> income = new HashMap<>();
    private final Map<String, Double> expenses = new HashMap<>();
    private final Map<String, Double> limits = new HashMap<>();

    public UserAccount(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public boolean checkPassword(String inputPassword) {
        return password.equals(inputPassword);
    }

    public void addIncome(String category, double amount) {
        income.merge(category, amount, Double::sum);
    }

    public boolean addExpense(String category, double amount) {
        if (income.values().stream().mapToDouble(Double::doubleValue).sum() >= amount) {
            expenses.merge(category, amount, Double::sum);
            return true;
        }
        return false;
    }

    public void setLimit(String category, double limit) {
        limits.put(category, limit);
    }

    public void showReport() {
        System.out.println("\nДоходы:");
        income.forEach((cat, amt) -> System.out.println(cat + ": " + amt));
        System.out.println("\nРасходы:");
        expenses.forEach((cat, amt) -> System.out.println(cat + ": " + amt));
        System.out.println("\nЛимиты:");
        limits.forEach((cat, lim) -> System.out.println(cat + ": " + lim));
    }
}

