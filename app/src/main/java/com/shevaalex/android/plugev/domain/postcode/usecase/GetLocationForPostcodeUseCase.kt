package com.shevaalex.android.plugev.domain.postcode.usecase

import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import com.shevaalex.android.plugev.domain.postcode.model.PostCode
import com.shevaalex.android.plugev.domain.postcode.repository.PostCodeRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetLocationForPostcodeUseCase
@Inject constructor(
    private val postCodeRepository: PostCodeRepository
) {

    suspend operator fun invoke(postCodeQuery: String): DataResult<PostCode> {
        return postCodeRepository.getPostCodeLocation(postCodeQuery = postCodeQuery)
    }

}
