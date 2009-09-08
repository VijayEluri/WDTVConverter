package wdtvconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author camon
 */
public class Constants {
    private Constants() {};

    public static final int BITRATE_192 = 192;
    public static final int BITRATE_286 = 286;
    public static final int BITRATE_384 = 384;
    public static final int BITRATE_448 = 448;
    public static final int BITRATE_640 = 640;

    public static String tempDir =
            WDTVConverterApp.USER_HOME + "\\" + WDTVConverterApp.SETTINGS_DIR;

    public static final Map<String, Language> LANGUAGES = loadLanguages();

    private static Map<String, Language> loadLanguages() {
        try {
            FileInputStream fis = new FileInputStream(
                (new File(WDTVConverterApp.APP_LOCATION.getFile()).getParent() +
                "\\langs.txt").replace("%20", " "));
            Scanner sc = new Scanner(fis);
            int count = 0;
            Map<String, Integer> map = new HashMap<String,Integer>();
            Map<String, Language> result = new HashMap<String, Language>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (++count >= 3) {
                    StringTokenizer st = new StringTokenizer(line,"\t\r\n|");
                    if (st.countTokens() < 2) continue;
                    String name = st.nextToken().trim();
                    String code2 = st.nextToken().trim();
                    String code2U = code2.toUpperCase();
                    String code1 = null;
                    if (st.hasMoreTokens()) code1 = st.nextToken().trim();
                    if (code1 != null && code1.isEmpty()) code1 = null;
                    Integer value = map.get(code2U);
                    if (value == null) value = new Integer(0);
                    map.put(code2U, ++value);
                    result.put(code2U + (value > 1 ? value : "")
                            ,new Language(name, code2, code1));
                }
            }
            return result;
        } catch(FileNotFoundException e) {
            return new HashMap<String, Language>();
        }
    }
}
