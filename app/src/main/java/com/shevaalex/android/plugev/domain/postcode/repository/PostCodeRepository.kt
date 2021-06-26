package com.shevaalex.android.plugev.domain.postcode.repository

import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import com.shevaalex.android.plugev.domain.postcode.model.PostCode

interface PostCodeRepository {

    suspend fun getPostCodeLocation(postCodeQuery: String): DataResult<PostCode>

}
