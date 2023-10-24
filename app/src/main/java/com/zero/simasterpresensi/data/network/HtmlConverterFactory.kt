package com.zero.simasterpresensi.data.network

import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class HtmlConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if (type == String::class.java) {
            return Converter<ResponseBody, String> { responseBody ->
                responseBody.string()
            }
        } else if (type == org.jsoup.nodes.Document::class.java) {
            return Converter<ResponseBody, org.jsoup.nodes.Document> { responseBody ->
                Jsoup.parse(responseBody.string())
            }
        }
        return null
    }
}
