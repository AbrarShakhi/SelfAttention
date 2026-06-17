package com.abrarshakhi.selfattention.domain.usecase.subject

import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.repository.SubjectRepository
import javax.inject.Inject

class GetSubjectByIdUseCase @Inject constructor(
    private val repository: SubjectRepository,
) {
    suspend operator fun invoke(id: Long): Subject? = repository.getSubjectById(id)
}
