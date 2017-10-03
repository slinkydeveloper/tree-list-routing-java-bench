package io.slinkydeveloper.bench;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class ImmutableECDummyNode implements Comparable {
    String constant_path;
    Pattern regex;

    MutableList<ImmutableECDummyNode> constant_paths_construction;
    MutableList<ImmutableECDummyNode> regexes_construction;

    ImmutableList<ImmutableECDummyNode> constant_paths;
    ImmutableList<ImmutableECDummyNode> regexes;

    public ImmutableECDummyNode(String path) {
        this(path, false);
    }

    public ImmutableECDummyNode(String path, boolean regex) {
        if (regex)
            this.regex = Pattern.compile(path);
        else
            this.constant_path = path;
        constant_paths_construction = Lists.mutable.empty();
        constant_paths_construction = constant_paths_construction.asSynchronized();
        regexes_construction = Lists.mutable.empty();
        regexes_construction = regexes_construction.asSynchronized();
    }

    public void addNode(ImmutableECDummyNode node) {
        if (node.isRegexPath())
            this.regexes_construction.add(node);
        else
            this.constant_paths_construction.add(node);
    }

    public boolean route(String pathChunk) {
        // Run this route handler
        if (pathChunk.isEmpty()) // Routing completed
            return true;
        else {
            for (ImmutableECDummyNode e : constant_paths) {
                // Test constant paths
                if (pathChunk.startsWith(e.getConstantPath()))
                    return e.route(pathChunk.substring(e.getConstantPath().length()));
            }
            for (ImmutableECDummyNode e : regexes) { // Test pattern paths
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

    public void setImmutable() {
        constant_paths = constant_paths_construction.toImmutable();
        regexes = regexes_construction.toImmutable();
        for (ImmutableECDummyNode e : constant_paths) {
            e.setImmutable();
        }
        for (ImmutableECDummyNode e : regexes) {
            e.setImmutable();
        }
    }
}