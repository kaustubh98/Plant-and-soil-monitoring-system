package com.example.plantmonitoringsystem.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.plantmonitoringsystem.R;

import static android.app.Activity.RESULT_OK;

public class FragmentDisease extends Fragment {


    private int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;

    public FragmentDisease() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_disease, container, false);
        Button camera = root.findViewById(R.id.camera);
        imageView = root.findViewById(R.id.imageView);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCamera();
            }
        });
        return root;
    }

    private void OpenCamera(){

        try{
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
}
