package com.alisli.intelligenthistory.settings;

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
    private String endpointURL;
    private String username;

    @Nullable
    public static JiraConfig getInstance(@NotNull Project project) {
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
        if (username == null) {
            return "";
        }
        CredentialAttributes credentialAttributes = createCredentialAttributes(username);
        return PasswordSafe.getInstance().getPassword(credentialAttributes);
    }

    public void setPassword(String password) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(username);
        PasswordSafe.getInstance().setPassword(credentialAttributes, password);
    }

    private CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("JiraConfig", key)
        );
    }
}
