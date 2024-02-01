package top.bogey.touch_tool_pro.bean.action.string;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.ocr.OcrResult;
import top.bogey.touch_tool_pro.utils.ocr.Predictor;

public class ExistTextOcrAction extends CheckAction {
    private transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private transient Pin areaPin = new Pin(new PinArea(PinSubType.OCR), R.string.pin_area);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point, true);
    private transient Pin fullTextPin = new Pin(new PinString(), R.string.pin_string, true);

    public ExistTextOcrAction() {
        super(ActionType.CHECK_EXIST_TEXT_OCR);
        needCapture = true;
        textPin = addPin(textPin);
        areaPin = addPin(areaPin);
        posPin = addPin(posPin);
        fullTextPin = addPin(fullTextPin);
    }

    public ExistTextOcrAction(JsonObject jsonObject) {
        super(jsonObject);
        needCapture = true;
        textPin = reAddPin(textPin);
        areaPin = reAddPin(areaPin);
        posPin = reAddPin(posPin);
        fullTextPin = reAddPin(fullTextPin);
    }

    @Override
    public void resetReturnValue(Pin pin) {
        if (pin.equals(resultPin)) super.resetReturnValue(pin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (!pin.equals(resultPin)) return;

        PinBoolean result = resultPin.getValue(PinBoolean.class);
        PinString fullText = fullTextPin.getValue(PinString.class);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (!service.isCaptureEnabled()) return;

        PinString text = (PinString) getPinValue(runnable, context, textPin);
        if (text.getValue() == null || text.getValue().isEmpty()) return;

        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        Rect areaArea = area.getArea(service);
        Bitmap currImage = runnable.getCurrImage(service);
        Bitmap bitmap = DisplayUtils.safeCreateBitmap(currImage, areaArea);
        ArrayList<OcrResult> results = Predictor.getInstance().runOcr(bitmap);
        if (results == null) return;

        Pattern pattern = Pattern.compile(text.getValue());
        for (OcrResult ocrResult : results) {
            if (pattern.matcher(ocrResult.getLabel()).find()) {
                result.setBool(true);
                fullText.setValue(ocrResult.getLabel());
                Rect rect = ocrResult.getArea();
                rect.offset(areaArea.left, areaArea.top);
                PinPoint value = posPin.getValue(PinPoint.class);
                value.setPoint(service, rect.centerX(), rect.centerY());
                break;
            }
        }
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (resultPin.getLinks().isEmpty()) {
            if (!posPin.getLinks().isEmpty()) {
                return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_result_pin_no_use);
            }
        }
        return super.check(context);
    }
}
