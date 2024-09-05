package aodintsov.to_do_list.domain.usecase.user

import aodintsov.to_do_list.domain.repository.UserRepository
import javax.inject.Inject


class UpdateUserPointsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(userId: String, points: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        userRepository.updateUserPoints(userId, points, onSuccess, onFailure)
    }
}
