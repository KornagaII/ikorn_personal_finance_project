import java.io.*;
import java.util.*;

public class UserAccount implements Serializable {
    private final String name;
    private final String secret;
    private final Map<String, Double> earnings = new HashMap<>();
    private final Map<String, Double> spendings = new HashMap<>();
    private final Map<String, Double> limits = new HashMap<>();

    public UserAccount(String name, String secret) {
        this.name = name;
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public boolean authenticate(String inputSecret) {
        return secret.equals(inputSecret);
    }

    public void recordEarnings(String category, double amount) {
        earnings.merge(category, amount, Double::sum);
    }

    public boolean recordSpending(String category, double amount) {
        double totalEarnings = earnings.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalSpendings = spendings.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalEarnings - totalSpendings >= amount) {
            spendings.merge(category, amount, Double::sum);
            return true;
        }
        return false;
    }

    public void assignLimit(String category, double limit) {
        limits.put(category, limit);
    }

    public void displayReport() {
        System.out.println("\nФинансовый отчет:");
        double totalEarnings = earnings.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalSpendings = spendings.values().stream().mapToDouble(Double::doubleValue).sum();
        System.out.println("Общий доход: " + totalEarnings);
        System.out.println("Общие расходы: " + totalSpendings);

        System.out.println("\nКатегории с лимитами:");
        limits.forEach((category, limit) -> {
            double spent = spendings.getOrDefault(category, 0.0);
            System.out.printf("Категория: %s, Лимит: %.2f, Израсходовано: %.2f, Остаток: %.2f%n",
                    category, limit, spent, limit - spent);
        });
    }
}

