package aodintsov.to_do_list.domain.usecase.user

import aodintsov.to_do_list.domain.repository.UserRepository
import javax.inject.Inject


class UpdateLastLoginTimeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(userId: String, currentTime: Long, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        userRepository.updateLastLoginTime(userId, currentTime, onSuccess, onFailure)
    }
}
