package com.example.android.demoapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class YelpBizDetail extends Activity {

    ImageView drawingImageView;
    String aboutText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yelp_biz_detail);
        TextView textName = (TextView)findViewById(R.id.textName);
        TextView textRating = (TextView)findViewById(R.id.textRating);
        TextView textCategory = (TextView)findViewById(R.id.textCategory);
        TextView textPhone = (TextView)findViewById(R.id.textPhone);
        drawingImageView=(ImageView)findViewById(R.id.ratingImage);
        Button directionURL = (Button)findViewById(R.id.buttonDirection);
        Button websiteURL = (Button)findViewById(R.id.b2);

        Intent i = getIntent();
        final ModelClass biz = (ModelClass)i.getSerializableExtra("Detailclass");

        textName.setText("Name : "+biz.getName()+"\n");
        textRating.setText("Rating : "+biz.getRating()+"\n");
        textCategory.setText("Category : "+biz.getCategory()+"\n");
        textPhone.setText("Phone : "+biz.getPhone()+"\n");
        websiteURL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String data = biz.getMobileURL();
                Uri uri = Uri.parse(data);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        directionURL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                double latitude = biz.getLatitude();
                double longitude = biz.getLongitude();
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "("+biz.getName()+")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);

            }
        });


        new FetchRatingImage((ImageView) findViewById(R.id.ratingImage))
                .execute(biz.getRatingURL());

    }

    private class FetchRatingImage extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        public FetchRatingImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}