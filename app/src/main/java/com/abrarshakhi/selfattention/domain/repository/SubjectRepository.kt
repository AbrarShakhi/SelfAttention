package com.abrarshakhi.selfattention.domain.repository

import com.abrarshakhi.selfattention.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getSubjects(): Flow<List<Subject>>
    suspend fun getSubjectById(id: Long): Subject?
    suspend fun insertSubject(subject: Subject): Long
    suspend fun deleteSubject(subject: Subject)
}
