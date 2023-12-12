package edu.team08.infinitegallery.singlephoto;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FaceOnLiveService {
    @Multipart
    @POST("j2y3q25y1b6maif1/api/iddoc")
    Call<ResponseBody> postImage(@Header("X-BLOBR-KEY") String key, @Part MultipartBody.Part image);

}
