package aodintsov.to_do_list.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class TaskRepositoryImpl(private val firestore: FirebaseFirestore) : TaskRepository {

    override fun getTasks(userId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("tasks")
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
        firestore.collection("tasks")
            .add(task)
            .addOnSuccessListener { documentReference ->
                task.taskId = documentReference.id
                Log.d("TaskRepositoryImpl", "Task added with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("TaskRepositoryImpl", "Error adding task", exception)
                onFailure(exception)
            }
    }

    override fun updateTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("tasks").document(task.taskId)
            .set(task)
            .addOnSuccessListener {
                Log.d("TaskRepositoryImpl", "Task updated with ID: ${task.taskId}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("TaskRepositoryImpl", "Error updating task", exception)
                onFailure(exception)
            }
    }

    override fun deleteTask(taskId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("tasks").document(taskId)
            .delete()
            .addOnSuccessListener {
                Log.d("TaskRepositoryImpl", "Task deleted with ID: $taskId")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("TaskRepositoryImpl", "Error deleting task", exception)
                onFailure(exception)
            }
    }

    override fun deleteAllTasks(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        try {
            val collection = firestore.collection("tasks")
            collection.get().addOnSuccessListener { snapshot ->
                val batch = firestore.batch()
                for (doc in snapshot.documents) {
                    batch.delete(doc.reference)
                }
                batch.commit().addOnSuccessListener {
                    Log.d("TaskRepositoryImpl", "All tasks deleted")
                    onSuccess()
                }.addOnFailureListener { exception ->
                    Log.e("TaskRepositoryImpl", "Error committing batch deletion", exception)
                    onFailure(exception)
                }
            }.addOnFailureListener { exception ->
                Log.e("TaskRepositoryImpl", "Error fetching tasks for deletion", exception)
                onFailure(exception)
            }
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Exception in deleteAllTasks", e)
            onFailure(e)
        }
    }
}
