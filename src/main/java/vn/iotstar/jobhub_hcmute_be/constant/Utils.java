package vn.iotstar.jobhub_hcmute_be.constant;

public class Utils {
    public static String cleanHTML(String dirtyHTML) {
        if (dirtyHTML != null) {
            String cleanedHTML = dirtyHTML.replace("&lt;", "<").replace("&gt;", ">");
            return cleanedHTML;
        } else {
            return null;
        }
    }

}
