package top.bogey.touch_tool_pro.bean.action.string;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.ocr.OcrResult;
import top.bogey.touch_tool_pro.utils.ocr.Predictor;

public class OcrTextStateAction extends Action {
    private transient Pin textPin = new Pin(new PinString(), R.string.pin_string, true);
    private transient Pin areaPin = new Pin(new PinArea(PinSubType.OCR), R.string.pin_area);
    private transient Pin similarPin = new Pin(new PinInteger(95), R.string.action_ocr_text_state_subtitle_similar);

    public OcrTextStateAction() {
        super(ActionType.OCR_TEXT_STATE);
        needCapture = true;
        textPin = addPin(textPin);
        areaPin = addPin(areaPin);
        similarPin = addPin(similarPin);
    }

    public OcrTextStateAction(JsonObject jsonObject) {
        super(jsonObject);
        needCapture = true;
        textPin = reAddPin(textPin);
        areaPin = reAddPin(areaPin);
        similarPin = reAddPin(similarPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinString text = textPin.getValue(PinString.class);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (!service.isCaptureEnabled()) return;

        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        Rect areaArea = area.getArea(service);
        Bitmap currImage = runnable.getCurrImage(service);
        Bitmap bitmap = DisplayUtils.safeCreateBitmap(currImage, areaArea);
        ArrayList<OcrResult> results = Predictor.runOcr(bitmap);
        if (results.isEmpty()) return;

        PinInteger similar = (PinInteger) getPinValue(runnable, context, similarPin);
        StringBuilder builder = new StringBuilder();
        for (int i = results.size() - 1; i >= 0; i--) {
            OcrResult result = results.get(i);
            if (result.getSimilar() >= similar.getValue()) {
                builder.append(result.getLabel());
            }
        }
        text.setValue(builder.toString());
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (!Predictor.ocrReady()) {
            return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_ocr_not_ready);
        }
        return super.check(context);
    }
}
