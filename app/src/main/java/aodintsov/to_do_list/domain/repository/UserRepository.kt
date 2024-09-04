package aodintsov.to_do_list.domain.repository

import aodintsov.to_do_list.data.model.User

interface UserRepository {
    fun getUser(userId: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit)
    fun addUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deleteUser(userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    // Метод для обновления очков пользователя
    fun updateUserPoints(userId: String, points: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    // Метод для обновления времени последнего входа
    fun updateLastLoginTime(userId: String, lastLoginTime: Long, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    // Метод для получения количества выполненных задач (если нужно отдельно хранить)
    fun getCompletedTaskCount(userId: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit)
}
