package com.abrarshakhi.selfattention.data.repository

import com.abrarshakhi.selfattention.data.local.dao.SubjectDao
import com.abrarshakhi.selfattention.data.local.entity.toDomain
import com.abrarshakhi.selfattention.data.local.entity.toEntity
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val dao: SubjectDao,
) : SubjectRepository {

    override fun getSubjects(): Flow<List<Subject>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getSubjectById(id: Long): Subject? =
        dao.getById(id)?.toDomain()

    override suspend fun insertSubject(subject: Subject): Long =
        dao.insert(subject.toEntity())

    override suspend fun deleteSubject(subject: Subject) =
        dao.delete(subject.toEntity())
}
