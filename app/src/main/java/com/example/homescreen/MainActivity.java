package com.example.homescreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView image;

        image=(ImageView)findViewById(R.id.imageView2);
        setImage(image);

        TextView dateview = (TextView)findViewById( R.id.textView_date);
        setDate(dateview);

        TextView info = (TextView)findViewById(R.id.textView_info);
        setinfo(info);
    }

    public void setDate (TextView view){
        String str = String.format("%tc", new Date());
        view.setText(str);
    }
    public  void setImage(ImageView image)
    {
        image.setImageResource(R.drawable.img);
    }
    public  void setinfo(TextView info)
    {
        String str_info = "Halifax Data Science\n"+"Social Meetup - Foggy Google\n"+"6pm";
        info.setText(str_info);
    }


}
