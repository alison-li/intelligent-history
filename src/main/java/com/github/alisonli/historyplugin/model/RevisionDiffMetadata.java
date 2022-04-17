package com.github.alisonli.historyplugin.model;

public class RevisionDiffMetadata {
    private final String beforeContent;
    private final String afterContent;
    private int docs = 0;
    private int annotations = 0;
    private int imports = 0;
    private int newlines = 0;
    private int other = 0;

    public RevisionDiffMetadata(String beforeContent, String afterContent) {
        this.beforeContent = beforeContent;
        this.afterContent = afterContent;
    }

    public RevisionDiffMetadata(String beforeContent, String afterContent, int docs, int annotations, int imports,
                                int newlines, int other) {
        this.beforeContent = beforeContent;
        this.afterContent = afterContent;
        this.docs = docs;
        this.annotations = annotations;
        this.imports = imports;
        this.newlines = newlines;
        this.other = other;
    }

    public void mergeMetadata(RevisionDiffMetadata anotherDiffMetadata) {
        this.docs += anotherDiffMetadata.getDocs();
        this.annotations += anotherDiffMetadata.getAnnotations();
        this.imports += anotherDiffMetadata.getImports();
        this.newlines += anotherDiffMetadata.getNewlines();
        this.other += anotherDiffMetadata.getOther();
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

    @Override
    public String toString() {
        String docsFormatted = String.format("Documentation: %d", this.getDocs());
        String annotationsFormatted = String.format("Annotation: %d", this.getAnnotations());
        String importsFormatted = String.format("Import: %d", this.getImports());
        String newlinesFormatted = String.format("Newline: %d", this.getNewlines());
        String otherFormatted = String.format("Other: %d", this.getOther());
        return String.format("%s <br/> %s <br/> %s <br/> %s <br/> %s", docsFormatted, annotationsFormatted, importsFormatted,
                newlinesFormatted, otherFormatted);
    }
}