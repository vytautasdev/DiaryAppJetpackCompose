package com.vytautasdev.mongo.repository

import com.vytautasdev.util.model.Diary
import com.vytautasdev.util.model.RequestState
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZonedDateTime

typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>

internal interface MongoRepository {
    fun configureTheRealm()
    fun getAllDiaries(): Flow<Diaries>
    fun getFilteredDiaries(zonedDateTime: ZonedDateTime): Flow<Diaries>
    fun getSelectedDiary(diaryId: ObjectId): Flow<RequestState<Diary>>
    suspend fun insertDiary(diary: Diary): RequestState<Diary>
    suspend fun updateDiary(diary: Diary): RequestState<Diary>
    suspend fun deleteDiary(id: ObjectId): RequestState<Boolean>
    suspend fun deleteAllDiaries(): RequestState<Boolean>
}