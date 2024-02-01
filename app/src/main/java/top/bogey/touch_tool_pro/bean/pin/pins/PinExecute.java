package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinExecute extends PinObject {
    private String image;
    private transient Bitmap bitmap;

    public PinExecute() {
        super(PinType.EXECUTE);
    }

    public PinExecute(PinSubType subType) {
        super(PinType.EXECUTE, subType);
    }

    public PinExecute(JsonObject jsonObject) {
        super(jsonObject);
        image = GsonUtils.getAsString(jsonObject, "image", null);
    }

    @Override
    public void resetValue() {
        image = null;
    }

    public void setImage(Bitmap image) {
        if (image == null) {
            this.image = null;
            bitmap = null;
            return;
        }

        int size = (int) DisplayUtils.dp2px(MainApplication.getInstance(), 28);
        Bitmap scaledBitmap = DisplayUtils.scaleBitmap(image, size, size);
        bitmap = scaledBitmap;

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            scaledBitmap.compress(Bitmap.CompressFormat.WEBP, 50, stream);
            byte[] bytes = stream.toByteArray();
            this.image = Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImage(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        setImage(bitmap);
    }

    public Bitmap getImage() {
        if (bitmap == null || bitmap.isRecycled()) {
            if (image != null && !image.isEmpty()) {
                try {
                    byte[] bytes = Base64.decode(image, Base64.NO_WRAP);
                    this.bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    return this.bitmap;
                } catch (Exception | Error ignored) {
                }
            }
            return null;
        }
        return bitmap;
    }

    @Override
    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0);
    }

    @Override
    public ShapeAppearanceModel getPinStyle(Context context) {
        float cornerSize = DisplayUtils.dp2px(context, 5.5f);
        return ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.CUT, 0)
                .setTopRightCorner(CornerFamily.CUT, 0)
                .setBottomLeftCorner(CornerFamily.CUT, cornerSize)
                .setBottomRightCorner(CornerFamily.CUT, cornerSize)
                .build();
    }
}
