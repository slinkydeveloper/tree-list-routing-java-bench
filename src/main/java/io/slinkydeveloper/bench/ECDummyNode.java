package io.slinkydeveloper.bench;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class ECDummyNode implements Comparable {
    String constant_path;
    Pattern regex;

    MutableList<ECDummyNode> constant_paths;
    MutableList<ECDummyNode> regexes;

    public ECDummyNode(String path) {
        this(path, false);
    }

    public ECDummyNode(String path, boolean regex) {
        if (regex)
            this.regex = Pattern.compile(path);
        else
            this.constant_path = path;
        constant_paths = new FastList<>();
        constant_paths = constant_paths.asSynchronized();
        regexes = new FastList<>();
        regexes = regexes.asSynchronized();
    }

    public void addNode(ECDummyNode node) {
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
            for (ECDummyNode e : constant_paths) {
                // Test constant paths
                if (pathChunk.startsWith(e.getConstantPath()))
                    return e.route(pathChunk.substring(e.getConstantPath().length()));
            }
            for (ECDummyNode e : regexes) { // Test pattern paths
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