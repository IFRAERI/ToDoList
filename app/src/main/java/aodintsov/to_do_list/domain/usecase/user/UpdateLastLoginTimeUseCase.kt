package aodintsov.to_do_list.domain.usecase.user

import aodintsov.to_do_list.data.model.User
import aodintsov.to_do_list.domain.repository.UserRepository
import jakarta.inject.Inject

class UpdateLastLoginTimeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(user: User) {
        user.lastLoginTime = System.currentTimeMillis()
        userRepository.updateUser(user, {
            // Successfully updated last login time
        }, {
            // Handle update failure
        })
    }
}
