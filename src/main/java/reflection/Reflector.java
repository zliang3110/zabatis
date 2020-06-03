package reflection;

import java.lang.reflect.ReflectPermission;

public class Reflector {

    public static boolean canControlMemberAccessible()
    {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (null != securityManager)
            {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        }catch (SecurityException e)
        {
            return false;
        }
       return true;
    }
}
