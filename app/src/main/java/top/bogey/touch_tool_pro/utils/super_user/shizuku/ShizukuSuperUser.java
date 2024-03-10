package top.bogey.touch_tool_pro.utils.super_user.shizuku;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.concurrent.atomic.AtomicReference;

import rikka.shizuku.Shizuku;
import top.bogey.touch_tool_pro.BuildConfig;
import top.bogey.touch_tool_pro.utils.super_user.CmdResult;
import top.bogey.touch_tool_pro.utils.super_user.ISuperUser;

public class ShizukuSuperUser implements ISuperUser {
    private final static int SHIZUKU_CODE = 16777114;
    private final static String SHIZUKU_SUFFIX = "UserService";
    private IUserService userService = null;
    private static ShizukuSuperUser superUser;

    private final Shizuku.UserServiceArgs ARGS =
            new Shizuku.UserServiceArgs(new ComponentName(BuildConfig.APPLICATION_ID, UserService.class.getName()))
                    .processNameSuffix(SHIZUKU_SUFFIX)
                    .debuggable(BuildConfig.DEBUG)
                    .version(BuildConfig.VERSION_CODE);

    private final ServiceConnection USER_SERVICE_CONNECTION = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            userService = IUserService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            userService = null;
        }
    };

    public static ShizukuSuperUser getInstance() {
        if (superUser == null) {
            superUser = new ShizukuSuperUser();
        }
        return superUser;
    }

    @Override
    public boolean init() {
        if (existShizuku()) {
            if (checkShizukuPermission()) {
                bindShizukuService();
                return true;
            } else {
                requestShizukuPermission();
            }
        }
        return false;
    }

    @Override
    public boolean tryInit() {
        if (existShizuku()) {
            if (checkShizukuPermission()) {
                bindShizukuService();
                return true;
            }
        }
        return false;
    }

    @Override
    public void exit() {
        if (existShizuku()) {
            try {
                Shizuku.unbindUserService(ARGS, USER_SERVICE_CONNECTION, true);
            } catch (Exception ignored) {
            }
        }
        userService = null;
    }

    @Override
    public CmdResult runCommand(String cmd) {
        if (userService != null) {
            try {
                return userService.runCommand(cmd);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }


    private void bindShizukuService() {
        if (userService == null) {
            if (Shizuku.getVersion() < 12) {
                Shizuku.bindUserService(ARGS, USER_SERVICE_CONNECTION);
            } else {
                int version = Shizuku.peekUserService(ARGS, USER_SERVICE_CONNECTION);
                if (version == -1) {
                    Shizuku.bindUserService(ARGS, USER_SERVICE_CONNECTION);
                }
            }
        }
    }


    private void requestShizukuPermission() {
        if (!checkShizukuPermission()) {
            AtomicReference<Shizuku.OnRequestPermissionResultListener> listener = new AtomicReference<>();
            listener.set((requestCode, grantResult) -> Shizuku.removeRequestPermissionResultListener(listener.get()));
            Shizuku.addRequestPermissionResultListener(listener.get());
            Shizuku.requestPermission(SHIZUKU_CODE);
        }
    }

    private boolean checkShizukuPermission() {
        if (existShizuku()) {
            return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public static boolean existShizuku() {
        return Shizuku.pingBinder();
    }
}
