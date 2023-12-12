package com.example.chatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.databinding.ActivitySingUpBinding;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SingUpActivity extends AppCompatActivity {
    private ActivitySingUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingUpBinding.inflate(getLayoutInflater());
         preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListener();
    }

    private void setListener() {
        binding.textsingIn.setOnClickListener(v -> onBackPressed());
        binding.buttonSingUp.setOnClickListener(v -> {
            if (isValidSingDetails()) {
                singUp();
            }
        });
        binding.layoutImage.setOnClickListener( v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });
    }

    private void showToast(String mensagem) {
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
    }

    private void singUp() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.inputName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImagem);
        database.collection(Constants.KEY_COLLECTION_USERS).add(user)
                .addOnSuccessListener(documentReference -> {

                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME,binding.inputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImagem);

                    Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }).addOnFailureListener(exception->{
                        loading(false);
                        showToast(exception.getMessage());
                });

    }


    private String encodImage(Bitmap bitmap) {
        int previewWidh = 150;
        int previewHeingh = bitmap.getHeight() * previewWidh / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidh, previewHeingh, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    binding.imageProfile.setImageBitmap(bitmap);
                    binding.textAddImage.setVisibility(View.GONE);
                    encodedImagem = encodImage(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    });

    private boolean isValidSingDetails() {
        if (encodedImagem == null) {
            showToast("Selecione uma imagem");
            return false;
        } else if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Informe o Nome");
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Informe o email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Email invalido");
            return false;

        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Informe o Senha");
            return false;
        } else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Informe o Nome");
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {
            showToast("Senhas diferentes");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSingUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSingUp.setVisibility(View.VISIBLE);

        }
    }
}

