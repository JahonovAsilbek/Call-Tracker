package uz.asaxiy.calltracker.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import uz.asaxiy.calltracker.domain.dto.CallRequest

interface ApiService {

    @POST("call-record/save-data")
    suspend fun postCall(@Body callRequest: CallRequest): Response<Any>

    @GET("administrator/update-location")
    suspend fun postLocation(
        @Query("user_id") userId: String,
        @Query("lat") lat: Double,
        @Query("lng") long: Double
    ): Response<Any>
}