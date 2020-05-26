package com.example.feijibook.util;

import android.widget.PopupWindow;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.HashMap;
import java.util.Map;

/**
 * MeasureUtils
 *
 * @author yuepengfei
 * @date 2019/6/16
 * @description 测量工具类
 */
public class MeasureUtils {
    public static Map<String, Object> getLineChartMarkersLocation(
            Highlight highlight, LineChart lineChart, PopupWindow dw, PopupWindow pw, PopupWindow rw) {
        Map<String, Object> map = new HashMap<>();

        boolean onlyShowDetailMarker;
        int highLightX = (int) highlight.getXPx();
        int highLightY = (int) highlight.getYPx();
        // 获取LineChart距离屏幕顶部的距离
        int[] location = new int[2];
        lineChart.getLocationOnScreen(location);
        // 测量popupWindow，获取其高宽
        dw.getContentView().measure(0, 0);
        pw.getContentView().measure(0, 0);
        rw.getContentView().measure(0, 0);
        int detailsWindowHeight = dw.getContentView().getMeasuredHeight();
        int positionWindowHeight = pw.getContentView().getMeasuredHeight();
        int roundWindowHeight = rw.getContentView().getMeasuredHeight();
        int detailsWindowsWidth = dw.getContentView().getMeasuredWidth();
        int positionWindowsWidth = pw.getContentView().getMeasuredWidth();
        int roundWindowsWidth = rw.getContentView().getMeasuredWidth();

        int chartCenterX = (lineChart.getWidth() - detailsWindowsWidth) / 2;
        int chartCenterY = location[1] + (lineChart.getHeight() - detailsWindowHeight) / 2;
        int dwX = highLightX - detailsWindowsWidth / 2;
        int height = detailsWindowHeight + positionWindowHeight + roundWindowHeight;
        int dwY;
        // 当linechart距离顶部距离不够容纳 marker 且y轴金额不为0，只显示 detailmarker
        if (location[1] < height && highlight.getY() != 0) {
            onlyShowDetailMarker = true;
            dwY = highLightY + location[1] - detailsWindowHeight - roundWindowHeight / 2;
        } else {
            onlyShowDetailMarker = false;
            dwY = highLightY + location[1] - detailsWindowHeight - positionWindowHeight - roundWindowHeight / 2;
        }
        int pwX = highLightX - positionWindowsWidth / 2;
        int pwY = highLightY + location[1] - positionWindowHeight - roundWindowHeight / 2;
        int rwX = highLightX - roundWindowsWidth / 2;
        int rwY = highLightY + location[1] - roundWindowHeight / 2;

        map.put("chartCenterX", chartCenterX);
        map.put("chartCenterY", chartCenterY);
        map.put("dwX", dwX);
        map.put("dwY", dwY);
        map.put("pwX", pwX);
        map.put("pwY", pwY);
        map.put("rwX", rwX);
        map.put("rwY", rwY);
        map.put("onlyShowDetailMarker", onlyShowDetailMarker);
        return map;
    }

}
