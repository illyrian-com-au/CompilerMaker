package au.com.illyrian.classmaker;

public class SourceNames {
    String source = null;
    String packageName = "";
    String className = "";
    String extention = "";
    
    public SourceNames() {
    }
    
    public SourceNames(String source) {
        setSource(source);
    }
    
    void extract() {
        String packageDir = "";
        String filename;
        int begin = source.lastIndexOf('/');
        if (begin >= 0) {
            packageDir = source.substring(0, begin);
            setPackageName(toDotName(packageDir));
            filename = source.substring(begin + 1);
        } else {
            filename = source;
        }
        int end = filename.indexOf('.');
        if (end >= 0) {
            setClassName(filename.substring(0, end));
            setExtention(filename.substring(end + 1));
        } else {
            setClassName(filename);
        }
    }
    
    String toDotName(String slashName) {
        return slashName.replace('/', '.');
    }
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
        extract();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getExtention() {
        return extention;
    }

    public void setExtention(String extention) {
        this.extention = extention;
    }
    
}