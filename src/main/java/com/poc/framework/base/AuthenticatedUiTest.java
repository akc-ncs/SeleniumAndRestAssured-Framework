package com.poc.framework.base;

import com.poc.framework.auth.AuthSession;
import org.testng.annotations.BeforeMethod;

/**
 * Extend this instead of BaseUiTest when a test needs a logged-in user.
 * The browser starts in BaseUiTest.setUp (superclass @BeforeMethod runs first), then the
 * saved session is restored, so the test begins already logged in.
 *
 * Keep tests that exercise login/logout (or change the session) on BaseUiTest.
 */
public abstract class AuthenticatedUiTest extends BaseUiTest {

    @BeforeMethod(alwaysRun = true)
    public void restoreSession() {
        AuthSession.ensureLoggedIn();
    }
}
