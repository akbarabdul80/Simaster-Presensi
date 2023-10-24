package com.zero.simasterpresensi.converter;

import com.google.gson.GsonBuilder;
import com.zero.simasterpresensi.data.network.HtmlConverterFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import pl.droidsonroids.retrofit2.JspoonConverterFactory;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class HtmlOrJsonConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Html.class) {
//                return new JspoonResponseBodyConverter<>(retrofit.baseUrl(),
//                        jspoon.adapter((Class<?>) type));
            }
            if (annotation.annotationType() == Json.class) {
                return GsonConverterFactory.create(new GsonBuilder().setLenient().excludeFieldsWithoutExposeAnnotation().create()).responseBodyConverter(type, annotations, retrofit);
            }
        }
        return GsonConverterFactory.create(new GsonBuilder().setLenient().excludeFieldsWithoutExposeAnnotation().create()).responseBodyConverter(type, annotations, retrofit);
    }
}