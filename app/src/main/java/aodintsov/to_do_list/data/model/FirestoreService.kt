package aodintsov.to_do_list.data.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import kotlinx.coroutines.tasks.await

class FirestoreService {

    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance().apply {
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
            .build()
        firestoreSettings = settings
    }

    suspend fun addTask(task: Task) {
        firestore.collection("tasks")
            .document(task.taskId)
            .set(task)
            .await()
    }

    suspend fun updateTask(task: Task) {
        firestore.collection("tasks")
            .document(task.taskId)
            .set(task)
            .await()
    }

    suspend fun deleteTask(taskId: String) {
        firestore.collection("tasks")
            .document(taskId)
            .delete()
            .await()
    }

    suspend fun getTasks(userId: String): List<Task> {
        return firestore.collection("tasks")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(Task::class.java)
    }

    suspend fun updateSubTask(taskId: String, subTask: SubTask) {
        val document = firestore.collection("tasks")
            .document(taskId)
            .get()
            .await()

        val task = document.toObject(Task::class.java) ?: throw Exception("Task not found")
        val updatedSubTasks = task.subTasks.toMutableList()
        val index = updatedSubTasks.indexOfFirst { it.subTaskId == subTask.subTaskId }

        if (index != -1) {
            updatedSubTasks[index] = subTask
        } else {
            updatedSubTasks.add(subTask)
        }

        task.subTasks = updatedSubTasks
        firestore.collection("tasks").document(taskId).set(task).await()
    }
}
