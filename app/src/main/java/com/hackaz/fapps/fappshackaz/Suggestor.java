package com.hackaz.fapps.fappshackaz;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class contains hardcoded user data and a suggestion algorithm to give any user their
 * recommended apps based on similar users.
 */

public class Suggestor {
    public List<User> users;
    public User currentUser;

    public Suggestor (List<Tags> tags, Map<String, Integer> userAppsInstalled){
        initializeUsers();
        this.currentUser = new User(tags, userAppsInstalled);
    }
    public void initializeUsers() {
        users = new ArrayList<User>();
        // begin user 1 data
        List<Tags> tags1 = new ArrayList<Tags>();
        tags1.add(Tags.BASEBALL);
        tags1.add(Tags.COMPUTER_SCIENCE);

        Map<String, Integer> apps1 = new HashMap<String, Integer>();
        //apps1.put("Terminal", 1);

        apps1.put("com.crowdstar.covetfashion", 2);
        //apps1.put("Tinder", 2);
        apps1.put("com.hackaz.fapps.fappshackaz", 2);
        //apps1.put("Testing", 0);
        apps1.put("com.android.calculator2", 1);

        User testUser = new User(tags1, apps1);
        users.add(testUser);

        tags1.remove(Tags.BASEBALL);
        tags1.add(Tags.BASKETBALL);
        tags1.add(Tags.BUSINESS);
        Map<String, Integer> apps2 = new HashMap<String, Integer>();
        apps2.put("com.breel.geswallpapers", 1);
        apps2.put("com.svox.pico", 2);
        apps2.put("com.ustwo.lwp", 2);
        //apps2.put("com.hackaz.fapps.fappshackaz", 0);
        User testUser2 = new User(tags1, apps2);
        users.add(testUser2);

    }


    /* Suggests apps ordered by rank, which is determined by other users' matching interests and
     * career and usage.
     * Returns a list of the package names of suggested apps.
     */
    public List<String> getSuggestedApps() {
        HashMap<String, Integer> suggestedApps = new HashMap<String, Integer>();
        for (User otherUser: users) {
            int similarTagCount = this.currentUser.getSimilarTags(otherUser.getTags()).size();
            List<String> otherUserApps = this.currentUser.getUncommonAppsFromOtherUser(otherUser.getInstalledAppNames().keySet());
            for (String appName: otherUserApps) {
                int usageMultiplier = otherUser.getInstalledAppNames().get(appName);
                if (!suggestedApps.containsKey(appName)) {
                    suggestedApps.put(appName, similarTagCount * usageMultiplier);
                } else {
                    suggestedApps.put(appName, similarTagCount * usageMultiplier + suggestedApps.get(appName));
                }
            }
        }
        // sort the list
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
    // a comparator that compares values of a map that holds <String, Integer> key-value pairs
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
