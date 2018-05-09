package ingage.ingage.util;

import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Davis on 4/17/2017.
 */

public class Configurations {

    private static final List<Feature> Features = new ArrayList<Feature>();

    static {
        addFeature("Politics");
        addFeature("Music");
        addFeature("Movies");
        addFeature("Art");
        addFeature("Sports");
        addFeature("Games");
        addFeature("Philosophy");
        addFeature("Technology");

    }

    public static List<Feature> getFeatureList() {
        return Collections.unmodifiableList(Features);
    }

    public static Feature getFeatureByName(final String name) {
        for (Feature Feature : Features) {
            if (Feature.name.equals(name)) {
                return Feature;
            }
        }
        return null;
    }

    private static void addFeature(final String name) {
        Feature Feature = new Feature(name);
        Features.add(Feature);
    }

    public static class Feature {
        public String name;
        public int iconResId;
        public int titleResId;
        public int subtitleResId;
        public int overviewResId;
        public int descriptionResId;
        public int poweredByResId;
        public List<Item> demos;


        public Feature(final String name) {
            this.name = name;
        }
    }

    public static class Item {
        public int titleResId;
        public int iconResId;
        public int buttonTextResId;
        public String fragmentClassName;

        public String title;
        public String buttonText;
        public Serializable tag ;

        public Item(final int titleResId, final int iconResId, final int buttonTextResId,
                    final Class<? extends Fragment> fragmentClass) {
            this.titleResId = titleResId;
            this.iconResId = iconResId;
            this.buttonTextResId = buttonTextResId;
            this.fragmentClassName = fragmentClass.getName();
        }

        public Item(final String title, final String buttonText, final Serializable tag, final Class<? extends  Fragment> fragmentClass){
            this.title = title;
            this.buttonText = buttonText;
            this.tag = tag;
            this.fragmentClassName = fragmentClass.getName();
        }
    }
}