package aodintsov.to_do_list.domain.usecase.user

import aodintsov.to_do_list.data.model.User
import aodintsov.to_do_list.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(userId: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            userRepository.getUser(userId, onSuccess, { exception ->
                onFailure(exception)
            })
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
