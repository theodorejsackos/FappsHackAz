package com.hackaz.fapps.fappshackaz;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class User {
    private List<Tags> userTags;
    private Map<String, Integer> userApps; //hold package names, usage(0-2)
    private List<String> banList;
    private List<String> interestedList;


    public User(List<Tags> tags, Map<String, Integer> userAppsInstalled) {
        this.userTags = tags;
        this.userApps = userAppsInstalled;
        banList = new ArrayList<>();
        interestedList = new ArrayList<>();

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
    public Map<String, Integer> getInstalledAppNames() {
        return userApps;
    }
    public List<String> getUncommonAppsFromOtherUser(Set<String> otherApps) {
        List<String> uncommonApps = new ArrayList<String>();
        for (String s: otherApps) {
            if (!this.userApps.keySet().contains(s))
                uncommonApps.add(s);
        }
        return uncommonApps;

    }

    //adds to the ban list
    public void addElementToBanList(String name){
        banList.add(name);
    }

    //adds to the interested list
    public void addElementToInterestedList(String name){
        interestedList.add(name);
    }
    public List<String> getAppsList(){
        List<String> result = new ArrayList<String>();
        for (String s: userApps.keySet())
            result.add(s);
        return result;
    }

}
