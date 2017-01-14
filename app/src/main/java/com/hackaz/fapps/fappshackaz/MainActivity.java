package com.hackaz.fapps.fappshackaz;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.fapps.hackaz.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppList = this.getPackageManager().queryIntentActivities(mainIntent, 0);
        for(int i = 0; i < pkgAppList.size(); i++) {
            if(pkgAppList.get(i).toString().contains("com.android"))
                continue;
            Log.d("Fappshackaz", pkgAppList.get(i).toString());
        }
    }

    /** Called when the user clicks on the button */
    public void lookup_apps(View view) {
        /* Ben's code should extract the list and we can build the string from it for the text box */
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        editText.setText("TEST THIS MAY NOT WORK"); //This seems to work,
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);
    }
}
