package top.bogey.touch_tool_pro.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNodeManager;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionMap;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.function.FunctionPinsAction;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.DialogSelectActionBinding;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.super_user.SuperUser;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;

@SuppressLint("ViewConstructor")
public class SelectActionDialog extends FrameLayout {
    private final LinkedHashMap<ActionMap, ArrayList<Object>> types;

    public SelectActionDialog(@NonNull Context context, CardLayoutView layoutView, Class<? extends PinObject> pinClass, boolean out) {
        super(context);
        DialogSelectActionBinding binding = DialogSelectActionBinding.inflate(LayoutInflater.from(context), this, true);

        Collator collator = Collator.getInstance(Locale.CHINA);
        types = new LinkedHashMap<>();

        ArrayList<Object> customFunctions = new ArrayList<>();
        ArrayList<Object> variables = new ArrayList<>();

        LinkedHashMap<String, FunctionPinsAction> actions = SaveRepository.getInstance().getAllFunctionActions();
        actions.forEach((id, action) -> {
            if (matchAction(action, pinClass, out)) {
                customFunctions.add(id);
            }
        });

        HashMap<String, PinValue> allVariables = SaveRepository.getInstance().getAllVariables();
        allVariables.forEach((key, value) -> {
            if (value.getClass().isAssignableFrom(pinClass) || pinClass.isAssignableFrom(value.getClass())) {
                variables.add(new VariableInfo(key, value, 1, out));
            }
        });

        FunctionContext functionContext = layoutView.getFunctionContext();
        if (functionContext instanceof Function function) {
            functionContext = function.getParent();

            function.getVars().forEach((key, value) -> {
                if (value.getClass().isAssignableFrom(pinClass) || pinClass.isAssignableFrom(value.getClass())) {
                    variables.add(new VariableInfo(key, value, 3, out));
                }
            });
        }
        if (functionContext instanceof Task task) {
            ArrayList<Object> objects = new ArrayList<>();
            task.getFunctions().forEach(function -> {
                if (matchAction(function.getAction(), pinClass, out)) objects.add(function);
            });
            objects.sort((o1, o2) -> collator.compare(((Function) o1).getTitle(), ((Function) o2).getTitle()));
            customFunctions.addAll(objects);

            task.getVars().forEach((key, value) -> {
                if (value.getClass().isAssignableFrom(pinClass) || pinClass.isAssignableFrom(value.getClass())) {
                    variables.add(new VariableInfo(key, value, 2, out));
                }
            });
        }

        if (!customFunctions.isEmpty()) {
            types.put(ActionMap.CUSTOM, customFunctions);
        }

        HashMap<ActionType, Action> tmpActions = layoutView.getCacheActions();
        for (ActionMap actionMap : ActionMap.values()) {
            ArrayList<Object> actionTypes = new ArrayList<>();
            for (ActionType actionType : actionMap.getTypes()) {
                if (!SuperUser.isSuperUser() && actionType.getConfig().isSuperAction()) continue;
                Action action = tmpActions.get(actionType);
                if (action == null) continue;
                if (matchAction(action, pinClass, out)) {
                    actionTypes.add(actionType);
                }
            }
            if (actionTypes.isEmpty()) continue;
            types.put(actionMap, actionTypes);
        }

        if (!variables.isEmpty()) {
            variables.sort((o1, o2) -> collator.compare(((VariableInfo) o1).key, ((VariableInfo) o2).key));
            types.put(ActionMap.VARIABLE, variables);
        }

        ArrayList<Object> cards = new ArrayList<>();
        layoutView.getCardMap().forEach((id, card) -> {
            if (matchAction(card.getAction(), pinClass, out)) {
                cards.add(card);
            }
        });

        if (!cards.isEmpty()) {
            cards.sort((o1, o2) -> {
                ActionCard<?> card1 = (ActionCard<?>) o1;
                ActionCard<?> card2 = (ActionCard<?>) o2;
                if (card1.getAction().getY() == card2.getAction().getY()) {
                    return card2.getAction().getX() - card1.getAction().getX();
                } else {
                    return card2.getAction().getY() - card1.getAction().getY();
                }
            });
            types.put(ActionMap.EXIST_CARD, cards);
        }

        SelectActionTreeAdapter adapter = new SelectActionTreeAdapter(layoutView, new TreeNodeManager(), types);
        binding.activityBox.setAdapter(adapter);
    }

    private boolean matchAction(Action action, Class<? extends PinObject> pinClass, boolean out) {
        for (Pin pin : action.getPins()) {
            if ((pin.getPinClass().isAssignableFrom(pinClass) || pinClass.isAssignableFrom(pin.getPinClass())) && pin.isOut() == out) return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return types.isEmpty();
    }

    static class VariableInfo {
        final String key;
        final PinValue value;
        final int from; // 1 = common, 2 = task, 3 = function
        final boolean out;

        public VariableInfo(String key, PinValue value, int from, boolean out) {
            this.key = key;
            this.value = value;
            this.from = from;
            this.out = out;
        }
    }
}
