import java.io.*;
import java.util.*;

public class User implements Serializable {
    private final String username;
    private final String password;
    private final Map<String, Double> income = new HashMap<>();
    private final Map<String, Double> expenses = new HashMap<>();
    private final Map<String, Double> budgets = new HashMap<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean validatePassword(String inputPassword) {
        return password.equals(inputPassword);
    }

    public void addIncome(String category, double amount) {
        income.put(category, income.getOrDefault(category, 0.0) + amount);
    }

    public boolean addExpense(String category, double amount) {
        double totalIncome = income.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalExpenses = expenses.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalIncome - totalExpenses >= amount) {
            expenses.put(category, expenses.getOrDefault(category, 0.0) + amount);
            return true;
        }
        return false;
    }

    public void setBudget(String category, double budget) {
        budgets.put(category, budget);
    }

    public void showStatistics() {
        System.out.println("\nСтатистика:");
        double totalIncome = income.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalExpenses = expenses.values().stream().mapToDouble(Double::doubleValue).sum();
        System.out.println("Общий доход: " + totalIncome);
        System.out.println("Общие расходы: " + totalExpenses);

        System.out.println("\nБюджет по категориям:");
        for (String category : budgets.keySet()) {
            double budget = budgets.get(category);
            double spent = expenses.getOrDefault(category, 0.0);
            System.out.printf("Категория: %s, Бюджет: %.2f, Потрачено: %.2f, Остаток: %.2f\n",
                    category, budget, spent, budget - spent);
        }
    }
}
