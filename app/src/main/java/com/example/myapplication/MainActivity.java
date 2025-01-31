package com.example.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button buttonLogin = findViewById(R.id.main_login);
        Button buttonRegister = findViewById(R.id.main_register);

        ImageView imageView = findViewById(R.id.imageView);

        try {
            SVG svg = SVG.getFromResource(this, R.raw.logo); // Load from res/raw
            // OR, if the SVG is in the assets folder:
            // SVG svg = SVG.getFromAsset(this.getAssets(), "your_logo.svg");
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            imageView.setImageDrawable(drawable);

        } catch (SVGParseException e) {
            // Handle SVG parsing errors
            e.printStackTrace();
        }

        FirebaseApp.initializeApp(this);
        buttonLogin.setOnClickListener(view -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

        buttonRegister.setOnClickListener(view -> {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
        });

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    }
}