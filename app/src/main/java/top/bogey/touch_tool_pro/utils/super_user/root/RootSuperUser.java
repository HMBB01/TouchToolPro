package top.bogey.touch_tool_pro.utils.super_user.root;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import top.bogey.touch_tool_pro.utils.super_user.CmdResult;
import top.bogey.touch_tool_pro.utils.super_user.ISuperUser;

public class RootSuperUser implements ISuperUser {
    private static boolean existRoot = false;
    private static RootSuperUser superUser;

    public static RootSuperUser getInstance() {
        if (superUser == null) {
            superUser = new RootSuperUser();
        }
        return superUser;
    }

    @Override
    public boolean init() {
        return existRoot();
    }

    @Override
    public boolean tryInit() {
        return existRoot();
    }

    @Override
    public void exit() {
    }

    @Override
    public CmdResult runCommand(String cmd) {
        CmdResult result = new CmdResult();
        if (existRoot) {
            Process process = null;

            try {
                process = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});

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
        }

        return result;
    }

    public static boolean existRoot() {
        if (existRoot) return true;

        Process process = null;
        OutputStreamWriter writer = null;
        try {
            process = Runtime.getRuntime().exec("su");
            writer = new OutputStreamWriter(process.getOutputStream());
            writer.write("exit\n");
            writer.flush();
            int value = process.waitFor();
            existRoot = value == 0;
            return existRoot;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) process.destroy();
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
