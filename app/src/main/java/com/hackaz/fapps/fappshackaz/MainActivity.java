package com.hackaz.fapps.fappshackaz;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.pm.PackageManager.GET_META_DATA;
import static android.content.pm.PackageManager.GET_SHARED_LIBRARY_FILES;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.fapps.hackaz.MESSAGE";
    PackageManager pm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pm = getPackageManager();
    }
    /** Called when the user clicks on the button */
    public void lookup_apps(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);

        // (01) TRY GETTING USER-INSTALLED APPS (01)
        int flags = GET_META_DATA |
                GET_SHARED_LIBRARY_FILES;// |
        //GET_UNINSTALLED_PACKAGES;
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        // (01) TRY GETTING USER-INSTALLED APPS (01)

        String result = "";

        List<String> appNames = getAppNamesForLookup(applications); // gets package names

        LinkedList<String> userPackageNames = new LinkedList<>();
        for(String packageName : appNames) {
            if(!isSystemApp(packageName)) {
                userPackageNames.add(packageName);
            }
        }

        // gather list of package names
        for(String name : appNames)
            result += name + "\n";

        // add list of user installed app names
        result += "= = = = = = = = = = =\n";
        /*result += "Num User Apps: "+userPackageNames.size()+"\n";
        result += "= = = = = = = = = = =\n";*/
        result += getUserAppNames(userPackageNames);

        //editText.setText(result); //This seems to work,
        intent.putExtra(EXTRA_MESSAGE, result);
        startActivity(intent);
    }

    public String getUserAppNames(LinkedList<String> packageNames) {
        ApplicationInfo ai = null;
        String result = "";
        for(String uName : packageNames) {
            try {
                ai = pm.getApplicationInfo(uName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (!isAppPreLoaded(uName)) {
                String dir;
                /*try {
                    //dir = pm.getPackageInfo(uName, 0).applicationInfo.dataDir;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }*/
                dir = ai.sourceDir;
                if(!isSystemDir(dir)) {
                    String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
                    result += (applicationName + "\n");
                    //result += ("   @" + dir + "\n");
                }
            }
        }
        return result;
    }

    /**
     * Called by getUserAppNames, uses regex to check if APK directory path is in system/ or data/
     * @param path
     * @return
     */
    public boolean isSystemDir(String path) {
        Pattern p = Pattern.compile("/system.*");
        Matcher m = p.matcher(path);
        if(m.find())
            return true;
        return false;
    }

    /**
     * Called by isAppPreLoaded
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

    /**
     * Check if application is preloaded. It also check if the application is
     * signed by system certificate or not by calling isSystemApp.
     *
     * @param packageName
     *            package name of application. Can not be null.
     * @return <code>true</code> if package is preloaded and system.
     */
    public boolean isAppPreLoaded(String packageName) {
        if (packageName == null) {
            throw new IllegalArgumentException("Package name can not be null");
        }

        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo( packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // First check if it is preloaded.
        // If yes then check if it is System app or not.
        if (ai != null
                && pm.getLaunchIntentForPackage(packageName) != null
                && (ai.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            // Check if signature matches
            if (isSystemApp(packageName) == true) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public List<String> getAppNamesForLookup(List<ApplicationInfo> appData){
        List<String> appNames = new ArrayList<>();
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


}
