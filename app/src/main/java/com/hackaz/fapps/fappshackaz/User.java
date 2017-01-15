package com.hackaz.fapps.fappshackaz;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class User {
    private List<Tags> userTags;
    private Map<String, Integer> userApps; //hold package names, usage(0-2)


    public User(List<Tags> tags, Map<String, Integer> userAppsInstalled) {
        this.userTags = tags;
        this.userApps = userAppsInstalled;

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

}
