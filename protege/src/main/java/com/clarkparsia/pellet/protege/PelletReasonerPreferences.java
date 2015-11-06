package com.clarkparsia.pellet.protege;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/**
 *
 * @author Evren Sirin
 */
public class PelletReasonerPreferences {
    private static String KEY = "com.clarkparsia.pellet.remote";
    private static PelletReasonerPreferences INSTANCE;

    public static synchronized PelletReasonerPreferences getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PelletReasonerPreferences();
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

    public void setReasonerType(PelletReasonerType type) {
        getPreferences().getString("reasonerType", type.name());
    }

    public PelletReasonerType getReasonerType() {
        return PelletReasonerType.valueOf(getPreferences().getString("serverURL", PelletReasonerType.REGULAR.name()));
    }
}