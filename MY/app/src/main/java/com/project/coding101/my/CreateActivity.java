package com.project.coding101.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private String TAG = "Email, PassWord";

    EditText email_textfield,password_textfield,password2_textfield;
    Button btn_join;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createacount);

        email_textfield = findViewById(R.id.EditText_Email);
        password_textfield = findViewById(R.id.EditText_Password);
        password2_textfield = findViewById(R.id.EditText_Password2);


        btn_join = findViewById(R.id.Button_join);

        mAuth = FirebaseAuth.getInstance();

        btn_join.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createAccount(email_textfield.getText().toString(),password_textfield.getText().toString());
            }
        });

    }

    private void createAccount(String email, String password){

        if(!validateForm()){return;}

        mAuth.createUserWithEmailAndPassword(email, password )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(CreateActivity.this, "회원가입을 성공하셨습니다.",Toast.LENGTH_SHORT).show();
                            setUser();
                            startActivity(new Intent(CreateActivity.this,LoginActivity.class));
                        } else {
                            Log.w(TAG, "회원가입 실패!", task.getException());
                            Toast.makeText(CreateActivity.this, "회원가입 실패",Toast.LENGTH_SHORT).show();
                            //에러 두가지 경우 예외처리하기
                        }
                    }
                });
    }

    private void setUser(){
        //DB공부 후, 달기
        String email = email_textfield.getText().toString();
        String name = ((EditText)findViewById(R.id.EditText_name)).getText().toString();
        String nickname =((EditText)findViewById(R.id.EditText_nickname)).getText().toString();
        String collagenum=((EditText)findViewById(R.id.EditText_collagenum)).getText().toString();
        String tel=((EditText)findViewById(R.id.EditText_tel)).getText().toString();

        database = FirebaseDatabase.getInstance().getReference();
        User user = new User(name,email,nickname,collagenum,tel);
        //참조, DB예외 잡기
        database.child("users").child(tel).setValue(user).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //계정삭제코드
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    private boolean validateForm(){
        boolean valid =true;

        String email = email_textfield.getText().toString();
        if(TextUtils.isEmpty(email)){
            email_textfield.setError("필수입력 항목입니다.");
            valid =false;
        }else{
            email_textfield.setError(null);
        }

        String password = password_textfield.getText().toString();
        if(TextUtils.isEmpty(password)){
            password_textfield.setError("필수입력 항목입니다.");
            valid =false;
        }else{
            password_textfield.setError(null);
        }

        String password2 = password2_textfield.getText().toString();
        if(password.equals(password2)){
            password2_textfield.setError(null);
        }else{
            password2_textfield.setError("비밀번호가 일치하지 않습니다.");
            valid =false;
        }
        return valid;
    }

    private String userId(String email){
        //DB path 에러 방지
        String userId = "";
        String e = new String(email);
        String emailarray[] = e.split(",.#$]");
        for (int i =0; i<emailarray.length;i++){
            userId += emailarray[i];
        }
        return userId;
    }

}
