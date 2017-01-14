package com.hackaz.fapps.fappshackaz;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    }
        /** Called when the user clicks on the button */
    public void lookup_apps(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);

        // (01) TRY GETTING USER-INSTALLED APPS (01)
        int flags = GET_META_DATA |
                GET_SHARED_LIBRARY_FILES;// |
        //GET_UNINSTALLED_PACKAGES;
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        // (01) TRY GETTING USER-INSTALLED APPS (01)

        String result = "";

        //for(int i = 0; i < applications.size(); i++) {
            //result += applications.get(i).toString() + "\n"; // old
        //    appInfo = applications.get(i);
        //    result += (appInfo != null ? pm.getApplicationLabel(appInfo) : "(@@@unknown!!!)");
        //}
        List<String> appNames = getAppNamesForLookup(applications);
        for(String name : appNames)
            result += name + "\n";
        //editText.setText(result); //This seems to work,
        intent.putExtra(EXTRA_MESSAGE, result);
        startActivity(intent);
    }

    /**
     * Match signature of application to identify that if it is signed by system
     * or not.
     *
     * @param packageName
     *            package of application. Can not be blank.
     * @return <code>true</code> if application is signed by system certificate,
     *         otherwise <code>false</code>
     */
    public boolean isSystemApp(String packageName) {
        try {
            PackageManager pm = getPackageManager(); // get package manager
            // Get packageinfo for target application
            PackageInfo targetPkgInfo = pm.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            // Get packageinfo for system package
            PackageInfo sys = pm.getPackageInfo(
                    "android", PackageManager.GET_SIGNATURES);
            // Match both packageinfo for there signatures
            return (targetPkgInfo != null && targetPkgInfo.signatures != null && sys.signatures[0]
                    .equals(targetPkgInfo.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public List<String> getAppNamesForLookup(List<ApplicationInfo> appData){
        List<String> appNames = new ArrayList<String>();
        for(ApplicationInfo verbose : appData){
            String  v = verbose.toString();
            Log.d("The name is:", v);
            Pattern p = Pattern.compile("\\{........(com.*)\\}");
            Matcher m = p.matcher(v);
            if(m.find()) {
                appNames.add(m.group(1));
                Log.d("GetAppNames", m.group(1));
            }
        }
        return appNames;
    }

    /*Methods to get user stats on applications */

    /** Get usage rating for the given package name (a particular app)
     * 0 - App is not used
     * 1 - App is used
     * 2 - App is heavily used
     */
    @TargetApi(25)
    public int getAppUsage(String packageName){
        Calendar rightNow = Calendar.getInstance();
        Calendar recentHist = Calendar.getInstance();
        recentHist.add(Calendar.DAY_OF_YEAR, -1);

        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(this.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats  = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, recentHist.getTimeInMillis(), rightNow.getTimeInMillis());

        for(UsageStats us : usageStats){
            if(us.getPackageName().equals(packageName)){
                long usage = us.getTotalTimeInForeground();
                if(usage > 3600000)
                    return 2;
                if(usage > 600000)
                    return 1;
                else
                    return 0;
            }
        }
        return 0;
    }
}
