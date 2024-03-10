package top.bogey.touch_tool_pro.utils.super_user.shizuku;

import android.content.Context;

import androidx.annotation.Keep;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import top.bogey.touch_tool_pro.utils.super_user.CmdResult;

public class UserService extends IUserService.Stub {

    public UserService() {
    }

    @Keep
    public UserService(Context context) {
    }

    @Override
    public void destroy() {
        System.exit(0);
    }

    @Override
    public CmdResult runCommand(String cmd) {
        Process process = null;
        CmdResult result = new CmdResult();

        try {
            process = Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd});

            String line;
            StringBuilder infoBuilder = new StringBuilder();
            BufferedReader infoReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = infoReader.readLine()) != null) {
                infoBuilder.append(line).append("\n");
            }
            infoReader.close();
            result.info = infoBuilder.toString().trim();

            process.waitFor();
            result.result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result.info = e.getMessage();
        } finally {
            if (process != null) process.destroy();
        }
        return result;
    }
}
