package com.mehmetbaloglu.geminichatbot.di

import com.google.ai.client.generativeai.GenerativeModel
import com.mehmetbaloglu.geminichatbot.repository.Repository
import com.mehmetbaloglu.geminichatbot.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-pro",
            apiKey = Constants.API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideRepository(generativeModel: GenerativeModel): Repository {
        return Repository(generativeModel)
    }


}