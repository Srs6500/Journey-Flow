package com.example.travelpractice.data

import java.util.Date

// Packing List Models
data class PackingCategory(
    val id: String = "",
    val name: String = "",
    val color: String = "#FF6200EE", // Default Material primary color
    val userId: String = "",
    val createdAt: Date = Date(),
    val isDefault: Boolean = false
)

data class PackingItem(
    val id: String = "",
    val name: String = "",
    val categoryId: String = "",
    val isChecked: Boolean = false,
    val userId: String = "",
    val createdAt: Date = Date(),
    val isDefault: Boolean = false
)

// Travel Tasks Models
data class TravelTask(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: TaskCategory = TaskCategory.OTHER,
    val dueDate: Date? = null,
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val userId: String = "",
    val createdAt: Date = Date(),
    val completedAt: Date? = null
)

enum class TaskCategory(val displayName: String) {
    PASSPORT("Passport & Documents"),
    TRANSPORTATION("Transportation"),
    ACCOMMODATION("Accommodation"),
    INSURANCE("Insurance"),
    HEALTH("Health & Safety"),
    MONEY("Money & Banking"),
    COMMUNICATION("Communication"),
    PACKING("Packing"),
    OTHER("Other")
}

enum class TaskPriority(val displayName: String, val value: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4)
}

// Expense Tracking Models
data class Expense(
    val id: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val date: Date = Date(),
    val userId: String = "",
    val createdAt: Date = Date()
)

enum class ExpenseCategory(val displayName: String, val icon: String) {
    FOOD("Food & Dining", "🍽️"),
    TRANSPORT("Transportation", "🚗"),
    ACCOMMODATION("Accommodation", "🏨"),
    ACTIVITIES("Activities & Entertainment", "🎯"),
    SHOPPING("Shopping", "🛍️"),
    HEALTH("Health & Medical", "🏥"),
    COMMUNICATION("Communication", "📱"),
    OTHER("Other", "📝")
}

data class Budget(
    val id: String = "",
    val totalBudget: Double = 0.0,
    val dailyBudget: Double = 0.0,
    val spentAmount: Double = 0.0,
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val userId: String = "",
    val createdAt: Date = Date()
)

data class BudgetAlert(
    val id: String = "",
    val budgetId: String = "",
    val threshold: Double = 0.0, // Percentage of budget spent
    val isTriggered: Boolean = false,
    val message: String = "",
    val userId: String = "",
    val createdAt: Date = Date()
)
