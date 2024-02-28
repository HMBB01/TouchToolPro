package top.bogey.touch_tool_pro.bean.action.number;

import com.google.gson.JsonObject;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionMorePinInterface;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinFloat;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class MathExpressionAction extends Action implements ActionMorePinInterface {
    protected transient Pin resultPin = new Pin(new PinFloat(), R.string.action_math_expression_subtitle_result, true);
    protected transient Pin expressionPin = new Pin(new PinString(PinSubType.AUTO_PIN), R.string.action_math_expression_subtitle_expression);

    public MathExpressionAction() {
        super(ActionType.MATH_EXPRESSION);
        resultPin = addPin(resultPin);
        expressionPin = addPin(expressionPin);
    }

    public MathExpressionAction(JsonObject jsonObject) {
        super(jsonObject);
        resultPin = reAddPin(resultPin);
        expressionPin = reAddPin(expressionPin);
        reAddPin(new Pin(new PinFloat()), 0);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinString expression = (PinString) getPinValue(runnable, context, expressionPin);
        String expressionValue = expression.getValue();
        if (expressionValue == null || expressionValue.trim().isEmpty()) return;

        ExpressionBuilder builder = new ExpressionBuilder(expressionValue);
        ArrayList<Pin> pins = calculateMorePins();
        pins.forEach(p -> builder.variable(p.getTitle()));
        try {
            Expression exp = builder.build();
            pins.forEach(p -> {
                PinObject value = getPinValue(runnable, context, p);
                float v = PinFloat.getFloatValue(value);
                exp.setVariable(p.getTitle(), v);
            });
            double v = exp.evaluate();
            resultPin.getValue(PinFloat.class).setValue((float) v);
        } catch (Exception ignored) {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            service.showToast(service.getString(R.string.action_math_expression_subtitle_expression_error));
        }
    }

    @Override
    public ArrayList<Pin> calculateMorePins() {
        ArrayList<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (start) pins.add(pin);
            if (pin == expressionPin) start = true;
        }
        return pins;
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (!expressionPin.getLinks().isEmpty()) {
            return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_math_expression_linked);
        }
        return super.check(context);
    }
}
