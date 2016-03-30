package com.gregorbrett;

import com.github.sarxos.webcam.Webcam;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gbrett on 29/03/2016.
 */
public class HoneyPot {

    private SlackApi slackApi;
    private Call<SlackApi.UploadFileResponse> call;
    private RequestBody file;

    public void takePic(){

        Date d = new Date();

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(640, 480));
        webcam.open();
        try {
            ImageIO.write(webcam.getImage(), "PNG", new File("./Capture" + d.getTime() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        webcam.close();
    }

    public void lockPC() throws IOException {
        Runtime.getRuntime().exec("C:\\Windows\\System32\\rundll32.exe user32.dll,LockWorkStation");
        System.exit(0);
    }

    public void uploadPicToSlack(){


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override public void log(String message) {
                System.out.println(message);
            }
        });

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();

        slackApi = new Retrofit.Builder().baseUrl("https://slack.com/").client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(SlackApi.class);

        String str = "Testy McTestString";
        file = RequestBody.create(MediaType.parse("multipart/form-data"), str.getBytes());

        Map<String, RequestBody> map = new HashMap<>();
        map.put("file\"; filename=\"heapDump.md\"", file);
        call = slackApi.uploadFile(SlackApi.TOKEN, map, "png", "pic.png", "Alert", "alert alert alert", SlackApi.MEMORY_LEAK_CHANNEL);

        call.clone().enqueue(new Callback<SlackApi.UploadFileResponse>() {
            @Override
            public void onResponse(Call<SlackApi.UploadFileResponse> call, Response<SlackApi.UploadFileResponse> response) {
                if (response != null) {
                    System.out.println(response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<SlackApi.UploadFileResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public static void main(String[] args) throws IOException, InterruptedException {

        HoneyPot hp = new HoneyPot();

        Thread.sleep(5000);

        System.out.println("Arming HoneyPot! any mouse movement will lock the PC");

        Point firstPoint = MouseInfo.getPointerInfo().getLocation();
        double diff = 0.0;

        while(true){
            diff = firstPoint.getX() - MouseInfo.getPointerInfo().getLocation().getX();

            if(Math.abs(diff) > 0){
                System.out.println("ah ah ah, you didn't say the magic word!");
                hp.takePic();
                hp.uploadPicToSlack();
                Runtime.getRuntime().exec("C:\\Windows\\System32\\rundll32.exe user32.dll,LockWorkStation");
                System.exit(0);
            }
            Thread.sleep(1000);
        }
    }
}
