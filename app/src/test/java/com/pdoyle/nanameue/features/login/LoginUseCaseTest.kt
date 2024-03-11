package com.pdoyle.nanameue.features.login

import com.google.common.truth.Truth
import com.pdoyle.nanameue.app.login.AuthResult
import com.pdoyle.nanameue.app.login.LoginError
import com.pdoyle.nanameue.app.login.LoginRepository
import com.pdoyle.nanameue.test.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LoginUseCaseTest {

    private val loginRepository: LoginRepository = mockk()

    private lateinit var loginUseCase: LoginUseCase

    @BeforeEach
    fun setUp() {
        loginUseCase = LoginUseCase(loginRepository)
    }

    @Test
    fun isLoggedIn() {
        //GIVEN
        coEvery { loginRepository.isLoggedIn() } returns true
        //WHEN
        val testResult = loginUseCase.isLoggedIn()
        //THEN
        Truth.assertThat(testResult).isTrue()
    }

    @Test
    fun login() = runTest {
        //GIVEN
        val formSubmit = LoginFormSubmit(TestData.EMAIL, TestData.PASSWORD)
        val user = TestData.createUser()
        coEvery {
            loginRepository.login(TestData.EMAIL, TestData.PASSWORD)
        } returns AuthResult.Success(user)

        //WHEN
        val testResult = loginUseCase.login(formSubmit) as AuthResult.Success

        //THEN
        Truth.assertThat(testResult.data).isEqualTo(user)
    }

    @Test
    fun signup()= runTest {
        //GIVEN
        val formSubmit = LoginFormSubmit(TestData.EMAIL, TestData.PASSWORD)
        val user = TestData.createUser()
        coEvery {
            loginRepository.signup(TestData.EMAIL, TestData.PASSWORD)
        } returns AuthResult.Success(user)

        //WHEN
        val testResult = loginUseCase.signup(formSubmit) as AuthResult.Success

        //THEN
        Truth.assertThat(testResult.data).isEqualTo(user)
    }

    @Nested
    inner class LoginRepositoryValidate {

        @Test
        fun invalidEmail() {
            //GIVEN
            val exceptedErrorType = LoginError.MalformedEmail::class.java
            val formSubmit = LoginFormSubmit(TestData.EMAIL, TestData.PASSWORD)
            coEvery { loginRepository.validateEmail(TestData.EMAIL) } returns false

            //WHEN
            val testResult = loginUseCase.validate(formSubmit)

            //THEN
            Truth.assertThat((testResult as AuthResult.Error).error).isInstanceOf(exceptedErrorType)
        }

        @Test
        fun invalidPassword() {
            //GIVEN
            val exceptedErrorType = LoginError.MalformedPassword::class.java
            val formSubmit = LoginFormSubmit(TestData.EMAIL, TestData.PASSWORD)
            coEvery { loginRepository.validateEmail(TestData.EMAIL) } returns true
            coEvery { loginRepository.validatePassword(TestData.PASSWORD) } returns false

            //WHEN
            val testResult = loginUseCase.validate(formSubmit)

            //THEN
            Truth.assertThat((testResult as AuthResult.Error).error).isInstanceOf(exceptedErrorType)
        }

        @Test
        fun invalidEmailAndPassword() {
            //GIVEN
            val exceptedErrorType = LoginError.MalformedEmail::class.java
            val formSubmit = LoginFormSubmit(TestData.EMAIL, TestData.PASSWORD)
            coEvery { loginRepository.validateEmail(TestData.EMAIL) } returns false
            coEvery { loginRepository.validatePassword(TestData.PASSWORD) } returns false

            //WHEN
            val testResult = loginUseCase.validate(formSubmit)

            //THEN
            Truth.assertThat((testResult as AuthResult.Error).error).isInstanceOf(exceptedErrorType)
        }

        @Test
        fun validEmailAndPassword() {
            //GIVEN
            val formSubmit = LoginFormSubmit(TestData.EMAIL, TestData.PASSWORD)
            coEvery { loginRepository.validateEmail(TestData.EMAIL) } returns true
            coEvery { loginRepository.validatePassword(TestData.PASSWORD) } returns true

            //WHEN
            val testResult = loginUseCase.validate(formSubmit)

            //THEN
            Truth.assertThat((testResult as AuthResult.Success<Boolean>).data).isTrue()
        }

    }


}