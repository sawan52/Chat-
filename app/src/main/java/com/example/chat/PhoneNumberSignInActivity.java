package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneNumberSignInActivity extends AppCompatActivity {

    private EditText enterNumber, enterOTP;
    private CountryCodePicker countryCodePicker;
    private TextView resendCODE;
    private String verificationId, number;
    private FirebaseAuth mAuth;
    private Button sendOTP, verifyOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_sign_in);

        mAuth = FirebaseAuth.getInstance();

        countryCodePicker = findViewById(R.id.country_code_picker);
        sendOTP = findViewById(R.id.send_otp_button);
        verifyOTP = findViewById(R.id.verify_otp_button);
        enterNumber = findViewById(R.id.enter_number_editText);
        enterOTP = findViewById(R.id.enter_otp_editText);
        resendCODE = findViewById(R.id.resend_otp_textView);

        // passing the country code in number edit text
        countryCodePicker.registerCarrierNumberEditText(enterNumber);

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCode();
            }
        });

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode();
            }
        });

        resendCODE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });
    }

    private void sendCode() {
        if (TextUtils.isEmpty(enterNumber.getText().toString())) {
            Toast.makeText(PhoneNumberSignInActivity.this, "Enter mobile number first!", Toast.LENGTH_SHORT).show();
        } else if (enterNumber.getText().toString().replace(" ", "").length() != 10) {
            Toast.makeText(PhoneNumberSignInActivity.this, "Enter correct number!", Toast.LENGTH_SHORT).show();
        } else {
            hideFields();
            showFields();
            number = countryCodePicker.getFullNumberWithPlus().replace(" ", "");
            sendVerificationCode();
        }
    }

    private void sendVerificationCode() {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                resendCODE.setText("Didn't receive OTP\nSend OTP again in " + l / 1000 + " seconds.");
                resendCODE.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resendCODE.setText("Send OTP again!");
                resendCODE.setEnabled(true);
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                // this method will ask OTP to enter
                PhoneNumberSignInActivity.this.verificationId = verificationId;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // this method will auto verify and finishes the process
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneNumberSignInActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyCode() {
        if (TextUtils.isEmpty(enterOTP.getText().toString())) {
            Toast.makeText(PhoneNumberSignInActivity.this, "Enter OTP first!", Toast.LENGTH_SHORT).show();
        } else if (enterOTP.getText().toString().replace(" ", "").length() != 6) {
            Toast.makeText(PhoneNumberSignInActivity.this, "Enter correct OTP!", Toast.LENGTH_SHORT).show();
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,
                    enterOTP.getText().toString().replace(" ", ""));
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            updateUI();
                            Toast.makeText(PhoneNumberSignInActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(PhoneNumberSignInActivity.this, "signInWithCredential:failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI() {
        Intent intent = new Intent(PhoneNumberSignInActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void hideFields() {
        countryCodePicker.setVisibility(View.GONE);
        enterNumber.setVisibility(View.GONE);
        sendOTP.setVisibility(View.GONE);
    }

    private void showFields() {
        enterOTP.setVisibility(View.VISIBLE);
        resendCODE.setVisibility(View.VISIBLE);
        verifyOTP.setVisibility(View.VISIBLE);
    }

}
