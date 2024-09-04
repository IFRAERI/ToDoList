package aodintsov.to_do_list.domain.usecase.user

import aodintsov.to_do_list.data.model.User
import aodintsov.to_do_list.domain.repository.UserRepository
import jakarta.inject.Inject

class CreateUserInFirestoreUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(userId: String, email: String, onComplete: (Boolean) -> Unit) {
        val newUser = User(
            userId = userId,
            email = email,
            name = "",
            points = 100,
            lastLoginTime = System.currentTimeMillis(),
            completedTasksCount = 0
        )
        userRepository.addUser(newUser, {
            onComplete(true)
        }, {
            onComplete(false)
        })
    }
}
