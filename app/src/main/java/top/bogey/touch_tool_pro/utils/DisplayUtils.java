package top.bogey.touch_tool_pro.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DisplayUtils {
    public static int getAttrColor(Context context, int id, int defValue) {
        int[] attrs = {id};
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs);
        int resourceId = typedArray.getResourceId(0, defValue);
        typedArray.recycle();
        return context.getResources().getColor(resourceId, null);
    }

    public static boolean isPortrait(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getRotation() % 2 == Surface.ROTATION_0;
    }

    public static Point getScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        manager.getDefaultDisplay().getRealSize(point);
        return point;
    }

    public static Rect getScreenArea(Context context) {
        Point size = getScreenSize(context);
        return new Rect(0, 0, size.x, size.y);
    }

    public static int getScreen(Context context) {
        Point size = getScreenSize(context);
        return Math.min(size.x, size.y);
    }

    public static int getStatusBarHeight(View view, WindowManager.LayoutParams params) {
        if (params == null) return 0;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        // 绝对坐标和相对坐标一致，则状态栏高度为0，否则就是有状态栏
        if (isPortrait(view.getContext())) {
            if (location[1] > params.y) return getStatusBarHeight(view.getContext());
        } else {
            if (location[0] > params.x) return getStatusBarHeight(view.getContext());
        }
        return 0;
    }

    @SuppressLint({"InternalInsetResource", "DiscouragedApi"})
    public static int getStatusBarHeight(Context context) {
        int id = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (id > 0) return context.getResources().getDimensionPixelSize(id);
        return 0;
    }

    public static float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int[] getHsvColor(Bitmap bitmap, int x, int y) {
        if (bitmap == null) return new int[]{0, 0, 0};
        int pixel = bitmap.getPixel(x, y);
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;
        float[] hsv = new float[3];
        Color.RGBToHSV(red, green, blue, hsv);
        return new int[]{(int) (hsv[0] / 2), (int) (hsv[1] * 255), (int) (hsv[2] * 255)};
    }

    public static int getColorFromHsv(int[] hsv) {
        float[] tmp = new float[]{hsv[0] * 2, hsv[1] / 255f, hsv[2] / 255f};
        return Color.HSVToColor(tmp);
    }

    public static Rect calculatePointArea(ArrayList<Point> points) {
        boolean isInit = true;
        Rect area = new Rect();
        for (Point point : points) {
            if (isInit) {
                area.set(point.x, point.y, point.x, point.y);
                isInit = false;
            } else {
                if (point.x < area.left) area.left = point.x;
                if (point.x > area.right) area.right = point.x;
                if (point.y < area.top) area.top = point.y;
                if (point.y > area.bottom) area.bottom = point.y;
            }
        }
        return area;
    }

    public static Bitmap safeCreateBitmap(Bitmap bitmap, int x, int y, int width, int height) {
        if (bitmap == null) return null;
        x = Math.max(x, 0);
        y = Math.max(y, 0);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        width = Math.min(width, bitmapWidth - x);
        height = Math.min(height, bitmapHeight - y);
        try {
            return Bitmap.createBitmap(bitmap, x, y, width, height);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static Bitmap safeCreateBitmap(Bitmap bitmap, Rect rect) {
        return safeCreateBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
    }

    public static native MatchResult nativeMatchTemplate(Bitmap bitmap, Bitmap temp);

    public static synchronized Rect matchImage(Bitmap sourceBitmap, Bitmap matchBitmap, int matchValue, Rect area) {
        if (sourceBitmap == null || matchBitmap == null) return null;

        Bitmap bitmap = null;
        if (!(area.isEmpty())) {
            sourceBitmap = safeCreateBitmap(sourceBitmap, area);
            bitmap = sourceBitmap;
            if (bitmap == null) return null;
        }

        MatchResult matchResult = nativeMatchTemplate(sourceBitmap, matchBitmap);
        if (bitmap != null) bitmap.recycle();

        if (Math.min(100, matchValue) > matchResult.value) return null;
        matchResult.rect.offset(area.left, area.top);
        return matchResult.rect;
    }

    public static native List<MatchResult> nativeMatchColor(Bitmap bitmap, int[] hsvColor, int offset);

    public static synchronized List<Rect> matchColor(Bitmap sourceBitmap, int[] color, Rect area, int offset) {
        if (sourceBitmap == null) return null;

        Bitmap bitmap = null;
        if (!(area.isEmpty())) {
            sourceBitmap = safeCreateBitmap(sourceBitmap, area);
            bitmap = sourceBitmap;
            if (bitmap == null) return null;
        }

        List<MatchResult> matchResults = nativeMatchColor(sourceBitmap, color, offset);
        if (bitmap != null) bitmap.recycle();

        if (matchResults != null) {
            matchResults.sort(Comparator.comparingInt(MatchResult::getValue));
            List<Rect> rectList = new ArrayList<>();
            for (int i = matchResults.size() - 1; i >= 0; i--) {
                MatchResult matchResult = matchResults.get(i);
                matchResult.rect.offset(area.left, area.top);
                rectList.add(matchResult.rect);
            }
            return rectList;
        }
        return null;
    }
}
