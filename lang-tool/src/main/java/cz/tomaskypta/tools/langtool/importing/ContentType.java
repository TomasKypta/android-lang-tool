package cz.tomaskypta.tools.langtool.importing;

/**
 * Type of content to insert
 */
public enum ContentType {
    TEXT, CDATA;

    /**
     * Detect content type based on key
     * @param key key
     * @return detected type
     */
    public static ContentType detect(String key) {
        if (key != null) {
            int index = key.indexOf("!");
            if (index != -1) {
                String type = key.substring(index + 1);
                if ("cdata".equals(type)) {
                    return CDATA;
                }
            }
        }
        return TEXT;
    }
}
