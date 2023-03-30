package uz.asaxiy.calltracker.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import uz.asaxiy.calltracker.domain.dto.CallRequest

interface ApiService {

    @POST("call-record/save-data")
    suspend fun postCall(@Body callRequest: CallRequest): Response<Any>
}