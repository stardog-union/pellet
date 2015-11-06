package com.clarkparsia.protege.plugin.remote;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/**
 *
 * @author Evren Sirin
 */
public class PelletRemoteReasonerPreferences {
    private static String KEY = "com.clarkparsia.pellet.remote";
    private static PelletRemoteReasonerPreferences INSTANCE;

    public static synchronized PelletRemoteReasonerPreferences getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PelletRemoteReasonerPreferences();
        }
        return INSTANCE;
    }

    private Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(KEY);
    }

    public void setServerURL(String url) {
        getPreferences().getString("serverURL", url);
    }

    public String getServerURL() {
        return getPreferences().getString("serverURL", "http://localhost:18080");
    }

}