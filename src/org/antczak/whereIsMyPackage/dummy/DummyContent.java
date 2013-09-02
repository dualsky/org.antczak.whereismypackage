
package org.antczak.whereIsMyPackage.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    static {
        // Add 3 sample items.
        addItem(new DummyItem("1", "Wszystkie"));
        addItem(new DummyItem("2", "Oczekiwane"));
        addItem(new DummyItem("3", "Wys³ane"));
        addItem(new DummyItem("4", "Archiwum"));
        addItem(new DummyItem("5", "2013-08-30"));
        addItem(new DummyItem("6", "2013-08-31"));
        addItem(new DummyItem("7", "2013-08-32"));
        addItem(new DummyItem("8", "2013-08-33"));
        addItem(new DummyItem("9", "2013-08-34"));
        addItem(new DummyItem("10", "2013-08-35"));
        addItem(new DummyItem("11", "2013-08-36"));

    }

    public static String[] dummyStringArray = new String[11];

    static {
        // Add 3 sample items.
        dummyStringArray[0] = "Wszystkie";
        dummyStringArray[1] = "Oczekiwane";
        dummyStringArray[2] = "Wys³ane";
        dummyStringArray[3] = "Archiwum";
        dummyStringArray[4] = "2013-08-30";
        dummyStringArray[5] = "2013-08-31";
        dummyStringArray[6] = "2013-08-32";
        dummyStringArray[7] = "2013-08-33";
        dummyStringArray[8] = "2013-08-34";
        dummyStringArray[9] = "2013-08-35";
        dummyStringArray[10] = "2013-08-36";

    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String content;

        public DummyItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
