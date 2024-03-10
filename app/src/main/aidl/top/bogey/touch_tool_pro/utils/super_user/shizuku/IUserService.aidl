// IUserService.aidl
package top.bogey.touch_tool_pro.utils.super_user.shizuku;

import top.bogey.touch_tool_pro.utils.super_user.CmdResult;

interface IUserService {
    void destroy() = 16777114;
    CmdResult runCommand(String cmd) = 1;
}