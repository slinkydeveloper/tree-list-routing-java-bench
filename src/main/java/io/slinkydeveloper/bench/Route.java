package io.slinkydeveloper.bench;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class Route implements Comparable {

    String path;
    Pattern regex;

    public Route(String path) {
        this(path, false);
    }

    public Route(String path, boolean regex) {
        if (regex)
            this.regex = Pattern.compile(path);
        else
            this.path = path;
    }

    public boolean route(String path) {
        if (isRegexPath()) {
            Matcher m = regex.matcher(path);
            if (m.lookingAt()) {
                // Run handler
                if (m.end() == path.length()) // If perfectly match, stop routing
                    return true;
            }
        } else {
            if (path.startsWith(this.path)) {
                // Run handler
                if (this.path.equals(path)) // If perfectly match, stop routing
                    return true;
            }
        }
        return false;
    }

    boolean isRegexPath() {
        return regex != null;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public String toString() {
        return (isRegexPath()) ? regex.toString() : this.path;
    }
}
