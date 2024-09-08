package aodintsov.to_do_list.di

import android.content.Context
import android.util.Log
import aodintsov.to_do_list.R
import aodintsov.to_do_list.data.api.OpenAIRepository
import aodintsov.to_do_list.data.api.OpenAIService
import aodintsov.to_do_list.data.api.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import aodintsov.to_do_list.data.model.FirestoreService
import aodintsov.to_do_list.data.repositoryimpl.OpenAIRepositoryImpl
import aodintsov.to_do_list.data.repositoryimpl.TaskRepositoryImpl
import aodintsov.to_do_list.data.repositoryimpl.UserRepositoryImpl
import aodintsov.to_do_list.domain.repository.TaskRepository
import aodintsov.to_do_list.domain.repository.UserRepository
import aodintsov.to_do_list.presentation.viewmodel.AuthViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import aodintsov.to_do_list.domain.usecase.user.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

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

    // Добавляем OpenAIService и OpenAIRepository
    @Provides
    @Singleton
    fun provideOpenAIService(@ApplicationContext context: Context): OpenAIService {
        val apiKey = context.getString(R.string.openai_api_key) // Здесь теперь используется корректный Context
        Log.d("HiltLog", "OpenAIService instance created with API key: $apiKey")
        return RetrofitInstance.createOpenAIService(apiKey)
    }

    @Provides
    @Singleton
    fun provideOpenAIRepository(openAIService: OpenAIService): OpenAIRepository {
        Log.d("HiltLog", "OpenAIRepository instance created")
        return OpenAIRepositoryImpl(openAIService, "")
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
        createUserInFirestoreUseCase: CreateUserInFirestoreUseCase
    ): RegisterUserUseCase {
        Log.d("HiltLog", "RegisterUserUseCase instance created")
        return RegisterUserUseCase(firebaseAuth, createUserInFirestoreUseCase)
    }

}
