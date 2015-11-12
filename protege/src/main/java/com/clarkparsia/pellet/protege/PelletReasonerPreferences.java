package com.clarkparsia.pellet.protege;

import java.util.Objects;

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

    private final Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(KEY);

    private PelletReasonerMode reasonerMode;
    private String serverURL;
    private int explanationCount;

    private boolean updated = false;

    private PelletReasonerPreferences() {
        _load();
    }

    private void _load() {
        reasonerMode = PelletReasonerMode.valueOf(prefs.getString("reasonerMode", PelletReasonerMode.REGULAR.name()));
        serverURL = prefs.getString("serverURL", "http://localhost:18080");
        explanationCount = prefs.getInt("explanationCount", 0);
    }

    private void _save() {
        prefs.putString("reasonerMode", reasonerMode.name());
        prefs.putString("serverURL", serverURL);
        prefs.putInt("explanationCount", explanationCount);
    }

    public boolean save() {
        if (!updated) {
            return false;
        }

        updated = false;

        _save();

        return true;
    }

    private void update(Object oldValue, Object newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            updated = true;
        }
    }

    public PelletReasonerMode getReasonerMode() {
        return reasonerMode;
    }

    public void setReasonerMode(final PelletReasonerMode Mode) {
        update(reasonerMode, Mode);
        reasonerMode = Mode;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(final String url) {
        update(serverURL, url);
        serverURL = url;
    }

    public int getExplanationCount() {
        return explanationCount;
    }

    public void setExplanationCount(int theExplanationCount) {
        update(explanationCount, theExplanationCount);
        explanationCount = theExplanationCount;
    }
}