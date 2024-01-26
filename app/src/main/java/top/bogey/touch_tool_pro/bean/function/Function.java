package top.bogey.touch_tool_pro.bean.function;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.UUID;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionExecuteInterface;
import top.bogey.touch_tool_pro.bean.action.ActionInterface;
import top.bogey.touch_tool_pro.bean.action.function.FunctionEndAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionInnerAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionPinsAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionReferenceAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionStartAction;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.base.IdentityInfo;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class Function extends FunctionContext implements ActionExecuteInterface {
    // 方法对外针脚
    private final FunctionPinsAction action;
    // 方法拥有者id
    private String parentId;

    // 仅获取针脚值
    private boolean justCall = false;
    // 快速结束，意味着执行到 FunctionEndAction 后内部逻辑会被限制
    private boolean fastEnd = true;

    private transient FunctionEndAction endAction;
    private transient FunctionReferenceAction executeAction;
    private transient FunctionContext outContext;

    public Function() {
        super(FunctionType.FUNCTION);
        action = new FunctionPinsAction();
        addAction(new FunctionStartAction(this));
        addAction(new FunctionEndAction(this));
    }

    public Function(JsonObject jsonObject) {
        super(jsonObject);
        action = (FunctionPinsAction) GsonUtils.getAsObject(jsonObject, "action", Action.class, null);
        parentId = GsonUtils.getAsString(jsonObject, "parentId", null);
        justCall = GsonUtils.getAsBoolean(jsonObject, "justCall", false);
        fastEnd = GsonUtils.getAsBoolean(jsonObject, "fastEnd", true);

        for (ActionInterface functionAction : getActionsByClass(FunctionInnerAction.class)) {
            ((FunctionInnerAction) functionAction).setOwner(this);
        }
    }

    public Function(Task task) {
        this();
        setTitle(task.getTitle());
        Task copy = (Task) task.copy();
        for (Action startAction : copy.getActionsByClass(StartAction.class)) {
            for (Pin pin : startAction.getPins()) {
                pin.cleanLinks(copy);
            }
        }

        for (Action taskAction : copy.getActions()) {
            if (!(taskAction instanceof StartAction)) {
                addAction(taskAction);
            }
        }
        getVars().forEach((key, value) -> {
            PinValue pinValue = (PinValue) value.copy();
            pinValue.newInfo();
            getVars().put(key, pinValue);
        });
    }

    public Function(Function function, FunctionReferenceAction executeAction, FunctionContext outContext) {
        super(FunctionType.FUNCTION);
        action = new FunctionPinsAction();
        parentId = function.getParentId();
        justCall = function.isJustCall();
        fastEnd = function.isFastEnd();
        for (Action act : function.getActions()) {
            addAction((Action) act.copy());
        }
        function.getVars().forEach((key, value) -> {
            PinValue copy = (PinValue) value.copy();
            copy.newInfo();
            getVars().put(key, copy);
        });

        this.executeAction = executeAction;
        this.outContext = outContext;
    }

    @Override
    public IdentityInfo copy() {
        return GsonUtils.copy(this, FunctionContext.class);
    }

    @Override
    public void newInfo() {
        setId(UUID.randomUUID().toString());
        action.newInfo();
    }

    @Override
    public void addAction(Action action) {
        // 方法动作需要设置拥有者
        if (action instanceof FunctionInnerAction) {
            ((FunctionInnerAction) action).setOwner(this);
        }
        // 开始动作只能有一个
        if (action instanceof FunctionStartAction) {
            ArrayList<Action> actions = getActionsByClass(FunctionStartAction.class);
            if (actions.size() > 0) return;
        }
        super.addAction(action);
    }

    @Override
    public void removeAction(Action action) {
        if (action instanceof FunctionInnerAction) {
            ArrayList<Action> actions = getActionsByClass(action.getClass());
            if (actions.size() <= 1) return;
        }
        super.removeAction(action);
    }

    @Override
    public void save() {
        action.syncDefaultValue(this);
        FunctionContext parent = getParent();
        if (parent != null) parent.save();
        else SaveRepository.getInstance().saveFunction(this);
    }

    @Override
    public FunctionContext getParent() {
        if (parentId == null) return null;
        if (outContext != null) return outContext;
        return SaveRepository.getInstance().getTaskById(parentId);
    }

    @Override
    public boolean isEnd() {
        return fastEnd && endAction != null;
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        Function function = (Function) context;
        function.endAction = null;
        ArrayList<Action> startActions = context.getActionsByClass(FunctionStartAction.class);
        if (startActions.size() > 0) {
            FunctionStartAction startAction = (FunctionStartAction) startActions.get(0);
            startAction.executeNext(runnable, context, startAction.getPinByUid(pin.getUid()));
        }
    }

    @Override
    public void executeNext(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (justCall) return;
        Function function = (Function) context;
        function.executeAction.executeNext(runnable, context, pin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (!justCall) return;

        Function function = (Function) context;
        function.endAction = null;
        ArrayList<Action> startActions = context.getActionsByClass(FunctionStartAction.class);
        if (startActions.size() > 0) {
            FunctionStartAction startAction = (FunctionStartAction) startActions.get(0);
            for (Pin actionPin : startAction.getPins()) {
                if (actionPin.isSameValueType(PinExecute.class)) {
                    startAction.executeNext(runnable, context, actionPin);
                    return;
                }
            }
        }
    }

    @Override
    public PinObject getPinValue(TaskRunnable runnable, FunctionContext context, Pin pin) {
        Function function = (Function) context;
        Pin pinByUid = function.executeAction.getPinByUid(pin.getUid());
        return function.executeAction.getPinValue(runnable, outContext, pinByUid);
    }

    public ArrayList<Pin> getPins() {
        ArrayList<Pin> pins = new ArrayList<>(action.getPins());
        if (justCall) {
            for (int i = pins.size() - 1; i >= 0; i--) {
                Pin pin = pins.get(i);
                if (pin.isSameValueType(PinExecute.class)) pins.remove(pin);
            }
        }
        return pins;
    }

    public FunctionPinsAction getAction() {
        return action;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isJustCall() {
        return justCall;
    }

    public void setJustCall(boolean justCall) {
        this.justCall = justCall;
        if (justCall) {
            // 仅获取针脚值的时候只能有一条执行针脚，UI上开始动作限制添加执行
            getActionsByClass(FunctionStartAction.class).forEach(action -> {
                boolean skip = true;
                ArrayList<Pin> pins = new ArrayList<>();
                for (Pin pin : action.getPins()) {
                    if (pin.isSameValueType(PinExecute.class)) {
                        if (skip) skip = false;
                        else pins.add(pin);
                    }
                }
                for (Pin pin : pins) {
                    action.removePin(pin, this);
                }
            });
        }
    }

    public boolean isFastEnd() {
        return fastEnd;
    }

    public void setFastEnd(boolean fastEnd) {
        this.fastEnd = fastEnd;
    }

    public FunctionEndAction getEndAction() {
        return endAction;
    }

    public void setEndAction(FunctionEndAction endAction) {
        this.endAction = endAction;
    }

    public FunctionContext getOutContext() {
        return outContext;
    }
}
