package aodintsov.to_do_list.domain.usecase.user

import aodintsov.to_do_list.data.model.User
import aodintsov.to_do_list.domain.repository.UserRepository
import javax.inject.Inject


class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        userRepository.updateUser(user, onSuccess, onFailure)
    }
}
