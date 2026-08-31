package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class StockChartView extends View {
    private List<Integer> days = new ArrayList<>();
    private List<Integer> values = new ArrayList<>();
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public StockChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gridPaint.setColor(Color.rgb(220, 228, 223));
        gridPaint.setStrokeWidth(dp(1));
        linePaint.setColor(Color.rgb(23, 107, 58));
        linePaint.setStrokeWidth(dp(3));
        linePaint.setStyle(Paint.Style.STROKE);
        pointPaint.setColor(Color.rgb(23, 107, 58));
        pointPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.rgb(95, 107, 101));
        textPaint.setTextSize(sp(11));
    }

    public void setData(List<Integer> newDays, List<Integer> newValues) {
        days = new ArrayList<>(newDays);
        values = new ArrayList<>(newValues);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left = dp(48);
        float top = dp(24);
        float right = getWidth() - dp(18);
        float bottom = getHeight() - dp(38);

        for (int i = 0; i <= 4; i++) {
            float y = top + (bottom - top) * i / 4f;
            canvas.drawLine(left, y, right, y, gridPaint);
        }
        canvas.drawLine(left, top, left, bottom, gridPaint);
        canvas.drawLine(left, bottom, right, bottom, gridPaint);

        if (values.isEmpty()) {
            canvas.drawText("No data", left, top + dp(20), textPaint);
            return;
        }

        int min = values.get(0);
        int max = values.get(0);
        for (int value : values) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        if (min == max) {
            min = Math.max(0, min - 5);
            max += 5;
        }

        Path path = new Path();
        for (int i = 0; i < values.size(); i++) {
            float x = values.size() == 1 ? (left + right) / 2f : left + (right - left) * i / (values.size() - 1f);
            float ratio = (values.get(i) - min) / (float) (max - min);
            float y = bottom - ratio * (bottom - top);
            if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
            if (i == 0 || i == values.size() - 1 || (i + 1) % 5 == 0) {
                canvas.drawCircle(x, y, dp(3), pointPaint);
            }
        }
        canvas.drawPath(path, linePaint);

        canvas.drawText(String.valueOf(max), dp(8), top + dp(4), textPaint);
        canvas.drawText(String.valueOf(min), dp(8), bottom, textPaint);

        if (!days.isEmpty()) {
            drawDayLabel(canvas, 0, left, right, bottom);
            if (days.size() > 9) drawDayLabel(canvas, 9, left, right, bottom);
            if (days.size() > 19) drawDayLabel(canvas, 19, left, right, bottom);
            drawDayLabel(canvas, days.size() - 1, left, right, bottom);
        }
    }

    private void drawDayLabel(Canvas canvas, int index, float left, float right, float bottom) {
        if (index < 0 || index >= days.size()) return;
        float x = days.size() == 1 ? (left + right) / 2f : left + (right - left) * index / (days.size() - 1f);
        canvas.drawText(String.valueOf(days.get(index)), x - dp(5), bottom + dp(22), textPaint);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}
