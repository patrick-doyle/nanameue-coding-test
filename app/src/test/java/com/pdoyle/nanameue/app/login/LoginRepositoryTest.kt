package com.pdoyle.nanameue.app.login

import com.google.common.truth.Truth
import com.pdoyle.nanameue.app.users.User
import com.pdoyle.nanameue.app.users.UsersRepository
import com.pdoyle.nanameue.test.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class LoginRepositoryTest {

    private val loginApi = mockk<LoginApi>(relaxed = true)
    private val usersRepository = mockk<UsersRepository>(relaxed = true)

    private lateinit var repository: LoginRepository

    private val testUser = TestData.createUser()
    private val testAuthResultSuccess = AuthResult.Success(testUser)
    private val testAuthResultFail = AuthResult.Error<User>(LoginError.InvalidCredentials())
    private val testEmail = "test@example.com"
    private val testPassword = "password"

    @BeforeEach
    fun setUp() {
        repository = LoginRepository(loginApi, usersRepository)
    }

    @Test
    fun login() = runTest {
        //GIVEN
        coEvery { repository.login(testEmail, testPassword) } returns testAuthResultSuccess

        //WHEN
        val returnedData = repository.login(testEmail, testPassword)

        //THEN
        coVerify { repository.login(testEmail, testPassword) }
        Truth.assertThat(returnedData).isEqualTo(testAuthResultSuccess)
    }

    @Test
    fun isLoggedIn() {
        //GIVEN
        coEvery { repository.isLoggedIn() } returns true

        //WHEN
        val returnedData = repository.isLoggedIn()

        //THEN
        coVerify { repository.isLoggedIn() }
        Truth.assertThat(returnedData).isTrue()
    }

    @Test
    fun currentUser() {
        //GIVEN
        coEvery { repository.currentUser() } returns testUser

        //WHEN
        val returnedData = repository.currentUser()

        //THEN
        coVerify { repository.currentUser() }
        Truth.assertThat(returnedData).isEqualTo(testUser)
    }

    @Test
    fun signupSuccess() = runTest {
        //GIVEN
        coEvery { repository.signup(testEmail, testPassword) } returns testAuthResultSuccess

        //WHEN
        val returnedData = repository.signup(testEmail, testPassword)

        //THEN
        coVerify { usersRepository.createUserInStore(testAuthResultSuccess.data) }
        Truth.assertThat(returnedData).isEqualTo(testAuthResultSuccess)
    }

    @Test
    fun signupError() = runTest {
        //GIVEN
        coEvery { repository.signup(testEmail, testPassword) } returns testAuthResultFail

        //WHEN
        val returnedData = repository.signup(testEmail, testPassword)

        //THEN
        coVerify(exactly = 0) { usersRepository.createUserInStore(testAuthResultSuccess.data) }
        Truth.assertThat(returnedData).isEqualTo(testAuthResultFail)
    }

    @ParameterizedTest
    @MethodSource("validateEmailParams")
    fun validateEmail(testEmail: String, expectedResult: Boolean) = runTest {
        //WHEN
        val returnedData = repository.validateEmail(testEmail)

        //THEN
        Truth.assertThat(returnedData).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @MethodSource("validatePasswordParams")
    fun validatePassword(testPassword: String, expectedResult: Boolean) = runTest {
        //WHEN
        val returnedData = repository.validatePassword(testPassword)

        //THEN
        Truth.assertThat(returnedData).isEqualTo(expectedResult)
    }

    companion object {
        @JvmStatic
        fun validateEmailParams() = listOf(
            Arguments.of("test@example.com", true),
            Arguments.of("malformedemail.at.example.com", false),
            Arguments.of("where is the bathroom", false),
            Arguments.of("", false),
        )
        @JvmStatic
        fun validatePasswordParams() = listOf(
            Arguments.of("password123", true),
            Arguments.of("", false),
        )
    }


}