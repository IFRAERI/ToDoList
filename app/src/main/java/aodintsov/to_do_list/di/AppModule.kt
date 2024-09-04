package aodintsov.to_do_list.di

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import aodintsov.to_do_list.data.model.FirestoreService
import aodintsov.to_do_list.data.repositoryimpl.TaskRepositoryImpl
import aodintsov.to_do_list.data.repositoryimpl.UserRepositoryImpl
import aodintsov.to_do_list.domain.repository.TaskRepository
import aodintsov.to_do_list.domain.repository.UserRepository
import aodintsov.to_do_list.presentation.viewmodel.AuthViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import aodintsov.to_do_list.domain.usecase.user.CreateUserInFirestoreUseCase
import aodintsov.to_do_list.domain.usecase.user.LoginUseCase
import aodintsov.to_do_list.domain.usecase.user.RegisterUserUseCase
import aodintsov.to_do_list.domain.usecase.user.SendPasswordResetEmailUseCase
import aodintsov.to_do_list.domain.usecase.user.SignOutUseCase
import aodintsov.to_do_list.domain.usecase.user.UpdateLastLoginTimeUseCase

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        Log.d("HiltLog", "FirebaseAuth instance created")
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestoreService(): FirestoreService {
        Log.d("HiltLog", "FirestoreService instance created")
        return FirestoreService()
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirestoreService): UserRepository {
        Log.d("HiltLog", "UserRepository instance created")
        return UserRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(firestore: FirestoreService): TaskRepository {
        Log.d("HiltLog", "TaskRepository instance created")
        return TaskRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideCreateUserInFirestoreUseCase(userRepository: UserRepository): CreateUserInFirestoreUseCase {
        Log.d("HiltLog", "CreateUserInFirestoreUseCase instance created")
        return CreateUserInFirestoreUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateLastLoginTimeUseCase(userRepository: UserRepository): UpdateLastLoginTimeUseCase {
        Log.d("HiltLog", "UpdateLastLoginTimeUseCase instance created")
        return UpdateLastLoginTimeUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideSendPasswordResetEmailUseCase(firebaseAuth: FirebaseAuth): SendPasswordResetEmailUseCase {
        Log.d("HiltLog", "SendPasswordResetEmailUseCase instance created")
        return SendPasswordResetEmailUseCase(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideAuthViewModelFactory(
        loginUseCase: LoginUseCase,
        registerUserUseCase: RegisterUserUseCase,
        signOutUseCase: SignOutUseCase,
        sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
    ): AuthViewModelFactory {
        Log.d("HiltLog", "AuthViewModelFactory instance created")
        return AuthViewModelFactory(
            loginUseCase,
            registerUserUseCase,
            signOutUseCase,
            sendPasswordResetEmailUseCase
        )
    }

    @Provides
    @Singleton
    fun provideRegisterUserUseCase(
        firebaseAuth: FirebaseAuth,
        userRepository: UserRepository
    ): RegisterUserUseCase {
        Log.d("HiltLog", "RegisterUserUseCase instance created")
        return RegisterUserUseCase(firebaseAuth, userRepository)
    }
}