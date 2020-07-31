package com.example.randhir.guesstheanimecharachter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> charachterURLs = new ArrayList<String>();

    ArrayList<String> charachterNames = new ArrayList<String>();

    String[] answers = new String[4];

    int locationOfCorrectAnswer = 0;

    ImageView imageView;

    int chosenChar = 0 ;

    Button button1;
    Button button2;
    Button button3;
    Button button4;


    public void charChosen(View view){

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(this,"CORRECT !",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"WRONG! It was "+ charachterNames.get(chosenChar),Toast.LENGTH_SHORT).show();
        }
            newQuestion();
    }


    public  class  ImageDownloader extends  AsyncTask<String , Void , Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try{

                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String , Void , String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1){

                    char current = (char) data ;
                    result += current;
                    data = reader.read();
                }

                 return result;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

        public void newQuestion(){
            try {
                Random rand = new Random();

                chosenChar = rand.nextInt(charachterURLs.size());

                ImageDownloader imageTask = new ImageDownloader();

                Bitmap charImage = imageTask.execute(charachterURLs.get(chosenChar)).get();

                imageView.setImageBitmap(charImage);

                locationOfCorrectAnswer = rand.nextInt(4);

                int incorrectAnswerLocation;

                for (int i = 0; i < 4; i++) {
                    if (i == locationOfCorrectAnswer) {
                        answers[i] = charachterNames.get(chosenChar);
                    } else {
                        incorrectAnswerLocation = rand.nextInt(charachterURLs.size());

                        while (incorrectAnswerLocation == chosenChar) {
                            incorrectAnswerLocation = rand.nextInt(charachterURLs.size());
                        }

                        answers[i] = charachterNames.get(incorrectAnswerLocation);
                    }
                }

                button1.setText(answers[0]);
                button2.setText(answers[1]);
                button3.setText(answers[2]);
                button4.setText(answers[3]);
            }catch (Exception e){
                e.printStackTrace();
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);


        imageView = (ImageView) findViewById(R.id.imageView3);

        DownloadTask task = new  DownloadTask() ;
        String result = null ;

        try{

            result = task.execute("https://myanimelist.net/featured/959/Top_20_Favorite_Anime_Guys_and_Boys_on_MAL").get();

            String[] splitResult = result.split("<div class=\"content clearfix featured-article-body pb0 mt16 pt16 url2embed\" itemprop=\"articleBody\">");

            Pattern p = Pattern.compile("<p><img src=\"(.*?)\"");

            Matcher m = p.matcher(splitResult[0]);

            while (m.find()){
                charachterURLs.add(m.group(1));
            }
                charachterURLs.remove("https://image.myanimelist.net/ui/Nxzta1m1Sc-kYrbG5bCjnku6upwUjvR6-FXdzFsEw8G_IEBlhWXN9aJGWRUcxQOrHdSFIj0MU3N8eYp1TrYuTw");
                charachterURLs.remove("https://image.myanimelist.net/ui/M1dUfPArdlQvA3f04MLfRdIYY6ecS5MxgJ3k9GoXcWtIl-8TjiOfU-57f4lSR7Z6QKAhAv0sGyFeyRfl42yI9A");
                charachterURLs.remove("https://image.myanimelist.net/ui/OK6W_koKDTOqqqLDbIoPAvYdSQbDS0J4GCLuJok29Dfn8pZe6u0rb3vhFHIJ_fdB");

            p = Pattern.compile("\">(.*?)</a> from");

            m = p.matcher(splitResult[0]);

            while (m.find()){
                charachterNames.add(m.group(1));
            }
                charachterNames.remove("L Lawliet");
                charachterNames.remove("Okabe &quot;Okarin&quot; Rintarou");
                charachterNames.remove("Kazuto &quot;Kirito&quot; Kirigaya");


             newQuestion();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
