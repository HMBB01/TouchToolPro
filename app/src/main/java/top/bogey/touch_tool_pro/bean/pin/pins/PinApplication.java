package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinApplication extends PinValue {
    private final LinkedHashMap<String, ArrayList<String>> apps = new LinkedHashMap<>();

    public PinApplication() {
        super(PinType.APP);
    }

    public PinApplication(PinSubType subType) {
        super(PinType.APP, subType);
    }

    public PinApplication(PinSubType subType, LinkedHashMap<String, ArrayList<String>> apps) {
        this(subType);
        this.apps.putAll(apps);
    }

    public PinApplication(JsonObject jsonObject) {
        super(jsonObject);
        apps.putAll(GsonUtils.getAsObject(jsonObject, "apps", TypeToken.getParameterized(LinkedHashMap.class, String.class, TypeToken.getParameterized(ArrayList.class, String.class).getType()).getType(), new LinkedHashMap<>()));
    }

    @Override
    public void newInfo() {
        apps.clear();
    }

    public boolean contain(Context context, String packageName, String activityName) {
        boolean includeActivity = activityName == null || activityName.isEmpty();
        PinApplication pinApplication = new PinApplication(includeActivity ? PinSubType.SINGLE_ACTIVITY : PinSubType.SINGLE);
        if (includeActivity) {
            pinApplication.apps.put(packageName, new ArrayList<>());
        } else {
            pinApplication.apps.put(packageName, new ArrayList<>(Collections.singletonList(activityName)));
        }
        return contain(context, pinApplication);
    }

    public boolean contain(Context context, PinApplication application) {
        LinkedHashMap<String, ArrayList<String>> checkApps = application.apps;
        if (apps.isEmpty() || checkApps.isEmpty()) return false;

        String commonPackageName = context.getString(R.string.common_package_name);
        boolean appsIncludeCommon = apps.containsKey(commonPackageName);
        boolean checkAppsIncludeCommon = checkApps.containsKey(commonPackageName);
        // 有限无法包含无限
        if (!appsIncludeCommon && checkAppsIncludeCommon) return false;

        // 无限包含无限检测，其实是检测有限的排除。需要自身的排除都在检测对象的排除中
        if (checkAppsIncludeCommon) {
            for (Map.Entry<String, ArrayList<String>> entry : apps.entrySet()) {
                // 自身有更多的排除，自身集合小于检测集合
                if (!checkApps.containsKey(entry.getKey())) return false;

                ArrayList<String> activities = entry.getValue();
                // 自身排除为空，肯定包含检测集合
                if (activities.isEmpty()) continue;

                ArrayList<String> checkActivities = checkApps.get(entry.getKey());
                // 被排除的活动为空时，但自身有排除，自身集合小于检测集合
                if (checkActivities == null || checkActivities.isEmpty()) return false;

                // 自身排除需要完全在检测集合中，才代表自身包含检测集合
                for (String activity : activities) {
                    if (!checkActivities.contains(activity)) return false;
                }
            }
        } else {
            // 无限包含有限检测，有限需要完全不在排除中
            if (appsIncludeCommon) {
                for (Map.Entry<String, ArrayList<String>> checkEntry : checkApps.entrySet()) {
                    // 如果包名一致，需要进一步检测活动
                    if (apps.containsKey(checkEntry.getKey())) {
                        ArrayList<String> activities = apps.get(checkEntry.getKey());
                        // 排除活动为空，代表排除所有
                        if (activities == null || activities.isEmpty()) return false;

                        ArrayList<String> checkActivities = checkEntry.getValue();
                        // 检测对象没有活动，因为包名在排除中，直接被排除
                        if (checkActivities.isEmpty()) return false;

                        for (String checkActivity : checkActivities) {
                            // 被检测活动在排除中
                            if (activities.contains(checkActivity)) return false;
                        }
                    }
                }
            } else {
                // 有限包含有限检测
                for (Map.Entry<String, ArrayList<String>> checkEntry : checkApps.entrySet()) {
                    // 检测对象对应的包未找到，直接不包含
                    if (!apps.containsKey(checkEntry.getKey())) return false;

                    ArrayList<String> activities = apps.get(checkEntry.getKey());
                    // 没有任何活动，代表有所有活动
                    if (activities == null || activities.isEmpty()) continue;

                    ArrayList<String> checkActivities = checkEntry.getValue();
                    // 检测对象没有活动，代表所有活动，有限无法包含无限
                    if (checkActivities.isEmpty()) return false;

                    for (String checkActivity : checkActivities) {
                        if (!activities.contains(checkActivity)) return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.AppPinColor);
    }

    @NonNull
    @Override
    public String toString() {
        return apps.toString();
    }

    public LinkedHashMap<String, ArrayList<String>> getApps() {
        return apps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinApplication that = (PinApplication) o;

        return apps.equals(that.apps);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + apps.hashCode();
        return result;
    }
}
