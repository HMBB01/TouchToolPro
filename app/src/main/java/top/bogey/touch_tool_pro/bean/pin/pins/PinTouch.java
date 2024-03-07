package top.bogey.touch_tool_pro.bean.pin.pins;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinTouch extends PinScreen {
    private final ArrayList<TouchRecord> records = new ArrayList<>();
    private TouchAnchor anchor = TouchAnchor.TOP_LEFT;
    private Point anchorPoint = new Point();

    public PinTouch() {
        super(PinType.TOUCH);
    }

    public PinTouch(Context context, ArrayList<TouchRecord> records) {
        super(PinType.TOUCH, context);
        setRecords(context, records);
    }

    public PinTouch(JsonObject jsonObject) {
        super(jsonObject);
        anchor = GsonUtils.getAsObject(jsonObject, "anchor", TouchAnchor.class, TouchAnchor.TOP_LEFT);
        int x = GsonUtils.getAsInt(jsonObject, "x", 0);
        int y = GsonUtils.getAsInt(jsonObject, "y", 0);
        anchorPoint = new Point(x, y);
        parseRecords(GsonUtils.getAsString(jsonObject, "records", null));
    }

    @Override
    public void resetValue() {
        super.resetValue();
        records.clear();
        anchor = TouchAnchor.TOP_LEFT;
        anchorPoint = new Point();
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.TouchPinColor);
    }

    public HashSet<GestureDescription.StrokeDescription> getStrokes(Context context, float timeScale, int offsetPx) {
        float scale = getScale(context);
        Point offset = getAnchorPoint(context);
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Integer> times = new ArrayList<>();
        HashMap<Integer, Path> prePaths = new HashMap<>();
        HashMap<Integer, Integer> preTimes = new HashMap<>();
        for (TouchRecord record : records) {
            for (PathPoint point : record.points) {
                int x = (int) Math.max(0, Math.random() * 2 * offsetPx - offsetPx + point.x * scale);
                int y = (int) Math.max(0, Math.random() * 2 * offsetPx - offsetPx + point.y * scale);
                x += offset.x;
                y += offset.y;

                Path path = prePaths.get(point.ownerId);
                int time = preTimes.computeIfAbsent(point.ownerId, k -> 0);
                if (path == null) {
                    path = new Path();
                    path.moveTo(x, y);
                    prePaths.put(point.ownerId, path);
                } else {
                    path.lineTo(x, y);
                    preTimes.put(point.ownerId, time + record.time);
                }

                if (point.end) {
                    paths.add(path);
                    times.add(time);
                    prePaths.remove(point.ownerId);
                    preTimes.remove(point.ownerId);
                }
            }
        }
        HashSet<GestureDescription.StrokeDescription> strokeSet = new HashSet<>();
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            int time = (int) Math.max(1, times.get(i) * timeScale);
            GestureDescription.StrokeDescription strokeDescription = new GestureDescription.StrokeDescription(path, 0, time);
            strokeSet.add(strokeDescription);
        }
        return strokeSet;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<HashSet<GestureDescription.StrokeDescription>> getStrokeList(Context context, float timeScale, int offsetPx) {
        ArrayList<HashSet<GestureDescription.StrokeDescription>> strokes = new ArrayList<>();
        float scale = getScale(context);
        Point offset = getAnchorPoint(context);

        HashMap<Integer, Point> prePoints = new HashMap<>();
        HashMap<Integer, GestureDescription.StrokeDescription> preStrokeMap = new HashMap<>();

        for (int i = 0; i < records.size(); i++) {
            boolean isLast = i == records.size() - 1;
            TouchRecord record = records.get(i);

            HashSet<GestureDescription.StrokeDescription> strokeSet = new HashSet<>();

            for (PathPoint point : record.points) {
                int x = (int) Math.max(0, Math.random() * 2 * offsetPx - offsetPx + point.x * scale);
                int y = (int) Math.max(0, Math.random() * 2 * offsetPx - offsetPx + point.y * scale);
                x += offset.x;
                y += offset.y;

                Point prePoint = prePoints.get(point.ownerId);
                Path path = new Path();
                if (prePoint == null) {
                    path.moveTo(x, y);
                    x++;
//                    y++;
                } else {
                    path.moveTo(prePoint.x, prePoint.y);
                }
                path.lineTo(x, y);

                int time = (int) Math.max(1, record.time * timeScale);
                GestureDescription.StrokeDescription description = preStrokeMap.get(point.ownerId);
                if (description == null) {
                    description = new GestureDescription.StrokeDescription(path, 0, time, !point.end && !isLast);
                } else {
                    description = description.continueStroke(path, 0, time, !point.end && !isLast);
                }
                preStrokeMap.put(point.ownerId, description);
                prePoints.put(point.ownerId, new Point(x, y));
                strokeSet.add(description);

                if (point.end) {
                    preStrokeMap.remove(point.ownerId);
                    prePoints.remove(point.ownerId);
                }
            }
            if (!strokeSet.isEmpty()) strokes.add(strokeSet);
        }
        return strokes;
    }

    public HashSet<ArrayList<Point>> getPaths(Context context) {
        float scale = getScale(context);
        Point offset = getAnchorPoint(context);
        HashSet<ArrayList<Point>> paths = new HashSet<>();
        HashMap<Integer, ArrayList<Point>> points = new HashMap<>();
        records.forEach(record -> record.points.forEach(point -> {
            int x = (int) (point.x * scale + offset.x);
            int y = (int) (point.y * scale + offset.y);
            ArrayList<Point> list = points.computeIfAbsent(point.ownerId, k -> new ArrayList<>());
            list.add(new Point(x, y));
            if (point.end) {
                paths.add(list);
                points.remove(point.ownerId);
            }
        }));
        return paths;
    }

    public ArrayList<TouchRecord> getRecords(Context context) {
        Point point = getAnchorPoint(context);
        ArrayList<TouchRecord> records = new ArrayList<>();
        this.records.forEach(record -> {
            TouchRecord touchRecord = new TouchRecord(record);
            touchRecord.scale(getScale(context));
            touchRecord.offset(point.x, point.y);
            records.add(touchRecord);
        });
        return records;
    }

    public ArrayList<TouchRecord> getRecords() {
        return records;
    }

    public void setRecords(Context context, PinTouch pinTouch) {
        setRecords(context, pinTouch.getRecords(context), pinTouch.anchor);
    }

    public void setRecords(Context context, ArrayList<TouchRecord> records) {
        setRecords(context, records, TouchAnchor.TOP_LEFT);
    }

    public void setRecords(Context context, ArrayList<TouchRecord> records, TouchAnchor anchor) {
        setScreen(context);
        this.anchor = anchor;
        Rect rect = getRecordsArea(records);
        Point size = DisplayUtils.getScreenSize(context);
        switch (anchor) {
            case TOP_LEFT -> anchorPoint = new Point(rect.left, rect.top);
            case TOP_RIGHT -> anchorPoint = new Point(rect.right - size.x, rect.top);
            case BOTTOM_LEFT -> anchorPoint = new Point(rect.left, rect.bottom - size.y);
            case BOTTOM_RIGHT -> anchorPoint = new Point(rect.right - size.x, rect.bottom - size.y);
        }
        this.records.clear();
        records.forEach(record -> {
            TouchRecord touchRecord = new TouchRecord(record);
            touchRecord.offset(-rect.left, -rect.top);
            this.records.add(touchRecord);
        });
    }

    public Rect getRecordsArea(Context context) {
        float scale = getScale(context);
        Rect rect = getRecordsArea(records);
        if (scale == 1) return rect;
        rect.set(rect.left, rect.top, (int) (rect.right * scale), (int) (rect.bottom * scale));
        return rect;
    }

    private Rect getRecordsArea(ArrayList<TouchRecord> records) {
        Rect rect = new Rect();
        boolean init = false;
        for (TouchRecord record : records) {
            for (PathPoint point : record.points) {
                if (!init) {
                    rect.left = rect.right = point.x;
                    rect.top = rect.bottom = point.y;
                    init = true;
                } else {
                    rect.left = Math.min(point.x, rect.left);
                    rect.right = Math.max(point.x, rect.right);
                    rect.top = Math.min(point.y, rect.top);
                    rect.bottom = Math.max(point.y, rect.bottom);
                }
            }
        }
        return rect;
    }

    public Point getAnchorPoint(Context context) {
        float scale = getScale(context);
        Point start = new Point((int) (anchorPoint.x * scale), (int) (anchorPoint.y * scale));
        Point size = DisplayUtils.getScreenSize(context);
        Rect rect = getRecordsArea(records);
        switch (anchor) {
            case TOP_LEFT:
                break;
            case TOP_RIGHT:
                start.x = (int) (start.x + size.x - rect.width() * scale);
                break;
            case BOTTOM_LEFT:
                start.y = (int) (start.y + size.y - rect.height() * scale);
                break;
            case BOTTOM_RIGHT:
                start.x = (int) (start.x + size.x - rect.width() * scale);
                start.y = (int) (start.y + size.y - rect.height() * scale);
                break;
        }
        return start;
    }

    public TouchAnchor getAnchor() {
        return anchor;
    }

    private String recordsToString() {
        if (records.isEmpty()) return null;
        StringBuilder builder = new StringBuilder();
        records.forEach(record -> builder.append(record.toString()).append("|"));
        return builder.substring(0, builder.length() - 1);
    }

    private void parseRecords(String info) {
        if (info == null || info.isEmpty()) return;
        String[] split = info.split("\\|");
        for (String s : split) {
            records.add(new TouchRecord(s));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinTouch pinTouch = (PinTouch) o;

        if (!records.equals(pinTouch.records)) return false;
        if (anchor != pinTouch.anchor) return false;
        return anchorPoint.equals(pinTouch.anchorPoint);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + records.hashCode();
        result = 31 * result + anchor.hashCode();
        result = 31 * result + anchorPoint.hashCode();
        return result;
    }

    public enum TouchAnchor {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
    }

    // time;[x.y.t,x.y.t]
    public static class TouchRecord {
        private final int time;
        private final HashSet<PathPoint> points;

        public TouchRecord(int time) {
            this.time = time;
            points = new HashSet<>();
        }

        public TouchRecord(int time, int x, int y, boolean end) {
            this.time = time;
            PathPoint point = new PathPoint(0, x, y);
            point.end = end;
            points = new HashSet<>(Collections.singleton(point));
        }

        public TouchRecord(TouchRecord record) {
            time = record.time;
            points = new HashSet<>();
            record.points.forEach(point -> points.add(new PathPoint(point)));
        }

        public TouchRecord(String info) {
            String[] split = info.split(";");
            time = Integer.parseInt(split[0]);
            points = new HashSet<>();
            String substring = split[1].substring(1, split[1].length() - 1);
            for (String s : substring.split(",")) {
                points.add(new PathPoint(s));
            }
        }

        public void addPoint(PathPoint point) {
            points.add(point);
        }

        public void removePoint(int ownerId) {
            for (PathPoint point : points) {
                if (point.getOwnerId() == ownerId) {
                    points.remove(point);
                    break;
                }
            }
        }

        public boolean isEmpty() {
            return points.isEmpty();
        }

        public PathPoint getPointByOwnerId(int ownerId) {
            for (PathPoint point : points) {
                if (point.ownerId == ownerId) return point;
            }
            return null;
        }

        public HashSet<PathPoint> getPoints() {
            return points;
        }

        public boolean existEndPoint() {
            for (PathPoint point : points) {
                if (point.end) return true;
            }
            return false;
        }

        public void scale(float scale) {
            points.forEach(point -> point.scale(scale));
        }

        public void offset(int x, int y) {
            points.forEach(point -> point.offset(x, y));
        }

        public int getTime() {
            return time;
        }

        @NonNull
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(time).append(";");
            builder.append("[");
            for (PathPoint point : points) {
                builder.append(point.toString()).append(",");
            }
            return builder.substring(0, builder.length() - 1) + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TouchRecord that = (TouchRecord) o;

            if (time != that.time) return false;
            return points.equals(that.points);
        }

        @Override
        public int hashCode() {
            int result = time;
            result = 31 * result + points.hashCode();
            return result;
        }
    }


    public static class PathPoint extends Point {
        private final int ownerId;
        private boolean end;

        public PathPoint(int ownerId, int x, int y) {
            super(x, y);
            this.ownerId = ownerId;
            end = false;
        }

        public PathPoint(PathPoint point) {
            super(point.x, point.y);
            ownerId = point.ownerId;
            end = point.end;
        }

        public PathPoint(String info) {
            String[] split = info.split("\\.");
            x = Integer.parseInt(split[0]);
            y = Integer.parseInt(split[1]);
            ownerId = Integer.parseInt(split[2]);
            end = split.length != 3;
        }

        public void scale(float scale) {
            x = (int) (x * scale);
            y = (int) (y * scale);
        }

        public boolean isEnd() {
            return end;
        }

        public void setEnd(boolean end) {
            this.end = end;
        }

        public int getOwnerId() {
            return ownerId;
        }

        @NonNull
        @Override
        public String toString() {
            String s = x + "." + y + "." + ownerId;
            if (end) s += "." + 1;
            return s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            PathPoint pathPoint = (PathPoint) o;

            if (ownerId != pathPoint.ownerId) return false;
            return end == pathPoint.end;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + ownerId;
            result = 31 * result + (end ? 1 : 0);
            return result;
        }
    }

    public static class PinTouchSerializer implements JsonSerializer<PinTouch> {

        @Override
        public JsonElement serialize(PinTouch src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", src.getType().name());
            jsonObject.addProperty("subType", src.getSubType().name());
            jsonObject.addProperty("screen", src.getScreen());
            jsonObject.addProperty("anchor", src.anchor.name());
            jsonObject.addProperty("x", src.anchorPoint.x);
            jsonObject.addProperty("y", src.anchorPoint.y);
            jsonObject.addProperty("records", src.recordsToString());
            return jsonObject;
        }
    }

}
