package com.sofascoreacademy.minisofascore.data.repository

import com.sofascoreacademy.minisofascore.data.remote.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline fun <ResultType, InterType, RequestType> repoResource(
    crossinline load: () -> Flow<ResultType>,
    crossinline shouldFetch: (ResultType?) -> Boolean = { true },
    crossinline fetch: suspend () -> Result<RequestType>,
    crossinline process: (RequestType) -> InterType,
    crossinline save: suspend (InterType) -> Unit
) = flow {
    emit(Resource.Loading())
    val dbValue = load().firstOrNull()
    if (shouldFetch(dbValue)) {
        emit(Resource.Loading(dbValue))
        when (val apiResponse = fetch()) {
            is Result.Success -> {
                save(process(apiResponse.data))

                // Emits everything from db, so further db updates
                // will be emitted via this flow auto-magically.
                // As out db is the only source of truth, it is fine
                // even if other clients re-request this from network.
                // IMPORTANT: Filter is needed as Room is written in Java
                // and can still emit nulls when table is empty :(
                emitAll(load().filter { it != null }.map { Resource.Success(it) })
            }
            // We do not emit anything else from database, so
            // client code can react on error and if needed -- request
            // this resource one more time from the repo.
            is Result.Error -> {
                emit(Resource.Failure(apiResponse.error))
            }
        }
    } else {

        emitAll(load().filter { it != null }.map { Resource.Success(it) })
    }
}