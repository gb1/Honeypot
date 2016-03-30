package com.gregorbrett;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

import java.util.Map;

public interface SlackApi {
    String TOKEN = "YOUR SLACK API TOKEN HERE";
    String MEMORY_LEAK_CHANNEL = "#general";


    @Multipart
    @POST("api/files.upload")
    Call<UploadFileResponse> uploadFile(
            @Query("token") String token,
            @PartMap Map<String, RequestBody> params,
            @Query("filetype") String filetype,
            @Query("filename") String filename, @Query("title") String title,
            @Query("initial_comment") String initialComment, @Query("channels") String channels);


    public static class UploadFileResponse {
        boolean ok;
        String error;

        @Override
        public String toString() {
            return "UploadFileResponse{" +
                    "ok=" + ok +
                    ", error='" + error + '\'' +
                    '}';
        }
    }
}