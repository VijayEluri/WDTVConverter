package wdtvconverter;

/**
 *
 * @author camon
 */
public class Language {

    private String name;
    private String ISO6392code;
    private String ISO6391code;

    public Language(String name, String ISO6392code, String ISO6391code) {
        this.name = name;
        this.ISO6391code = ISO6391code;
        this.ISO6392code = ISO6392code;
    }

    public String getISO6391code() {
        return ISO6391code;
    }

    public void setISO6391code(String ISO6391code) {
        this.ISO6391code = ISO6391code;
    }

    public String getISO6392code() {
        return ISO6392code;
    }

    public void setISO6392code(String ISO6392code) {
        this.ISO6392code = ISO6392code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ISO6392code + " (" + name + ")";
    }
}
