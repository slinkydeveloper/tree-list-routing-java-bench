package io.slinkydeveloper.bench;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class DummyNode implements Comparable {
    String constant_path;
    Pattern regex;

    Set<DummyNode> constant_paths;
    Set<DummyNode> regexes;

    public DummyNode(String path) {
        this(path, false);
    }

    public DummyNode(String path, boolean regex) {
        if (regex)
            this.regex = Pattern.compile(path);
        else
            this.constant_path = path;
        constant_paths = new ConcurrentSkipListSet<>();
        regexes = new ConcurrentSkipListSet<>();
    }

    public void addNode(DummyNode node) {
        if (node.isRegexPath())
            this.regexes.add(node);
        else
            this.constant_paths.add(node);
    }

    public boolean route(String pathChunk) {
        // Run this route handler
        if (pathChunk.isEmpty()) // Routing completed
            return true;
        else {
            for (DummyNode e : constant_paths) {
                // Test constant paths
                if (pathChunk.startsWith(e.getConstantPath()))
                    return e.route(pathChunk.substring(e.getConstantPath().length()));
            }
            for (DummyNode e : regexes) { // Test pattern paths
                Matcher m = e.getPathRegex().matcher(pathChunk);
                if (m.lookingAt())
                    return e.route(pathChunk.substring(m.end()));
            }
            return false;
        }
    }

    public boolean isRegexPath() {
        return regex != null;
    }

    public boolean isConstantPath() {
        return constant_path != null;
    }

    public String getConstantPath() {
        return constant_path;
    }

    public Pattern getPathRegex() {
        return regex;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public String toString() {
        return (isRegexPath()) ? regex.toString() : this.constant_path;
    }

}