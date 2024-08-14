package aodintsov.to_do_list.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class TaskRepositoryImpl(private val firestoreService: FirestoreService) : TaskRepository {

    override fun getTasks(userId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        firestoreService.firestore.collection("tasks")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val tasks = result.map { document -> document.toObject<Task>().apply { taskId = document.id } }
                onSuccess(tasks)
                Log.d("TaskRepositoryImpl", "Tasks fetched successfully: $tasks")

            }
            .addOnFailureListener { exception ->
                Log.e("TaskRepositoryImpl", "Error fetching tasks", exception)
                onFailure(exception)
            }
    }

    override fun addTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestoreService.firestore.collection("tasks")
            .add(task)
            .addOnSuccessListener { documentReference ->
                task.taskId = documentReference.id
//                Log.d("TaskRepositoryImpl", "Task added with ID: ${documentReference.id}")
//                Log.d("TaskRepositoryImpl", "Adding task with createdAt: ${task.createdAt}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
          //      Log.e("TaskRepositoryImpl", "Error adding task", exception)
                onFailure(exception)
            }
    }

    override fun updateTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestoreService.firestore.collection("tasks").document(task.taskId)
            .set(task)
            .addOnSuccessListener {
            //    Log.d("TaskRepositoryImpl", "Task updated with ID: ${task.taskId}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
             //   Log.e("TaskRepositoryImpl", "Error updating task", exception)
                onFailure(exception)
            }
    }

    override fun deleteTask(taskId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestoreService.firestore.collection("tasks").document(taskId)
            .delete()
            .addOnSuccessListener {
            //    Log.d("TaskRepositoryImpl", "Task deleted with ID: $taskId")
                onSuccess()
            }
            .addOnFailureListener { exception ->
           //     Log.e("TaskRepositoryImpl", "Error deleting task", exception)
                onFailure(exception)
            }
    }



    override fun getAssignedTasks(userId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        firestoreService.firestore.collection("tasks")
            .whereEqualTo("assignedTo", userId)
            .get()
            .addOnSuccessListener { result ->
                val tasks = result.map { document -> document.toObject<Task>().apply { taskId = document.id } }
                onSuccess(tasks)
              //  Log.d("TaskRepositoryImpl", "Assigned tasks fetched successfully: $tasks")
            }
            .addOnFailureListener { exception ->
             //   Log.e("TaskRepositoryImpl", "Error fetching assigned tasks", exception)
                onFailure(exception)
            }
    }

    override fun updateSubTask(taskId: String, subTask: SubTask, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestoreService.firestore.collection("tasks")
            .document(taskId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val task = document.toObject(Task::class.java)
                    if (task != null) {
                        val updatedSubTasks = task.subTasks.toMutableList()
                        val index = updatedSubTasks.indexOfFirst { it.subTaskId == subTask.subTaskId }
                        if (index != -1) {
                            updatedSubTasks[index] = subTask
                        } else {
                            updatedSubTasks.add(subTask)
                        }
                        task.subTasks = updatedSubTasks
                        firestoreService.firestore.collection("tasks")
                            .document(taskId)
                            .set(task)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { exception ->
                                onFailure(exception)
                            }
                    } else {
                        onFailure(Exception("Task not found"))
                    }
                } else {
                    onFailure(Exception("Task not found"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    override fun assignTaskToUser(taskId: String, assignedTo: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestoreService.firestore.collection("tasks")
            .document(taskId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val task = document.toObject(Task::class.java)
                    if (task != null) {
                        task.assignedTo = assignedTo
                        task.delegatedBy = task.userId
                        firestoreService.firestore.collection("tasks")
                            .document(taskId)
                            .set(task)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { exception ->
                                onFailure(exception)
                            }
                    } else {
                        onFailure(Exception("Task not found"))
                    }
                } else {
                    onFailure(Exception("Task not found"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    override fun getDeferredTasks(
        userId: String,
        currentTime: Long,
        onSuccess: (List<Task>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestoreService.firestore.collection("tasks")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isDeferred", true)
            .whereLessThanOrEqualTo("activationTime", currentTime)
            .get()
            .addOnSuccessListener { result ->
                val tasks = result.map { document -> document.toObject<Task>().apply { taskId = document.id } }
                onSuccess(tasks)
                Log.d("TaskRepositoryImpl", "Deferred tasks fetched successfully: $tasks")
            }
            .addOnFailureListener { exception ->
                Log.e("TaskRepositoryImpl", "Error fetching deferred tasks", exception)
                onFailure(exception)
            }
    }

    override fun activateDeferredTask(
        taskId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestoreService.firestore.collection("tasks")
            .document(taskId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val task = document.toObject(Task::class.java)
                    if (task != null) {
                        task.isDeferred = false
                        task.activationTime = null
                        firestoreService.firestore.collection("tasks")
                            .document(taskId)
                            .set(task)
                            .addOnSuccessListener {
                                onSuccess()
                                Log.d("TaskRepositoryImpl", "Deferred task activated: $taskId")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("TaskRepositoryImpl", "Error activating deferred task", exception)
                                onFailure(exception)
                            }
                    } else {
                        onFailure(Exception("Task not found"))
                    }
                } else {
                    onFailure(Exception("Task not found"))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TaskRepositoryImpl", "Error retrieving task for activation", exception)
                onFailure(exception)
            }
    }

}
