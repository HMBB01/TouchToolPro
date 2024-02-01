package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinImage extends PinScreen {
    private String image;

    private transient Bitmap bitmap;

    public PinImage() {
        super(PinType.IMAGE);
    }

    public PinImage(Bitmap image) {
        this();
        setImage(image);
    }

    public PinImage(Context context, Bitmap image) {
        super(PinType.IMAGE, context);
        setImage(context, image);
    }

    public PinImage(JsonObject jsonObject) {
        super(jsonObject);
        image = GsonUtils.getAsString(jsonObject, "image", null);
    }

    @Override
    public void resetValue() {
        super.resetValue();
        image = null;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() + "image";
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.ImagePinColor);
    }

    public Bitmap getImage(Context context) {
        if (bitmap == null || bitmap.isRecycled()) {
            if (image != null && !image.isEmpty()) {
                try {
                    byte[] bytes = Base64.decode(image, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (getScale(context) != 1) {
                        Matrix matrix = new Matrix();
                        matrix.postScale(getScale(context), getScale(context));
                        this.bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    } else {
                        this.bitmap = bitmap;
                    }
                    return this.bitmap;
                } catch (Exception | Error ignored) {
                }
            }
            return null;
        }
        return bitmap;
    }

    public void setImage(Context context, Bitmap image) {
        setScreen(context);
        setImage(image);
    }

    public void setImage(Bitmap image) {
        bitmap = image;
        if (image == null) {
            this.image = null;
            return;
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            image.compress(Bitmap.CompressFormat.WEBP, 50, stream);
            byte[] bytes = stream.toByteArray();
            this.image = Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinImage pinImage = (PinImage) o;

        if (!Objects.equals(image, pinImage.image)) return false;
        return Objects.equals(bitmap, pinImage.bitmap);
    }

    @Override
    public int hashCode() {
        int result = image != null ? image.hashCode() : 0;
        result = 31 * result + (bitmap != null ? bitmap.hashCode() : 0);
        return result;
    }
}
