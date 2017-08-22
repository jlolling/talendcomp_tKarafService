package de.jlo.talendcomp.karaf.jmx;

import java.security.Permission;

public class NOSecurityManager extends SecurityManager {

    public NOSecurityManager() {
    	super();
    }

    /**
     * Deny permission to exit the VM.
     */
    @Override
	public void checkExit(int status) {}

    /**
     * Allow this security manager to be replaced, if fact, allow pretty
     * much everything.
     */
    @Override
    public void checkPermission(Permission perm) {}

}