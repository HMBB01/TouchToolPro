package top.bogey.touch_tool_pro.bean.task;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.function.FunctionReferenceAction;
import top.bogey.touch_tool_pro.bean.action.normal.CaptureSwitchAction;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.base.IdentityInfo;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.function.FunctionType;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class Task extends FunctionContext {
    private final long createTime;
    // 配置的动作
    private final HashSet<Function> functions = new HashSet<>();

    public Task() {
        super(FunctionType.TASK);
        createTime = System.currentTimeMillis();
    }

    public Task(FunctionType type) {
        super(type);
        createTime = System.currentTimeMillis();
    }

    public Task(JsonObject jsonObject) {
        super(jsonObject);
        functions.addAll(GsonUtils.getAsObject(jsonObject, "functions", TypeToken.getParameterized(HashSet.class, FunctionContext.class).getType(), new HashSet<>()));
        createTime = GsonUtils.getAsLong(jsonObject, "createTime", System.currentTimeMillis());
    }

    @Override
    public IdentityInfo copy() {
        try {
            return GsonUtils.copy(this, FunctionContext.class);
        } catch (Exception | Error error) {
            error.printStackTrace();
        }
        return null;
    }

    @Override
    public void newInfo() {
        setId(UUID.randomUUID().toString());
        getFunctions().forEach(function -> function.setParentId(getId()));
        getFunctions().forEach(function -> function.getActions().forEach(action -> {
            if (action instanceof FunctionReferenceAction referenceAction) {
                if (referenceAction.getParentId() != null && !referenceAction.getParentId().isEmpty()) ((FunctionReferenceAction) action).setParentId(getId());
            }
        }));
        getActions().forEach(action -> {
            if (action instanceof FunctionReferenceAction referenceAction) {
                if (referenceAction.getParentId() != null && !referenceAction.getParentId().isEmpty()) ((FunctionReferenceAction) action).setParentId(getId());
            }
        });
    }

    @Override
    public ActionCheckResult check() {
        ActionCheckResult result = super.check();
        if (result.type == ActionCheckResult.ActionResultType.ERROR) return result;

        for (Function function : getFunctions()) {
            ActionCheckResult functionResult = function.check();
            if (functionResult.type == ActionCheckResult.ActionResultType.ERROR) return functionResult;
        }
        return result;
    }

    @Override
    public void save() {
        SaveRepository.getInstance().saveTask(this);
    }

    @Override
    public FunctionContext getParent() {
        return null;
    }


    public HashSet<Function> getFunctions() {
        return functions;
    }

    public ArrayList<String> getAllFunctionTags() {
        ArrayList<String> functionTags = SaveRepository.getInstance().getFunctionTags();
        HashSet<String> tags = new HashSet<>();
        boolean existNoTag = false;
        boolean needSave = false;
        for (Function function : functions) {
            if (function.getTags() != null) {
                HashSet<String> set = new HashSet<>(function.getTags());
                for (String tag : set) {
                    if (!functionTags.contains(tag)) {
                        function.removeTag(tag);
                        needSave = true;
                    }
                }
            }

            if (function.getTags() == null || function.getTags().isEmpty()) existNoTag = true;
            else {
                tags.addAll(function.getTags());
            }
        }
        ArrayList<String> list = new ArrayList<>(tags);
        if (existNoTag || functions.isEmpty()) list.add(SaveRepository.NO_TAG);

        if (needSave) save();

        return list;
    }

    public ArrayList<Function> getFunctionsByTag(String tag) {
        ArrayList<Function> functions = new ArrayList<>();
        for (Function function : getFunctions()) {
            HashSet<String> tags = function.getTags();
            if (tags != null && tags.contains(tag)) {
                functions.add(function);
                continue;
            }
            if (tag == null || tag.isEmpty() || SaveRepository.NO_TAG.equals(tag)) {
                if (tags == null || tags.isEmpty()) functions.add(function);
            }
        }
        return functions;
    }

    public void addFunction(Function function) {
        function.setParentId(getId());
        getFunctions().add(function);
    }

    public void removeFunction(Function function) {
        getFunctions().remove(function);
    }

    public Function getFunctionById(String functionId) {
        for (Function function : getFunctions()) {
            if (function.getId().equals(functionId)) return function;
        }
        return null;
    }

    public boolean isEnable() {
        for (Action action : getActionsByClass(StartAction.class)) {
            if (((StartAction) action).isEnable()) return true;
        }
        return false;
    }

    public boolean needCaptureService() {
        ArrayList<Action> captureActions = getActionsByClass(CaptureSwitchAction.class);
        if (captureActions.size() > 0) return false;
        for (Action action : getActions()) {
            if (action.needCapture) return true;
        }
        return false;
    }

    public long getCreateTime() {
        return createTime;
    }
}
