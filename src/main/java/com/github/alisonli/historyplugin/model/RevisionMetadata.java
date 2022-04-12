package com.github.alisonli.historyplugin.model;

public class RevisionMetadata {
    private final String beforeContent;
    private final String afterContent;
    private int docs;
    private int annotations;
    private int imports;
    private int newlines;
    private int other;

    public RevisionMetadata(String beforeContent, String afterContent) {
        this.beforeContent = beforeContent;
        this.afterContent = afterContent;
    }

    public void setDocs(int docs) {
        this.docs = docs;
    }

    public void setAnnotations(int annotations) {
        this.annotations = annotations;
    }

    public void setImports(int imports) {
        this.imports = imports;
    }

    public void setNewlines(int newlines) {
        this.newlines = newlines;
    }

    public void setOther(int other) {
        this.other = other;
    }

    public int getDocs() {
        return docs;
    }

    public int getAnnotations() {
        return annotations;
    }

    public int getImports() {
        return imports;
    }

    public int getNewlines() {
        return newlines;
    }

    public int getOther() {
        return other;
    }
}
