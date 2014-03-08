package org.greencheek.related.api.indexing;

import org.greencheek.related.api.RelatedItemAdditionalProperties;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by dominictootell on 01/03/2014.
 */
public class RelatedItemUtil {

    public static Comparator<String[]> PROPERTY_NAME_COMPARATOR = new Comparator<String[]>() {
        @Override
        public int compare(final String[] entry1, final String[] entry2) {
            final String time1 = entry1[0];
            final String time2 = entry2[0];
            return time1.compareTo(time2);
        }
    };

    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };


    public static char[] bytesToHex(byte[] data) {

        int l = data.length;

        char[] out = new char[l << 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }

        return out;

    }

    public static char[] getComparisonHashForRelatedItemProperties(String[][] props,int propertySize, MessageDigest digest) {
        StringBuilder b = new StringBuilder(props.length*propertySize);
        for(String[] keyValue : props) {
            b.append(keyValue[0]).append(keyValue[1]);
        }
        byte[] data = digest.digest(b.toString().getBytes());
        digest.reset();
        return bytesToHex(data);
    }


    public static void main(String[] args) throws Exception{
        String[][] prop = new String[][] { new String[] { "prop1","val1"},
                                           new String[] { "prop3","val3"},
                                           new String[] { "alpah","one"},
                                           new String[] { "abs","up"}};

        String[][] prop2 = new String[][] { new String[] { "prop1","val1"},
                new String[] { "alpah","one"},
                new String[] { "abs","up"},
                new String[] { "prop3","val3"}

                };
        Arrays.sort(prop,PROPERTY_NAME_COMPARATOR);
        Arrays.sort(prop2,PROPERTY_NAME_COMPARATOR);

        System.out.println(getComparisonHashForRelatedItemProperties(prop, 10, MessageDigest.getInstance("SHA-256")));
        System.out.println(getComparisonHashForRelatedItemProperties(prop2,10,MessageDigest.getInstance("SHA-256")));

    }

    public static String[][] getSortedProperties(RelatedItemAdditionalProperties properties) {
        int maxNumberOfProperties = properties.getNumberOfProperties();
        String[][] sorted = new String[maxNumberOfProperties][2];
        for(int i=0;i<maxNumberOfProperties;i++) {
            sorted[i] = new String[]{properties.getPropertyName(i),properties.getPropertyValue(i)};
        }

        Arrays.sort(sorted,RelatedItemUtil.PROPERTY_NAME_COMPARATOR);
        return sorted;
    }
}
