package top.bogey.touch_tool_pro.service;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.View;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool_pro.ui.setting.LogFloatView;
import top.bogey.touch_tool_pro.ui.setting.SettingView;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;

public class LogTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null && service.isServiceEnabled()) {
            KeepAliveFloatView keepView = MainApplication.getInstance().getKeepView();
            if (keepView != null) {
                View view = EasyFloat.getView(LogFloatView.class.getName());
                if (view == null) {
                    new LogFloatView(keepView.getContext()).show();
                } else {
                    EasyFloat.dismiss(LogFloatView.class.getName());
                }
            }
        }
        updateTile();
        SettingView.resetSwitchState();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    private void updateTile() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        Tile tile = getQsTile();
        if (service != null && service.isServiceEnabled()) {
            View view = EasyFloat.getView(LogFloatView.class.getName());
            tile.setState(view != null ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        } else tile.setState(Tile.STATE_UNAVAILABLE);
        tile.updateTile();
    }
}
