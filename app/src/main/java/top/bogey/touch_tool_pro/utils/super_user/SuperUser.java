package top.bogey.touch_tool_pro.utils.super_user;

import top.bogey.touch_tool_pro.utils.super_user.root.RootSuperUser;
import top.bogey.touch_tool_pro.utils.super_user.shizuku.ShizukuSuperUser;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class SuperUser {
    private static ISuperUser SUPER_USER = null;

    public static boolean init() {
        ISuperUser superUser = getSuperUser();
        if (superUser == null) return false;
        if (superUser.init()) {
            SUPER_USER = superUser;
            return true;
        }
        return false;
    }

    public static void tryInit() {
        exit();
        ISuperUser superUser = getSuperUser();
        if (superUser == null) return;
        if (superUser.tryInit()) {
            SUPER_USER = superUser;
        }
    }

    public static void exit() {
        if (SUPER_USER != null) SUPER_USER.exit();
        SUPER_USER = null;
    }

    public static boolean isSuperUser() {
        return SUPER_USER != null;
    }

    public static int getSuperUserType() {
        if (SUPER_USER == null) return 0;
        if (SUPER_USER instanceof ShizukuSuperUser) return 1;
        if (SUPER_USER instanceof RootSuperUser) return 2;
        return -1;
    }

    public static CmdResult runCommand(String cmd) {
        if (SUPER_USER != null) {
            return SUPER_USER.runCommand(cmd);
        }
        return null;
    }

    private static ISuperUser getSuperUser() {
        int type = SettingSave.getInstance().getSuperUserType();
        return switch (type) {
            case 1 -> ShizukuSuperUser.getInstance();
            case 2 -> RootSuperUser.getInstance();
            default -> null;
        };
    }
}
