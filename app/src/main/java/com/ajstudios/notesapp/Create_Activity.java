package com.ajstudios.notesapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import io.realm.Realm;

public class Create_Activity extends AppCompatActivity {

    public static final int PICK_IMAGE_REQUEST_EVENT1 = 1;

    private EditText notes_ET, title_ET;
    private ImageView add_image, imageView, cancel_btn, submit_btn;
    private MaterialCardView cardView;

    private Uri mImageUri;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        notes_ET = findViewById(R.id.notes_create_ET);
        title_ET = findViewById(R.id.title_create_ET);
        add_image = findViewById(R.id.image_add_button);
        imageView = findViewById(R.id.image_create);
        cardView = findViewById(R.id.card_create);
        cardView.setVisibility(View.GONE);
        cancel_btn = findViewById(R.id.cancel_btn_create);
        submit_btn = findViewById(R.id.submit_create);

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView.setVisibility(View.GONE);
                add_image.setVisibility(View.VISIBLE);
            }
        });

        title_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!title_ET.getText().toString().isEmpty()){
                        submit_btn.setColorFilter(getResources().getColor(R.color.green));
                        submit_btn.setClickable(true);
                    }else{
                        submit_btn.setColorFilter(getResources().getColor(R.color.inactive));
                        submit_btn.setClickable(false);
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

    }

    private void saveNote() {

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        File file = wrapper.getDir("Notes App",MODE_APPEND);

        file = new File(file, System.currentTimeMillis() + "." + getFileExtension(mImageUri));


        try {

            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

            stream.flush();
            stream.close();

            Toast.makeText(this, "sucess" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "failed", Toast.LENGTH_LONG).show();
        }

    }

    private void openFileChooser() {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_EVENT1 );

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== PICK_IMAGE_REQUEST_EVENT1 && resultCode == RESULT_OK
                && data!= null && data.getData()!=null){
            mImageUri= data.getData();
            cardView.setVisibility(View.VISIBLE);
            add_image.setVisibility(View.GONE);
            Picasso.with(this).load(mImageUri).into(imageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}