package com.github.alisonli.historyplugin.settings;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "JiraConfig",
        storages = {@Storage("jira-config.xml")}
)
public class JiraConfig implements PersistentStateComponent<JiraConfig> {
    private static final String DEFAULT_ENDPOINT_URL = "https://issues.apache.org/jira/";
    private String endpointURL = DEFAULT_ENDPOINT_URL;
    private String username;

    @Nullable
    public static JiraConfig getInstance(Project project) {
        return project.getService(JiraConfig.class);
    }

    @Override
    public @Nullable JiraConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull JiraConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getEndpointURL() {
        return this.endpointURL;
    }

    public void setEndpointURL(String endpointURL) {
        this.endpointURL = endpointURL;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Transient
    public String getPassword() {
        CredentialAttributes credentialAttributes = createCredentialAttributes(this.endpointURL);
        return PasswordSafe.getInstance().getPassword(credentialAttributes);
    }

    public void setPassword(String password) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(this.endpointURL);
        PasswordSafe.getInstance().setPassword(credentialAttributes, password);
    }

    private CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("JiraConfig", key)
        );
    }
}
