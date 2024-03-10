package top.bogey.touch_tool_pro.utils.super_user;

public interface ISuperUser {

    boolean init();

    boolean tryInit();

    void exit();

    CmdResult runCommand(String cmd);
}
