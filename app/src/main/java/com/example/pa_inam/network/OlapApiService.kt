package com.example.pa_inam.network

import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

// Lo que el ASP.NET recibe
data class MdxRequest(val mdx: String)

// Cada fila del resultado viene como objeto JSON genérico
typealias OlapRow = JsonObject

interface OlapApiService {

    @POST("api/CuboOlap/query")
    suspend fun runMdx(@Body request: MdxRequest): List<OlapRow>



        // EJEMPLO 1: si tu backend espera el MDX en el body como texto plano
    @POST("api/CuboOlap/Gender")
    suspend fun executeMdx(@Body mdx: String): List<OlapRow>

        // O EJEMPLO 2: si espera un JSON { "mdxQuery": "..." }
        // @POST("api/olap/query")
        // suspend fun executeMdx(@Body request: MdxRequest): List<OlapRow>

}