package com.example.devexercise.viewmodel

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.network.ArcgisApiService
import com.example.devexercise.network.connection.ConnectionLiveData
import com.example.devexercise.repository.CountryRepository
import com.example.devexercise.util.DateProvider
import com.example.devexercise.util.MockDateProvider
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()
    private val testDispatcher = TestCoroutineDispatcher()

    private var database: LocalDatabase = mockk(relaxed = true)
    private var sharedPreferences: SharedPreferences = mockk(relaxed = true)
    private var dateProvider: DateProvider = MockDateProvider()
    private var arcgisApiService: ArcgisApiService = mockk(relaxed = true)
    private var connectionLiveData: ConnectionLiveData = mockk(relaxed = true)
    private var localDirectoryPath = "/data/user/0/com.example.devexercise/app_offlineMode/offlineMap"

    private val countryRepository = spyk(CountryRepository(database, arcgisApiService, sharedPreferences, dateProvider))
    private var homeViewModel: HomeViewModel = HomeViewModel(countryRepository, connectionLiveData, localDirectoryPath)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `get the latest data when cache is expired`() = runBlocking {
        homeViewModel._isOnline.value = true
        every { countryRepository.isCacheExpiredData() } returns true
        val getData = homeViewModel.presentData()
        assertEquals("REFRESHED", getData)
    }

    @Test
    fun `no request data when offline mode is activated`() = runBlocking {
        homeViewModel._isOnline.value = false
        every { countryRepository.isCacheExpiredData() } returns true
        val getData = homeViewModel.presentData()
        assertEquals("NO NETWORK", getData)
    }

    @Test
    fun `no request data when cache is not expired`() = runBlocking {
        homeViewModel._isOnline.value = true
        every { countryRepository.isCacheExpiredData() } returns false
        val getData = homeViewModel.presentData()
        assertEquals("NO REFRESHED", getData)
    }

    @Test
    fun `downloaded areas should be cleared from storage when cache expired`() = runBlocking {
        homeViewModel._isOnline.value = true
        every { countryRepository.isCacheExpiredData() } returns true
        homeViewModel.presentData()

        val directory = File(localDirectoryPath)
        assert(!directory.exists())
    }
}