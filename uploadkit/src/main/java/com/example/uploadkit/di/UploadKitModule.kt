package com.example.uploadkit.di

import android.content.Context
import androidx.room.Room
import com.example.uploadkit.data.db.UploadDao
import com.example.uploadkit.data.db.UploadDatabase
import com.example.uploadkit.domain.UploadRepository
import com.example.uploadkit.domain.usecase.EnqueuePhotosUseCase
import com.example.uploadkit.domain.usecase.ObserveUploadsUseCase
import com.example.uploadkit.domain.usecase.RetryFailedUseCase
import com.example.uploadkit.infra.AndroidFileStore
import com.example.uploadkit.infra.FileStore
import com.example.uploadkit.infra.NetworkMonitor
import com.example.uploadkit.infra.UploadRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UploadKitModule {
    @Provides
    @Singleton
    fun provideUploadDatabase(@ApplicationContext context: Context): UploadDatabase =
        Room.databaseBuilder(context, UploadDatabase::class.java, "uploads.db").build()

    @Provides
    @Singleton
    fun provideUploadDao(db: UploadDatabase): UploadDao = db.uploads()

    @Provides
    @Singleton
    fun provideFileStore(@ApplicationContext context: Context): FileStore = AndroidFileStore(context)

    @Provides
    @Singleton
    fun provideUploadRepository(@ApplicationContext context: Context, fileStore: FileStore): UploadRepository =
        UploadRepositoryImpl(context, fileStore)

    @Provides
    @Singleton
    fun provideEnqueuePhotosUseCase(repo: UploadRepository): EnqueuePhotosUseCase =
        EnqueuePhotosUseCase(repo)

    @Provides
    @Singleton
    fun provideObserveUploadsUseCase(repo: UploadRepository): ObserveUploadsUseCase =
        ObserveUploadsUseCase(repo)

    @Provides
    @Singleton
    fun provideRetryFailedUseCase(repo: UploadRepository): RetryFailedUseCase =
        RetryFailedUseCase(repo)

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor = NetworkMonitor(context)
}
