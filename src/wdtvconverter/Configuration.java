package wdtvconverter;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author camon
 */
public class Configuration {
    protected Map<File, Subtitle> additionalSubs;
    protected int bitRate;

    public Configuration() {
        additionalSubs = new HashMap<File, Subtitle>();
        bitRate = - Integer.MAX_VALUE;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public boolean addSubtitle(File file, Language language,
            boolean isDefault) {
        Subtitle s = new Subtitle(file, language, isDefault);
        boolean contains = this.additionalSubs.containsKey(file);
        if (!contains) this.additionalSubs.put(file, s);
        return !contains;
    }

    public boolean removeSubtitle(File file) {
        return this.additionalSubs.remove(file) != null;
    }

    public boolean hasSubtitles() {
        return !additionalSubs.isEmpty();
    }

    public Collection<Subtitle> getSubtitles() {
        return additionalSubs.values();
    }

    public int getBitRate() {
        return bitRate;
    }

}