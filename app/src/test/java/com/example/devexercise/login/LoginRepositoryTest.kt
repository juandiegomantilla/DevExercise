package com.example.devexercise.login

import com.example.devexercise.repository.LoginRepository
import com.example.devexercise.util.mock
import junit.framework.TestCase.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginRepositoryTest {
    private val username = "jmantilla"
    private val pass = "unclesam123"
    private val remember = false

    private val repository:LoginRepository = mock()

    @Test
    fun `The user is not logged in by default`(){
        assertFalse(repository.isLoggedIn)
    }

    @Test
    fun `No user data exists by default`(){
        assertNull(repository.user)
    }

    @Test
    fun `Logout success`(){
        repository.logout()
        assertFalse(repository.isLoggedIn)
        assertNull(repository.user)
    }

    @Test
    fun `Logout should clear data`(){
        repository.login(username, pass, remember)
        repository.logout()
        assertFalse(repository.isLoggedIn)
        assertNull(repository.user)
    }

    @Test
    fun `Is not logged in when login fails`(){
        repository.login("fakeuser", "fakepass", remember)
        assertFalse(repository.isLoggedIn)
        assertNull(repository.user)
    }

    @Test
    fun `Is logged in when login success`(){
        repository.login(username, pass, remember)
        assertFalse(repository.isLoggedIn)
    }

    @Test
    fun `Must not remember the user when option is not selected`(){
        repository.userRemembered(false)
        assertFalse(repository.rememberActive)
    }
}