package net.micode.notes.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.micode.notes.R;

public class PasswordActivity extends Activity {
    protected String password = "123321";
    private Button button_OK;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_activity);
        button_OK = (Button) findViewById(R.id.button_OK);
        final EditText passwordEdit = (EditText) findViewById(R.id.password_edit);
        button_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input_password = passwordEdit.getText().toString();
                if(input_password == password) {
                    Intent intent = new Intent();
                    intent.setClass(PasswordActivity.this, NotesListActivity.class);

                }
            }
        });
    }


}
