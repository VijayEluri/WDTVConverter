package wdtvconverter;

import java.io.File;

/**
 *
 * @author camon
 */
public class Subtitle {

    private File file;
    private Language language;
    private boolean isDefault;

    public Subtitle(File file, Language language, boolean isDefault) {
        this.file = file;
        this.language = language;
        this.isDefault = isDefault;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Subtitle other = (Subtitle) obj;
        if (this.file != other.file && (this.file == null
                || !this.file.equals(other.file))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.file != null ? this.file.hashCode() : 0);
        return hash;
    }

    
}
