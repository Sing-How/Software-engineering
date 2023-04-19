package net.micode.notes.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.micode.notes.R;

import java.util.Set;

public class PasswordView extends Activity{

    private String setpassword;
    private String verifypassword;

    private static final int REQUEST_SET_CODE = 1002;
    private static final int REQUEST_VERIFY_CODE = 1003;

    public String setPassword() {
        getSetPassword();
        return setpassword;
    }

    public String verifyPassword() {
        getVerifyPassword();
        return verifypassword;
    }

    private void getSetPassword() {
        Intent intent = new Intent(PasswordView.this, SetPassword.class);
        startActivityForResult(intent, REQUEST_SET_CODE);
    }

    private void getVerifyPassword() {
        Intent intent = new Intent(PasswordView.this, VerifyPassword.class);
        startActivityForResult(intent, REQUEST_VERIFY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SET_CODE && resultCode == RESULT_OK) {
            setpassword = data.getStringExtra("password");
            // 获取到返回的密码内容，进行处理
        }
        if (requestCode == REQUEST_VERIFY_CODE && resultCode == RESULT_OK) {
            verifypassword = data.getStringExtra("password");
            // 获取到返回的密码内容，进行处理
        }
    }
}

class SetPassword extends Activity {

    private EditText passwordText;
    private String password;
    private static final int REQUEST_CONFIRM_PASSWORD = 1001;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_encryption);

        passwordText = findViewById(R.id.passwordEditText);
        Button btn_0 = findViewById(R.id.button_0);
        Button btn_1 = findViewById(R.id.button_1);
        Button btn_2 = findViewById(R.id.button_2);
        Button btn_3 = findViewById(R.id.button_3);
        Button btn_4 = findViewById(R.id.button_4);
        Button btn_5 = findViewById(R.id.button_5);
        Button btn_6 = findViewById(R.id.button_6);
        Button btn_7 = findViewById(R.id.button_7);
        Button btn_8 = findViewById(R.id.button_8);
        Button btn_9 = findViewById(R.id.button_9);
        Button btn_delete = findViewById(R.id.button_delete);
        Button btn_ok = findViewById(R.id.button_ok);

        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("0");
            }
        });

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("1");
            }
        });

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("2");
            }
        });

        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("3");
            }
        });

        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("4");
            }
        });

        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("5");
            }
        });

        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("6");
            }
        });

        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("7");
            }
        });

        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("8");
            }
        });

        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("9");
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = passwordText.getText().toString();
                if (text.length() > 0) {
                    passwordText.setText(text.substring(0, text.length() - 1));
                }
            }
        });


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = passwordText.getText().toString();
                // 在这里使用获取到的文本
                // 在第一个 Activity 中
                Intent intent = new Intent(SetPassword.this, CheckPassword.class);
                intent.putExtra("password", password);
                startActivityForResult(intent, REQUEST_CONFIRM_PASSWORD);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONFIRM_PASSWORD && resultCode == RESULT_OK) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("password", password);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}

class CheckPassword extends Activity {

    private EditText passwordText;
    private String password;

    private String setPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_encryption_repeat);

        Intent intent = getIntent();
        setPassword = intent.getStringExtra("password");

        passwordText = findViewById(R.id.rpasswordEditText);
        Button btn_0 = findViewById(R.id.rbutton_0);
        Button btn_1 = findViewById(R.id.rbutton_1);
        Button btn_2 = findViewById(R.id.rbutton_2);
        Button btn_3 = findViewById(R.id.rbutton_3);
        Button btn_4 = findViewById(R.id.rbutton_4);
        Button btn_5 = findViewById(R.id.rbutton_5);
        Button btn_6 = findViewById(R.id.rbutton_6);
        Button btn_7 = findViewById(R.id.rbutton_7);
        Button btn_8 = findViewById(R.id.rbutton_8);
        Button btn_9 = findViewById(R.id.rbutton_9);
        Button btn_delete = findViewById(R.id.rbutton_delete);
        Button btn_ok = findViewById(R.id.rbutton_ok);

        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("0");
            }
        });

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("1");
            }
        });

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("2");
            }
        });

        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("3");
            }
        });

        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("4");
            }
        });

        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("5");
            }
        });

        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("6");
            }
        });

        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("7");
            }
        });

        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("8");
            }
        });

        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("9");
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = passwordText.getText().toString();
                if (text.length() > 0) {
                    passwordText.setText(text.substring(0, text.length() - 1));
                }
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = passwordText.getText().toString();
                // 在这里使用获取到的文本
                if (password.equals(setPassword)) {
                    // 密码匹配，执行操作
                    // 在第二个 Activity 中
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("confirmPassword", password);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    // 密码不匹配，显示错误消息
                    passwordText.setText("");
                    Toast.makeText(CheckPassword.this, "两次密码输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

class VerifyPassword extends Activity {
    private EditText passwordText;
    private String password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_encryption_repeat);

        passwordText = findViewById(R.id.vpasswordEditText);
        Button btn_0 = findViewById(R.id.vbutton_0);
        Button btn_1 = findViewById(R.id.vbutton_1);
        Button btn_2 = findViewById(R.id.vbutton_2);
        Button btn_3 = findViewById(R.id.vbutton_3);
        Button btn_4 = findViewById(R.id.vbutton_4);
        Button btn_5 = findViewById(R.id.vbutton_5);
        Button btn_6 = findViewById(R.id.vbutton_6);
        Button btn_7 = findViewById(R.id.vbutton_7);
        Button btn_8 = findViewById(R.id.vbutton_8);
        Button btn_9 = findViewById(R.id.vbutton_9);
        Button btn_delete = findViewById(R.id.vbutton_delete);
        Button btn_ok = findViewById(R.id.vbutton_ok);

        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("0");
            }
        });

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("1");
            }
        });

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("2");
            }
        });

        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("3");
            }
        });

        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("4");
            }
        });

        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("5");
            }
        });

        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("6");
            }
        });

        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("7");
            }
        });

        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("8");
            }
        });

        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.append("9");
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = passwordText.getText().toString();
                if (text.length() > 0) {
                    passwordText.setText(text.substring(0, text.length() - 1));
                }
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = passwordText.getText().toString();
                // 在这里使用获取到的文本
                Intent intent = new Intent();
                intent.putExtra("password", password);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}