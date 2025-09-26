package com.example.travelpractice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelpractice.data.TravelTask
import com.example.travelpractice.data.TaskCategory
import com.example.travelpractice.data.TravelTasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class TasksViewModel : ViewModel() {
    private val repository = TravelTasksRepository()
    
    private val _tasks = MutableStateFlow<List<TravelTask>>(emptyList())
    val tasks: StateFlow<List<TravelTask>> = _tasks.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadTasks()
    }
    
    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _tasks.value = repository.getTravelTasks()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addTask(task: TravelTask) {
        viewModelScope.launch {
            try {
                val taskId = repository.addTravelTask(task)
                loadTasks()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun updateTask(task: TravelTask) {
        viewModelScope.launch {
            try {
                repository.updateTravelTask(task)
                loadTasks()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTravelTask(taskId)
                loadTasks()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun toggleTaskCompletion(task: TravelTask) {
        updateTask(task.copy(isCompleted = !task.isCompleted))
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun getTasksByCategory(category: TaskCategory): List<TravelTask> {
        return _tasks.value.filter { it.category == category }
    }
    
    fun getCompletedTasks(): List<TravelTask> {
        return _tasks.value.filter { it.isCompleted }
    }
    
    fun getPendingTasks(): List<TravelTask> {
        return _tasks.value.filter { !it.isCompleted }
    }
    
    fun getOverdueTasks(): List<TravelTask> {
        val now = Date()
        return _tasks.value.filter { 
            !it.isCompleted && it.dueDate != null && it.dueDate.before(now) 
        }
    }
    
    fun getTasksDueToday(): List<TravelTask> {
        val today = Date()
        val startOfDay = java.util.Calendar.getInstance().apply {
            time = today
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.time
        
        val endOfDay = java.util.Calendar.getInstance().apply {
            time = today
            set(java.util.Calendar.HOUR_OF_DAY, 23)
            set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 59)
            set(java.util.Calendar.MILLISECOND, 999)
        }.time
        
        return _tasks.value.filter { 
            !it.isCompleted && it.dueDate != null && 
            it.dueDate.after(startOfDay) && it.dueDate.before(endOfDay)
        }
    }
}
