package com.alisli.intelligenthistory.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "RegexConfig",
        storages = {@Storage("regex-config.xml")}
)
public class RegexConfig implements PersistentStateComponent<RegexConfig> {
    private String documentationPattern = "\\s*\\W(\\*|/\\*|//)(.*)";
    private String importPattern = "import(.*)";
    private String annotationPattern = "\\s*@(Deprecated|Suppress[A-Za-z]*)(.*)";

    @Nullable
    public static RegexConfig getInstance(@NotNull Project project) {
        return project.getService(RegexConfig.class);
    }

    @Override
    public @Nullable RegexConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull RegexConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getDocumentationPattern() {
        return this.documentationPattern;
    }

    public void setDocumentationPattern(String documentationPattern) {
        this.documentationPattern = documentationPattern;
    }

    public String getImportPattern() {
        return this.importPattern;
    }

    public void setImportPattern(String importPattern) {
        this.importPattern = importPattern;
    }

    public String getAnnotationPattern() {
        return this.annotationPattern;
    }

    public void setAnnotationPattern(String annotationPattern) {
        this.annotationPattern = annotationPattern;
    }
}