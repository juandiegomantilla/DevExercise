package com.example.devexercise.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DatabaseDao {
    @Query("select * from countryentity")
    fun getCountryFromDatabase(): LiveData<List<CountryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCountryToDatabase(vararg countryEntity: CountryEntity)

    @Query("select * from mappointentity where OBJECTID = :pointId")
    fun getMapPointFromDatabase(pointId: Long): LiveData<List<MapPointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMapPointToDatabase(vararg mapPointEntity: MapPointEntity)
}

@Database(entities = [CountryEntity::class, MapPointEntity::class], version = 1)
abstract class LocalDatabase: RoomDatabase(){
    abstract val databaseDao: DatabaseDao
}

private lateinit var INSTANCE: LocalDatabase

fun getDatabase(context: Context): LocalDatabase{
    synchronized(LocalDatabase::class.java){
        if(!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext, LocalDatabase::class.java, "countries").build()
        }
    }
    return INSTANCE
}




