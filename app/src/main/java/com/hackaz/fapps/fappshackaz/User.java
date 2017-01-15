package com.hackaz.fapps.fappshackaz;
import android.content.pm.ApplicationInfo;

import java.util.ArrayList;
import java.util.List;

public class User {
    private List<Tags> userTags;
    private List<ApplicationInfo> userAppsInstalled;
    private List<String> userAppNames;
    public User(List<Tags> tags, List<ApplicationInfo> userAppsInstalled) {
        this.userTags = tags;
        this.userAppsInstalled = userAppsInstalled;

    }
    public User(List<Tags> tags, List<String> userAppsInstalled, int i) { //int i just used to differentiate constructor
        this.userTags = tags;
        this.userAppNames = userAppsInstalled;

    }
    /*
     * returns the intersection between this user's tags and the other user's
     */
    public List<Tags> getSimilarTags(List<Tags> otherUser) {
        List<Tags> matchingTags = userTags;
        matchingTags.retainAll(otherUser);
        return matchingTags;
    }
    public List<Tags> getTags() {
        return this.userTags;
    }
    public List<ApplicationInfo> getInstalledApps() {
        return userAppsInstalled;
    }
    public List<String> getInstalledAppNames() {
        return userAppNames;
    }
    public List<String> getUncommonAppsFromOtherUser(List<String> otherApps) {
        List<String> uncommonApps = new ArrayList<String>();
        for (String s: otherApps) {
            if (!this.userAppNames.contains(s))
                uncommonApps.add(s);
        }
        return uncommonApps;

    }

}
