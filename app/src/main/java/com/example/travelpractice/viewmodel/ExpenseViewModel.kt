package com.example.travelpractice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelpractice.data.Expense
import com.example.travelpractice.data.ExpenseCategory
import com.example.travelpractice.data.Budget
import com.example.travelpractice.data.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ExpenseViewModel : ViewModel() {
    private val repository = ExpenseRepository()
    
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()
    
    private val _budget = MutableStateFlow<Budget?>(null)
    val budget: StateFlow<Budget?> = _budget.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _expenses.value = repository.getExpenses()
                _budget.value = repository.getBudget()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                val expenseId = repository.addExpense(expense)
                loadData()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repository.updateExpense(expense)
                loadData()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            try {
                repository.deleteExpense(expenseId)
                loadData()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun saveBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                val budgetId = repository.saveBudget(budget)
                loadData()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun getExpensesByCategory(category: ExpenseCategory): List<Expense> {
        return _expenses.value.filter { it.category == category }
    }
    
    fun getExpensesForDate(date: Date): List<Expense> {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        val startOfDay = calendar.apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.time
        
        val endOfDay = calendar.apply {
            set(java.util.Calendar.HOUR_OF_DAY, 23)
            set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 59)
            set(java.util.Calendar.MILLISECOND, 999)
        }.time
        
        return _expenses.value.filter { 
            it.date.after(startOfDay) && it.date.before(endOfDay)
        }
    }
    
    fun getTotalSpent(): Double {
        return _expenses.value.sumOf { it.amount }
    }
    
    fun getTotalSpentForDate(date: Date): Double {
        return getExpensesForDate(date).sumOf { it.amount }
    }
    
    fun getTotalSpentByCategory(category: ExpenseCategory): Double {
        return getExpensesByCategory(category).sumOf { it.amount }
    }
    
    fun getRemainingBudget(): Double {
        val currentBudget = _budget.value ?: return 0.0
        return currentBudget.totalBudget - getTotalSpent()
    }
    
    fun getDailySpendingAverage(): Double {
        val expenses = _expenses.value
        if (expenses.isEmpty()) return 0.0
        
        val firstExpense = expenses.minByOrNull { it.date }?.date ?: return 0.0
        val lastExpense = expenses.maxByOrNull { it.date }?.date ?: return 0.0
        
        val daysBetween = ((lastExpense.time - firstExpense.time) / (1000 * 60 * 60 * 24)) + 1
        return getTotalSpent() / daysBetween
    }
    
    fun getExpenseTrend(): List<Pair<Date, Double>> {
        val expenses = _expenses.value
        val groupedByDate = expenses.groupBy { 
            val calendar = java.util.Calendar.getInstance()
            calendar.time = it.date
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            calendar.time
        }
        
        return groupedByDate.map { (date, expenseList) ->
            date to expenseList.sumOf { it.amount }
        }.sortedBy { it.first }
    }
}
