package uz.asaxiy.calltracker.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import uz.asaxiy.calltracker.data.locale.entity.Call

interface ApiService {

    @POST("info/calls")
    suspend fun postCall(@Body list: List<Call>): Response<Any>
}