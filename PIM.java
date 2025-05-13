//20220459_20160219

//usecase#5
public class ReminderService {
private ReminderRepository reminderRepo;
private NotificationService notificationService;

public boolean setReminder(String userId,
String title,
LocalDate date,
LocalTime time) {
// Validation (per SRS)
if (date.isBefore(LocalDate.now())) {
throw new IllegalArgumentException("Date must be in the future");
}

Reminder reminder = new Reminder(userId, title, date, time);
reminderRepo.save(reminder);

// Schedule notification
notificationService.schedule(
userId,
"Reminder: " + title,
date.atTime(time)
);

return true;
}
}

//usecase#6
public class SavingsGoalService {
private SavingsGoalRepository goalRepo;
private DashboardService dashboard;

public boolean createSavingsGoal(String userId,
String goalName,
BigDecimal targetAmount,
BigDecimal currentAmount) {
// Validation
if (targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
throw new IllegalArgumentException("Target amount must be > 0");
}

SavingsGoal goal = new SavingsGoal(userId, goalName, targetAmount, currentAmount);
goalRepo.save(goal);

// Update dashboard
dashboard.updateSavingsProgress(userId, goal.getId());

return true;
}
}
//usecase#7
public class ExpenseService {
private ExpenseRepository expenseRepo;
private BudgetService budgetService;

public boolean recordExpense(String userId,
String category,
BigDecimal amount,
LocalDate date) {
// Validation
if (amount.compareTo(BigDecimal.ZERO) <= 0) {
throw new IllegalArgumentException("Amount must be positive");
}

Expense expense = new Expense(userId, category, amount, date);
expenseRepo.save(expense);

// Update budget
budgetService.updateBudget(userId, amount, category);

return true;
}
}
//usecase#8
public class DebtService {
private DebtRepository debtRepo;
private DashboardService dashboard;

public boolean addDebt(String userId,
String debtName,
BigDecimal totalAmount,
BigDecimal remainingAmount) {

if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
throw new IllegalArgumentException("Total debt must be > 0");
}

Debt debt = new Debt(userId, debtName, totalAmount, remainingAmount);
debtRepo.save(debt);

// Update dashboard
dashboard.updateDebtTracker(userId, debt.getId());

return true;
}

public void recordPayment(String debtId, BigDecimal paymentAmount) {
Debt debt = debtRepo.findById(debtId);
debt.setRemainingAmount(debt.getRemainingAmount().subtract(paymentAmount));
debtRepo.save(debt);
}
}
//usecase#9
public class TransactionService {
private TransactionRepository transactionRepo;
private SchedulerService scheduler;

public boolean scheduleRecurringTransaction(String userId,
TransactionType type,
String category,
BigDecimal amount,
LocalDate startDate) {
// Validation (per SRS Data Dictionary)
if (amount.compareTo(BigDecimal.ZERO) <= 0) {
throw new IllegalArgumentException("Amount must be > 0");
}
if (startDate.isBefore(LocalDate.now())) {
throw new IllegalArgumentException("Start date must be in the future");
}

// Create and save transaction
RecurringTransaction transaction = new RecurringTransaction(
userId, type, category, amount, startDate);

transactionRepo.save(transaction);

// Schedule future executions
scheduler.schedule(transaction.getId(),
new TransactionJob(transaction));

return true;
}
}
//usecase#10
public class ReportService {
private Database db;
private AIAnalyticsService ai;

public Report generateFinancialReport(String userId) {

List<Transaction> transactions = db.getTransactions(userId);
List<Debt> debts = db.getDebts(userId);
List<Saving> savings = db.getSavings(userId);


if (transactions.isEmpty() && debts.isEmpty() && savings.isEmpty()) {
throw new NoDataException("No financial data available");
}


Insights insights = ai.analyzeData(transactions, debts, savings);


return new Report(transactions, debts, savings, insights);
}
}
