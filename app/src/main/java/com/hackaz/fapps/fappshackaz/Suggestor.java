package com.hackaz.fapps.fappshackaz;

import android.content.pm.ApplicationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains hardcoded user data and a suggestion algorithm to give any user their
 * recommended apps based on similar users.
 */

public class Suggestor {
    public List<User> users;
    public User currentUser;

    public Suggestor (ArrayList<Tags> tags, ArrayList<ApplicationInfo> userAppsInstalled){
        initializeUsers();
        this.currentUser = new User(tags, userAppsInstalled);
    }
    public void initializeUsers() {
        users = new ArrayList<User>();
        // begin user 1 data
        List<Tags> tags1 = new ArrayList<Tags>();
        tags1.add(Tags.BASEBALL);
        tags1.add(Tags.COMPUTER_SCIENCE);
        /*List<ApplicationInfo> apps1 = new ArrayList<ApplicationInfo>();
        ApplicationInfo terminal = new ApplicationInfo();
        terminal.*/
        List<String> apps1 = new ArrayList<String>();
        apps1.add("terminal");
        apps1.add("Facebook");
        apps1.add("Tinder");
        apps1.add("Fapps");
        User testUser = new User(tags1, apps1, 1);
        users.add(testUser);
    }
    public List<String> getSuggestedApps(User mainUser) {
        return null;
    }

}
