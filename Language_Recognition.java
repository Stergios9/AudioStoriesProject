package com.unipi.mobile_dev.audiostoriesproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;


public class WelcomeActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private DatabaseReference languageRef;
    EditText email,password;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        sharedPreferences = getSharedPreferences("com.unipi.mobile_dev.audiostoriesproject", Context.MODE_PRIVATE);
        email = findViewById(R.id.editTextEmailAddress);
        password = findViewById(R.id.editTextPassword);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        languageRef = FirebaseDatabase.getInstance().getReference("Language");
        languageRef.setValue("");
        Locale defaultLocale = Locale.getDefault();
        language = defaultLocale.getLanguage();
    }

    public void goSignIn(View view) {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        boolean emailEmpty = userEmail.isEmpty();
        boolean passwordEmpty = userPassword.isEmpty();

        if (!emailEmpty && !passwordEmpty){// Credentials are both set
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){// Log In Successful
                                showMessage(getString(R.string.success_title), getString(R.string.success_signin_description));
                                saveUserType(userEmail);
                                navigateToLibraryActivity();
                            }else {
                                showMessage(getString(R.string.error_title), task.getException().getLocalizedMessage());
                            }
                        }
                    });
        }else {
            showErrorMessages(emailEmpty, passwordEmpty);
        }
    }

    public void goSignUp(View view){
        Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void visitor(View view) {
        saveUserType("Visitor");
        navigateToLibraryActivity();
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).
                setTitle(title).
                setMessage(message).
                setCancelable(true).
                show();
    }

    private void showErrorMessages(boolean emailEmpty, boolean passwordEmpty) {
        String errorMessage;
        if (emailEmpty && passwordEmpty) {
            errorMessage = getString(R.string.error_email_password_empty);
        } else if (emailEmpty) {
            errorMessage = getString(R.string.error_email_empty);
        } else {
            errorMessage = getString(R.string.error_password_empty);
        }
        showMessage(getString(R.string.error_title), errorMessage);
    }

    private void saveUserType(String userEmail) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserType", userEmail);
        editor.apply();
    }

    private void navigateToLibraryActivity() {
        Intent intent = new Intent(WelcomeActivity.this, LibraryActivity.class);
        startActivity(intent);
    }
}