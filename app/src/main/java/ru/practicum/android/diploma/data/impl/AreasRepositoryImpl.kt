package ru.practicum.android.diploma.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.dto.AreasDto
import ru.practicum.android.diploma.data.filters.AreasRequest
import ru.practicum.android.diploma.data.filters.AreasResponse
import ru.practicum.android.diploma.data.network.NetworkClientInterface
import ru.practicum.android.diploma.data.network.NoInternetException
import ru.practicum.android.diploma.data.network.ServerErrorException
import ru.practicum.android.diploma.domain.filters.AreasRepository
import ru.practicum.android.diploma.domain.models.Areas
import ru.practicum.android.diploma.util.HTTP_200_OK
import ru.practicum.android.diploma.util.HTTP_NO_CONNECTION

class AreasRepositoryImpl(
    private val network: NetworkClientInterface
) : AreasRepository {
    private var cacheResponse: AreasResponse? = null

    override fun getAreas(): Flow<Result<List<Areas>>> = flow {
        val response = cacheResponse ?: network.doRequest(AreasRequest())
        when (response.resultCode) {
            HTTP_NO_CONNECTION -> emit(Result.failure(NoInternetException()))
            HTTP_200_OK -> {
                cacheResponse = response as AreasResponse
                cacheResponse?.let {
                    val data = it.areas.map { areasDto ->
                        dtoToModel(areasDto)
                    }
                    emit(Result.success(data))
                }
            }

            else -> emit(Result.failure(ServerErrorException()))
        }
    }

    private fun dtoToModel(dto: AreasDto): Areas {
        return Areas(
            id = dto.id,
            name = dto.name,
            parentId = dto.parentId,
            areas = if (dto.areas.isEmpty()) {
                emptyList()
            } else {
                dto.areas.sortedBy { it.name }.map {
                    dtoToModel(it)
                }
            }
        )
    }
}
