package com.example.coroutinesinkotlin

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiServices{

    @GET("/api/6976624839033210/search/{name}")
    suspend fun getSuperheroes(@Path("name") superheroName:String): Response<SuperHeroDataResponse>
}
//la clase para obtener el resultado. Solo necesito lo que recibo.
//No es necesario definir todas las columnas
data class SuperHeroDataResponse(
    //el campo se llama response
    @SerializedName("response") val response:String
)
