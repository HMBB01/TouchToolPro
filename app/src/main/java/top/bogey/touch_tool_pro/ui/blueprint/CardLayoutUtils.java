package top.bogey.touch_tool_pro.ui.blueprint;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashSet;

import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class CardLayoutUtils {

    public static Path calculateLinePath(int[] outLocation, int[] inLocation, boolean v, float gridSize) {
        Path path = new Path();
        if (outLocation == null || inLocation == null) return path;

        PointF outLinkLinePoint;
        PointF inLinkLinePoint;

        if (v) {
            outLinkLinePoint = new PointF(outLocation[0], outLocation[1] + gridSize);
            inLinkLinePoint = new PointF(inLocation[0], inLocation[1] - gridSize);
        } else {
            outLinkLinePoint = new PointF(outLocation[0] + gridSize, outLocation[1]);
            inLinkLinePoint = new PointF(inLocation[0] - gridSize, inLocation[1]);
        }

        // 结束点在右边，为正方向
        int xScale = outLinkLinePoint.x < inLinkLinePoint.x ? 1 : -1;
        // 结束点在下边，为正方向
        int yScale = outLinkLinePoint.y < inLinkLinePoint.y ? 1 : -1;

        path.moveTo(outLocation[0], outLocation[1]);
        path.lineTo(outLinkLinePoint.x, outLinkLinePoint.y);

        float offsetX = Math.abs(outLinkLinePoint.x - inLinkLinePoint.x);
        float offsetY = Math.abs(outLinkLinePoint.y - inLinkLinePoint.y);
        boolean xLong = offsetX > offsetY;

        /*
        垂直连接：
            X长度为0：
                yScale = 1, 向下连接，看其他条件
                yScale = -1, 向右绕2格连接
            X更长：
                yScale = 1, 就先竖，再横，再竖
                yScale = -1， 就先横，再斜，再横
            Y更长：
                yScale = 1, 就先竖，再斜，再竖
                yScale = -1, 就先横，再竖，再横
        水平连接：
            X更长：
                xScale = 1, 就先横，再斜，再横
                xScale = -1, 就先竖，再横，再竖
            Y更长：
                xScale = 1, 就先横，再竖，再横
                xScale = -1, 就先竖，再斜，再竖
            Y长度为0：
                xScale = 1, 水平连接，看其他条件
                xScale = -1, 向下绕2格连接
        */
        float linkLineLen = Math.abs(offsetX - offsetY) / 2;

        boolean flag = true;
        if (offsetX < gridSize * 3.1 && v) {
            if (yScale == 1 && offsetX < gridSize / 2) {
                flag = false;
            } else if (yScale == -1 && offsetY > gridSize) {
                // 向左绕2格连接
                float x = Math.max(outLinkLinePoint.x, inLinkLinePoint.x) - gridSize * 6;
                path.lineTo(x, outLinkLinePoint.y);
                path.lineTo(x, inLinkLinePoint.y);
                flag = false;
            }
        } else if (offsetY < gridSize * 3.1 && !v) {
            if (xScale == 1 && offsetY < gridSize / 2) {
                flag = false;
            } else if (xScale == -1 && offsetX > gridSize) {
                //向下绕2格连接
                float y = Math.max(outLinkLinePoint.y, inLinkLinePoint.y) + gridSize * 6;
                path.lineTo(outLinkLinePoint.x, y);
                path.lineTo(inLinkLinePoint.x, y);
                flag = false;
            }
        }

        if (flag) {
            if (xLong) {
                if ((v && yScale == 1) || (!v && xScale == -1)) {
                    //就先竖，再横，再竖
                    path.lineTo(outLinkLinePoint.x, outLinkLinePoint.y + offsetY / 2 * yScale);
                    path.lineTo(inLinkLinePoint.x, inLinkLinePoint.y - offsetY / 2 * yScale);
                } else {
                    //就先横，再斜，再横
                    path.lineTo(outLinkLinePoint.x + linkLineLen * xScale, outLinkLinePoint.y);
                    path.lineTo(inLinkLinePoint.x - linkLineLen * xScale, inLinkLinePoint.y);
                }
            } else {
                if ((v && yScale == 1) || (!v && xScale == -1)) {
                    //就先竖，再斜，再竖
                    path.lineTo(outLinkLinePoint.x, outLinkLinePoint.y + linkLineLen * yScale);
                    path.lineTo(inLinkLinePoint.x, inLinkLinePoint.y - linkLineLen * yScale);
                } else {
                    //就先横，再竖，再横
                    path.lineTo(outLinkLinePoint.x + offsetX / 2 * xScale, outLinkLinePoint.y);
                    path.lineTo(inLinkLinePoint.x - offsetX / 2 * xScale, inLinkLinePoint.y);
                }
            }
        }

        path.lineTo(inLinkLinePoint.x, inLinkLinePoint.y);
        path.lineTo(inLocation[0], inLocation[1]);

        return path;
    }

    public static Rect getCardsArea(HashSet<ActionCard<?>> cards, float scale) {
        ArrayList<Point> points = new ArrayList<>();
        cards.forEach(card -> {
            int x = (int) card.getX();
            int y = (int) card.getY();
            int width = (int) (card.getWidth() * scale);
            int height = (int) (card.getHeight() * scale);
            points.add(new Point(x, y));
            points.add(new Point(x + width, y + height));
        });
        return DisplayUtils.calculatePointArea(points);
    }
}
