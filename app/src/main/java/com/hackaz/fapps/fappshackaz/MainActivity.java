package com.hackaz.fapps.fappshackaz;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppList = this.getPackageManager().queryIntentActivities(mainIntent, 0);
        for(int i = 0; i < pkgAppList.size(); i++)
            Log.d("Fappshackaz", pkgAppList.get(i).toString());

    }
}
