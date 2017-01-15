package com.hackaz.fapps.fappshackaz;

import android.content.pm.ApplicationInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.Comparator;

/**
 * This class contains hardcoded user data and a suggestion algorithm to give any user their
 * recommended apps based on similar users.
 */

public class Suggestor {
    public List<User> users;
    public User currentUser;

    public Suggestor (List<Tags> tags, List<ApplicationInfo> userAppsInstalled){
        initializeUsers();
        this.currentUser = new User(tags, userAppsInstalled);
    }


    public Suggestor (List<Tags> tags, List<String> userAppsInstalled, int i){
        initializeUsers();
        this.currentUser = new User(tags, userAppsInstalled, i);
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
        apps1.add("testing");
        User testUser = new User(tags1, apps1, 1);
        users.add(testUser);
    }
    public List<String> getSuggestedApps() {
        HashMap<String, Integer> suggestedApps = new HashMap<String, Integer>(); // should be appinfo
        for (User otherUser: users) {
            int similarTagCount = this.currentUser.getSimilarTags(otherUser.getTags()).size();
            List<String> otherUserApps = this.currentUser.getUncommonAppsFromOtherUser(otherUser.getInstalledAppNames());
            for (String appName: otherUserApps) {
                if (!suggestedApps.containsKey(appName)) {
                    suggestedApps.put(appName, similarTagCount);
                } else {
                    suggestedApps.put(appName, similarTagCount+suggestedApps.get(appName));
                }
            }
        }
        Map<String, Integer> sortedMap = sortMapByValue(suggestedApps);
        List<String> sortedSApps = new ArrayList<String>();
        for (String s: sortedMap.keySet()) {
            sortedSApps.add(s);
        }
        return sortedSApps;
    }
    public TreeMap<String, Integer> sortMapByValue(HashMap<String, Integer> map){
        Comparator<String> comparator = new ValueComparator(map);
        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by keys.
        TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
        result.putAll(map);
        return result;
    }
    // a comparator that compares Strings
    class ValueComparator implements Comparator<String>{

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        public ValueComparator(HashMap<String, Integer> map){
            this.map.putAll(map);
        }

        @Override
        public int compare(String s1, String s2) {
            if(map.get(s1) >= map.get(s2)){
                return -1;
            }else{
                return 1;
            }
        }
    }

}
