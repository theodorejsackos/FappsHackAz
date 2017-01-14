package com.hackaz.fapps.fappshackaz;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.LinkedList;
import java.util.List;

import static android.content.pm.PackageManager.GET_META_DATA;
import static android.content.pm.PackageManager.GET_SHARED_LIBRARY_FILES;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.fapps.hackaz.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppList = this.getPackageManager().queryIntentActivities(mainIntent, 0);
        Log.d("Fappshackaz", "First size: " + pkgAppList.size());
        for (int i = 0; i < pkgAppList.size(); i++)
            Log.d("Fappshackaz", pkgAppList.get(i).toString());

        // TRY GETTING USER-INSTALLED APPS
        int flags = GET_META_DATA |
                GET_SHARED_LIBRARY_FILES;// |
        //GET_UNINSTALLED_PACKAGES;
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        List<ApplicationInfo> systemInstalled = applications;
        List<ApplicationInfo> userInstalled = new LinkedList<>();
        int numTotal = applications.size();
        int numSys = systemInstalled.size();
        int numUser = userInstalled.size();
        Log.d("NUMBER OF APPS", "***\nTotal: " + numTotal + "\nUser: " + numUser + "\nSystem: " + numSys + "***\n");

        ApplicationInfo appInfo;
        for (int i = 0; i < applications.size(); i++) {
            appInfo = applications.get(i);
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // System application
            } else {
                // Installed by user
                userInstalled.add(appInfo);
                systemInstalled.remove(appInfo); // removeIf?
            }
        }
        numTotal = applications.size();
        numSys = systemInstalled.size();
        numUser = userInstalled.size();
        if (numSys + numUser == numTotal) {
            Log.d("NUMBER OF APPS", "*** Looking A O K boss! ***");
        } else {
            Log.d("NUMBER OF APPS", "***\nTotal: " + numTotal + "\nUser: " + numUser + "\nSystem: " + numSys + "***\n");
        }
        Log.d("****-----****\nLONG LIST", "lsadfjlaksfda;slfj");
        for (int i = 0; i < applications.size(); i++)
            Log.d("SOMETHING ELSE", applications.get(i).toString());
        // TRY GETTING USER-INSTALLED APPS
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
