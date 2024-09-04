package aodintsov.to_do_list.data.repositoryimpl

import aodintsov.to_do_list.data.model.FirestoreService
import aodintsov.to_do_list.data.model.User
import aodintsov.to_do_list.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor (firestore: FirestoreService) : UserRepository {
    private val usersCollection = FirebaseFirestore.getInstance().collection("users")

    override fun getUser(userId: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                user?.let { onSuccess(it) } ?: onFailure(Exception("User not found"))
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    override fun addUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(user.userId).set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    task.exception?.let { onFailure(it) }
                }
            }
    }

    override fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(user.userId).set(user, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    override fun deleteUser(userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(userId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    override fun updateUserPoints(userId: String, points: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(userId).update("points", points)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    override fun updateLastLoginTime(userId: String, lastLoginTime: Long, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(userId).update("lastLoginTime", lastLoginTime)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    override fun getCompletedTaskCount(userId: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        // Этот метод может вызываться из TaskRepository, если данные хранятся там
    }
}
