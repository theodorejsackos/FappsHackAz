package com.hackaz.fapps.fappshackaz;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.pm.PackageManager.GET_META_DATA;
import static android.content.pm.PackageManager.GET_SHARED_LIBRARY_FILES;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.fapps.hackaz.MESSAGE";
    private Suggestor sug;
    private TextView tv;
    PackageManager pm = null;

    PrintWriter out = null;
    BufferedReader in = null;
    Socket conn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pm = getPackageManager();
        getSuggestions();

        // THEODORE'S DISPLAY ICON CODE
//        try {
//            Drawable icon = pm.getApplicationIcon("com.groupme.android");
//            findViewById(R.id.image_area).setBackgroundDrawable(icon);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        // BEN'S package name -> icon -> bitmap -> string -> (server -> phone) -> bitmap -> display
        try {
            Drawable icon = pm.getApplicationIcon("com.hackaz.fapps.fappshackaz");

            Bitmap b1 = drawableToBitmap(icon);
            String s1 = encodeToBase64(b1);
            Bitmap b2 = decodeBase64(s1);

            ((ImageView) findViewById(R.id.image_area)).setImageBitmap(b2);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //new Thread(new SendMessage("Hello there buddy!")).start();
        //Log.d("SERVER_CONN", "Message Send in thread");

//        try {
//            ProfileNode pn = new ProfileNode();
//            pn.ud = UserDemographic.STUDENT_UNIVERSITY;
//            pn.tc = TradeCraft.COMPUTER_SCIENCE;
//            pn.interests = Arrays.asList(Interests.SOCCER, Interests.PROGRAMMING, Interests.PHILOSOPHY);
//            new Thread(new SendObject(pn)).start();
//        }catch(Exception e){
//            Log.d("SEND_SERIAL", "Failed to serialize and send ProfileNode sample");
//        }
    }

    // FOR THE NEXT BUTTON
    public void firstSuggestedToIcon(String packageName) {
        try {
            Drawable icon = pm.getApplicationIcon("com.hackaz.fapps.fappshackaz");

            Bitmap b1 = drawableToBitmap(icon);
            String s1 = encodeToBase64(b1);
            Bitmap b2 = decodeBase64(s1);

            ((ImageView) findViewById(R.id.image_area)).setImageBitmap(b2);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Called when the user clicks on the button */
    public void lookup_apps(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);

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
        result += "Suggested apps: \n";
        List<String> sugpnames = getSuggestions();
        for (String s: sugpnames)
            result += s + "\n";
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

    // SEND TO SERVER: PACKAGE NAMES
    public LinkedList<String> getUserAppPackageNames(List<String> packageNames) {
        ApplicationInfo ai = null;
        LinkedList<String> result = new LinkedList<>();
        for(String uName : packageNames) {
            try {
                ai = pm.getApplicationInfo(uName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (!isAppPreLoaded(uName)) {
                String dir = ai.sourceDir;
                if(!isSystemDir(dir)) {
                    result.add(uName);
                }
            }
        }
        return result;
    }

    public LinkedList<Drawable> getIcons(LinkedList<String> pNames) {
        LinkedList<Drawable> icons = new LinkedList<>();
        for(String p : pNames) {
            try {
                icons.add(pm.getApplicationIcon(p));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return icons;
    }

    // icon to bitmap
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

//    // SEND TO SERVER: icon bitmaps to strings
//    public List<String> getIconStrings(List<Drawable> icons) {
//        List<String> iconStrings = new LinkedList<>();
//        for(Drawable icon : icons) {
//            Bitmap photo = drawableToBitmap(icon);
//            ByteArrayOutputStream bao = new ByteArrayOutputStream();
//            photo.compress(Bitmap.CompressFormat.PNG, 100, bao);
//            byte[] ba = bao.toByteArray();
//            String ba1= Base64.encodeToString(ba, Base64.DEFAULT);
//            iconStrings.add(ba1);
//        }
//        return iconStrings;
//    }

    // SEND TO SERVER: icon bitmap to string
    // EXAMPLE: String myBase64Image = encodeToBase64(myBitmap
    public static String encodeToBase64(Bitmap image)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    // GET FROM SERVER: icon string to bitmap to drawable icon
    // EXAMPLE: Bitmap myBitmapAgain = decodeBase64(myBase64Image);
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    /*
    Display a bitmap in view code snippet
        ImageView mImg;
        mImg = (ImageView) findViewById(R.id.imageView2);
        mImg.setImageBitmap(bmOut);
     */

    /**
     * Called by getUserAppNames, uses regex to check if APK directory path is in system/ or data/
     * @param path
     * @return boolean
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
            // Get package info for target application
            PackageInfo targetPkgInfo = pm.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            // Get package info for system package
            PackageInfo sys = pm.getPackageInfo(
                    "android", PackageManager.GET_SIGNATURES);
            // Match both package info for there signatures
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
            if (isSystemApp(packageName)) {
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

    public List<String> getSuggestions() { // currently hardcoded user tags
        List<Tags> tags = new ArrayList<Tags>();
        tags.add(Tags.BASKETBALL);
        tags.add(Tags.COMPUTER_SCIENCE);
        int flags = GET_META_DATA |
                GET_SHARED_LIBRARY_FILES;
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        List<String> allPackageNames = getAppNamesForLookup(applications); // gets package names
        List<String> appNames = getUserAppPackageNames(allPackageNames); // gets user package names
        Map<String, Integer> userPackageNames = new HashMap<String, Integer>(); // holds package name and usage
        for(String packageName : appNames) {
            if(!isSystemApp(packageName)) {
                userPackageNames.put(packageName, getAppUsage(packageName));
            }
        }
        sug = new Suggestor(tags, userPackageNames);
        return sug.getSuggestedApps();
    }

    private class SendMessage implements Runnable {
        private String mMsg;

        public SendMessage(String msg) {
            mMsg = msg;
        }
        public void run() {
            try {
                Socket conn = new Socket("ec2-35-166-192-131.us-west-2.compute.amazonaws.com", 8080);
                OutputStream os = conn.getOutputStream();
                PrintWriter out = new PrintWriter(os,true);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                out.write(mMsg + "\n");  //write the message to output stream
                out.flush();
                String echo = in.readLine();
                Log.d("echo_from_server: ", echo);
                out.close();
                conn.close();   //closing the connection
            } catch (Exception e) {
                Log.d("FAILED_CONN", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private class SendObject implements Runnable {
        private ProfileNode mMsg;

        public SendObject(ProfileNode msg) {
            mMsg = msg;
        }
        public void run() {
            try {
                Socket conn = new Socket("ec2-35-166-192-131.us-west-2.compute.amazonaws.com", 8080);
                ObjectOutputStream os = new ObjectOutputStream(conn.getOutputStream());
                os.writeObject(mMsg);  //write the message to output stream
                os.flush();
                os.close();
                conn.close();   //closing the connection
            } catch (Exception e) {
                Log.d("FAILED_CONN", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private class SendApps implements Runnable {
        private List<String> mMsg;

        public SendApps(List<String> msg) {
            mMsg = msg;
        }
        public void run() {
            try {
                Socket conn = new Socket("ec2-35-162-158-36.us-west-2.compute.amazonaws.com", 8080);
                OutputStream os = conn.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(os);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                out.writeObject(mMsg);  //write the list of apps to output stream
                out.flush();
                String echo = in.readLine();
                Log.d("echo_from_server: ", echo);
                out.close();
                conn.close();   //closing the connection
            } catch (Exception e) {
                Log.d("FAILED_CONN", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    //dislike button will save app information into a personal dislike list
    //also removes from the suggested apps list
    public void onClickDislikeButton(View v){
        if(sug.getSuggestedApps().size() == 0){
            return;
        }
        sug.currentUser.addElementToBanList(sug.getSuggestedApps().get(0)); //add string value

        //display Toast message
        Context context = getApplicationContext();
        CharSequence text = "Added to Dislike List!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        sug.getSuggestedApps().remove(0);
        displayDescription();
    }

    //download button will take you to the play store
    //also removes from the suggested apps list
    public void onClickDownloadButton(View v){
        if(sug.getSuggestedApps().size() == 0){
            return;
        }
        String temp = sug.getSuggestedApps().get(0); //gets that element
        sug.getSuggestedApps().remove(0);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.google.android.apps.maps")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.google.android.apps.maps")));
        }
    }

    //next button
    public void onClickNextButton1(View v){
        if(sug.getSuggestedApps().size() == 0){
            return;
        }
        String temp = sug.getSuggestedApps().get(0); //gets that element
        sug.getSuggestedApps().remove(0);
        sug.getSuggestedApps().add(temp); //adds to the back of the lsit
        displayDescription();
    }

    //display descipriton and titles
    public void displayDescription(){
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse("market://details?id=" + sug.getSuggestedApps().get(0)));
    startActivity(intent);
}

    //gets first app of the list's name
    private String getUserAppNamesAtBeginning() {
//        ApplicationInfo ai = null;
//        String result = "";
//        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return sug.getSuggestedApps().get(0);
    }
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {

                    }

                    // Right to left swipe action
                    else
                    {

                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private class ProfileNode implements Serializable{
        public UserDemographic ud;
        public TradeCraft tc;
        public List<Interests> interests;
    }
}