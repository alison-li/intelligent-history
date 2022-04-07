package com.github.alisonli.historyplugin.model;

import com.intellij.openapi.vcs.history.VcsRevisionNumber;

public class RevisionMetadata {
    private final VcsRevisionNumber revisionNumber;
    private final int docs;
    private final int annotations;
    private final int imports;
    private final int newlines;
    private final int other;

    public RevisionMetadata(VcsRevisionNumber revisionNumber, int numDoc, int annotations, int imports,
                            int newlines, int other, int others) {
        this.revisionNumber = revisionNumber;
        this.docs = numDoc;
        this.annotations = annotations;
        this.imports = imports;
        this.newlines = newlines;
        this.other = other;
    }
}
