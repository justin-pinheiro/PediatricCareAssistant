package com.example.pediatriccareassistant.model;

import com.google.firebase.database.DataSnapshot;

public class PercentileMeasure
{
    private final float abcyssa, P3, P10, P25, P50, P75, P90, P97;

    public PercentileMeasure(float abcyssa, float p3, float p10, float p25, float p50, float p75, float p90, float p97) {
        this.abcyssa = abcyssa;
        P3 = p3;
        P10 = p10;
        P25 = p25;
        P50 = p50;
        P75 = p75;
        P90 = p90;
        P97 = p97;
    }

    public float getAbcyssa() {
        return abcyssa;
    }

    public float getP3() {
        return P3;
    }

    public float getP10() {
        return P10;
    }

    public float getP25() {
        return P25;
    }

    public float getP50() {
        return P50;
    }

    public float getP75() {
        return P75;
    }

    public float getP90() {
        return P90;
    }

    public float getP97() {
        return P97;
    }

    public enum ChartType {
        WEIGHT,
        HEIGHT,
        HEIGHT_WEIGHT
    }

    public static PercentileMeasure fromSnapshot(DataSnapshot dataSnapshot, ChartType type)
    {
        float abcyssa;
        switch (type) {
            case WEIGHT:
                abcyssa = getFloatFromDataSnapshot(dataSnapshot, "Age");
                break;
            case HEIGHT:
                abcyssa = getFloatFromDataSnapshot(dataSnapshot, "Day");
                break;
            case HEIGHT_WEIGHT:
                abcyssa = getFloatFromDataSnapshot(dataSnapshot, "Height");
                break;
            default:
                abcyssa = -1;
                break;
        }

        float p3 = getFloatFromDataSnapshot(dataSnapshot, "P3");
        float p10 = getFloatFromDataSnapshot(dataSnapshot, "P10");
        float p25 = getFloatFromDataSnapshot(dataSnapshot, "P25");
        float p50 = getFloatFromDataSnapshot(dataSnapshot, "P50");
        float p75 = getFloatFromDataSnapshot(dataSnapshot, "P75");
        float p90 = getFloatFromDataSnapshot(dataSnapshot, "P90");
        float p97 = getFloatFromDataSnapshot(dataSnapshot, "P97");

        return new PercentileMeasure(abcyssa, p3, p10, p25, p50, p75, p90, p97);
    }

    private static float getFloatFromDataSnapshot(DataSnapshot dataSnapshot, String key) {
        Object value = dataSnapshot.child(key).getValue();
        if (value instanceof Double) {
            return ((Double) value).floatValue();
        } else if (value instanceof Long) {
            return ((Long) value).floatValue();
        } else if (value instanceof String) {
            return Float.parseFloat((String) value);
        }
        return 0;
    }

}