package com.example.devexercise.dagger

import android.app.Application
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.getDatabase
import com.example.devexercise.network.connection.ConnectionLiveData
import com.example.devexercise.util.DateProvider
import com.example.devexercise.util.DateProviderImp
import dagger.Module
import dagger.Provides
import java.time.Clock
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class, ArcgisApiServiceModule::class, LoginModule::class, RepositoryModule::class])
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): LocalDatabase = getDatabase(application)

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create("LOGIN",
            masterKeyAlias,
            application,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    @Provides
    @Singleton
    fun provideConnectionLiveData(application: Application): ConnectionLiveData = ConnectionLiveData(application)

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideDateProvider(): DateProvider {
        return DateProviderImp(Clock.systemDefaultZone())
    }
}