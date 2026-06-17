package com.abrarshakhi.selfattention.domain.usecase.subject

import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubjectsUseCase @Inject constructor(
    private val repository: SubjectRepository,
) {
    operator fun invoke(): Flow<List<Subject>> = repository.getSubjects()
}
