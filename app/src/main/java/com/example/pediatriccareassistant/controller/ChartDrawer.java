package com.example.pediatriccareassistant.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.Child;
import com.example.pediatriccareassistant.model.Measurement;
import com.example.pediatriccareassistant.model.PercentileMeasure;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * ChartDrawer is responsible for drawing charts using the provided data.
 */
public class ChartDrawer {

    private Context context;

    /**
     * Constructor to initialize ChartDrawer with a context.
     *
     * @param context The context to be used for accessing resources.
     */
    public ChartDrawer(Context context) {
        this.context = context;
    }

    /**
     * Updates the chart with the given data.
     *
     * @param chart       The LineChart to be updated.
     * @param percentiles The percentile data to be displayed.
     * @param child       The child whose measurements are displayed.
     * @param chartType   The type of chart to be displayed.
     */
    public void updateChart(LineChart chart, ArrayList<PercentileMeasure> percentiles, Child child, PercentileMeasure.ChartType chartType) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        dataSets.add(createLineDataSet(getDataValuesFromPercentiles(percentiles, PercentileMeasure::getP3), "3%", ContextCompat.getColor(context, R.color.chart_3_97)));
        dataSets.add(createLineDataSet(getDataValuesFromPercentiles(percentiles, PercentileMeasure::getP10), "10%", ContextCompat.getColor(context, R.color.chart_10_90)));
        dataSets.add(createLineDataSet(getDataValuesFromPercentiles(percentiles, PercentileMeasure::getP25), "25%", ContextCompat.getColor(context, R.color.chart_25_75)));
        dataSets.add(createLineDataSet(getDataValuesFromPercentiles(percentiles, PercentileMeasure::getP50), "50%", ContextCompat.getColor(context, R.color.chart_50)));
        dataSets.add(createLineDataSet(getDataValuesFromPercentiles(percentiles, PercentileMeasure::getP75), "75%", ContextCompat.getColor(context, R.color.chart_25_75)));
        dataSets.add(createLineDataSet(getDataValuesFromPercentiles(percentiles, PercentileMeasure::getP90), "90%", ContextCompat.getColor(context, R.color.chart_10_90)));
        dataSets.add(createLineDataSet(getDataValuesFromPercentiles(percentiles, PercentileMeasure::getP97), "97%", ContextCompat.getColor(context, R.color.chart_3_97)));

        if (child != null) {
            List<Entry> childDataValues = getDataValuesFromChild(child, chartType);
            dataSets.add(createUserLineDataSet(childDataValues, child.getName()));

            float minAbcyssa = 0;
            float maxAbcyssa = 0;

            // Focus on child's data by setting the x-axis range
            if (chartType == PercentileMeasure.ChartType.HEIGHT_WEIGHT)
            {
                minAbcyssa = getMinAbcyssaInHeightFromChild(child);
                maxAbcyssa = getMaxAbcyssaInHeightFromChild(child);
            }
            else {
                minAbcyssa = getMinAbcyssaInDaysFromChild(child);
                maxAbcyssa = getMaxAbcyssaInDaysFromChild(child);
            }

            configureXAxis(chart, minAbcyssa, maxAbcyssa);
        }

        // Apply the initial chart data
        chart.clear();
        chart.setData(new LineData(dataSets));
        chart.invalidate();

        // After setting the data, adjust Y-Axis based on the visible X-Axis range
        adjustYAxis(chart, percentiles);
    }

    private LineDataSet createLineDataSet(List<Entry> dataValues, String label, int color) {
        LineDataSet lineDataSet = new LineDataSet(dataValues, label);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT); // Ensure it uses the left Y-axis
        lineDataSet.setColor(color);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleColor(color);
        lineDataSet.setCircleRadius(1f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        return lineDataSet;
    }

    private LineDataSet createUserLineDataSet(List<Entry> dataValues, String label) {
        LineDataSet lineDataSet = new LineDataSet(dataValues, label);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT); // Ensure it uses the left Y-axis
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.abbey));
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.abbey));
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        return lineDataSet;
    }

    private List<Entry> getDataValuesFromPercentiles(ArrayList<PercentileMeasure> percentiles, Function<PercentileMeasure, Float> percentileFunction) {
        ArrayList<Entry> dataValues = new ArrayList<>();

        for (PercentileMeasure percentile : percentiles) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dataValues.add(new Entry(percentile.getAbcyssa(), percentileFunction.apply(percentile)));
            }
        }
        return dataValues;
    }

    private List<Entry> getDataValuesFromChild(Child child, PercentileMeasure.ChartType type)
    {
        ArrayList<Entry> dataValues = new ArrayList<>();
        LocalDate birthday = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            birthday = LocalDate.of(child.getBirth_year(), child.getBirth_month(), child.getBirth_day());
        }

        for (Measurement measurement : child.getMeasurements())
        {
            float y_value = 0f;
            float x_value;

            LocalDate measurementDate = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                measurementDate = LocalDate.of(measurement.getYear(), measurement.getMonth(), measurement.getDay_of_month());
            }
            x_value = daysBetween(birthday, measurementDate);

            switch (type){
                case WEIGHT:
                    y_value = measurement.getWeight();
                    break;
                case HEIGHT:
                    y_value = measurement.getHeight();
                    break;
                case HEIGHT_WEIGHT:
                    x_value = measurement.getHeight();
                    y_value = measurement.getWeight();
                    break;
            }

            dataValues.add(new Entry(x_value, y_value));
        }
        return dataValues;
    }

    /**
     * Calculates the number of days between two dates.
     *
     * @param startDate The starting date (e.g., birthday).
     * @param endDate   The ending date (e.g., another date).
     * @return The number of days between the two dates.
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        // Ensure startDate is before endDate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date must be before end date");
            }
        }

        // Calculate the number of days between the two dates
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return ChronoUnit.DAYS.between(startDate, endDate);
        }

        return -1;
    }

    private float getMinAbcyssaInHeightFromChild(Child child) {
        if (child == null || child.getMeasurements() == null || child.getMeasurements().isEmpty()) {
            return 0;
        }

        float minAbcyssa = Float.MAX_VALUE;

        for (Measurement measurement : child.getMeasurements())
        {
            float abcyssa = measurement.getHeight();
            if (abcyssa < minAbcyssa) {
                minAbcyssa = abcyssa;
            }
        }
        return minAbcyssa;
    }

    private float getMaxAbcyssaInHeightFromChild(Child child) {
        if (child == null || child.getMeasurements() == null || child.getMeasurements().isEmpty()) {
            return 0;
        }

        float minAbcyssa = Float.MIN_VALUE;

        for (Measurement measurement : child.getMeasurements())
        {
            float abcyssa = measurement.getHeight();
            if (abcyssa > minAbcyssa) {
                minAbcyssa = abcyssa;
            }
        }
        return minAbcyssa;
    }

    private float getMinAbcyssaInDaysFromChild(Child child)
    {
        if (child == null || child.getMeasurements() == null || child.getMeasurements().isEmpty()) {
            return 0;
        }

        float minAbcyssa = Float.MAX_VALUE;
        LocalDate birthday = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            birthday = LocalDate.of(child.getBirth_year(), child.getBirth_month(), child.getBirth_day());
        }

        for (Measurement measurement : child.getMeasurements()) {
            LocalDate measurementDate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                measurementDate = LocalDate.of(measurement.getYear(), measurement.getMonth(), measurement.getDay_of_month());
            }
            float abcyssa = daysBetween(birthday, measurementDate);
            if (abcyssa < minAbcyssa) {
                minAbcyssa = abcyssa;
            }
        }
        return minAbcyssa;
    }

    private float getMaxAbcyssaInDaysFromChild(Child child) {
        if (child == null || child.getMeasurements() == null || child.getMeasurements().isEmpty()) {
            return 0; // or throw an exception if preferred
        }

        float maxAbcyssa = Float.MIN_VALUE;
        LocalDate birthday = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            birthday = LocalDate.of(child.getBirth_year(), child.getBirth_month(), child.getBirth_day());
        }

        for (Measurement measurement : child.getMeasurements()) {
            LocalDate measurementDate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                measurementDate = LocalDate.of(measurement.getYear(), measurement.getMonth(), measurement.getDay_of_month());
            }
            float abcyssa = daysBetween(birthday, measurementDate);
            if (abcyssa > maxAbcyssa) {
                maxAbcyssa = abcyssa;
            }
        }
        return maxAbcyssa;
    }

    private void configureXAxis(LineChart chart, float minAbcyssa, float maxAbcyssa) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(minAbcyssa);
        xAxis.setAxisMaximum(maxAbcyssa);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(10, true);
    }

    private float getMinYFromPercentiles(ArrayList<PercentileMeasure> percentiles, float minAbcyssa, float maxAbcyssa) {
        float minY = Float.MAX_VALUE;
        for (PercentileMeasure percentile : percentiles) {
            if (percentile.getAbcyssa() >= minAbcyssa && percentile.getAbcyssa() <= maxAbcyssa) {
                minY = Math.min(minY, Math.min(
                        Math.min(percentile.getP3(), percentile.getP10()),
                        Math.min(percentile.getP25(), Math.min(percentile.getP50(), Math.min(percentile.getP75(), Math.min(percentile.getP90(), percentile.getP97()))))
                ));
            }
        }
        return minY;
    }

    private float getMaxYFromPercentiles(ArrayList<PercentileMeasure> percentiles, float minAbcyssa, float maxAbcyssa) {
        float maxY = Float.MIN_VALUE;
        for (PercentileMeasure percentile : percentiles) {
            if (percentile.getAbcyssa() >= minAbcyssa && percentile.getAbcyssa() <= maxAbcyssa) {
                maxY = Math.max(maxY, Math.max(
                        Math.max(percentile.getP3(), percentile.getP10()),
                        Math.max(percentile.getP25(), Math.max(percentile.getP50(), Math.max(percentile.getP75(), Math.max(percentile.getP90(), percentile.getP97()))))
                ));
            }
        }
        return maxY;
    }

    private void adjustYAxis(LineChart chart, ArrayList<PercentileMeasure> percentiles) {
        XAxis xAxis = chart.getXAxis();
        float minAbcyssa = xAxis.getAxisMinimum();
        float maxAbcyssa = xAxis.getAxisMaximum();

        float minY = getMinYFromPercentiles(percentiles, minAbcyssa, maxAbcyssa);
        float maxY = getMaxYFromPercentiles(percentiles, minAbcyssa, maxAbcyssa);

        configureYAxis(chart, minY, maxY);
    }

    private void configureYAxis(LineChart chart, float minY, float maxY) {
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(minY);
        leftAxis.setAxisMaximum(maxY);
        leftAxis.setGranularity(1f);
        leftAxis.setLabelCount(10, true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }
}
