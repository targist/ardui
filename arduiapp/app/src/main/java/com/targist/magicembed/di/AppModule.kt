package com.targist.magicembed.di

import android.content.Context
import android.hardware.usb.UsbManager
import androidx.room.Room
import com.targist.magicembed.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "projects"
        ).fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideUsbManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.USB_SERVICE) as UsbManager

}

