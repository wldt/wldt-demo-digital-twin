package io.github.wldt.demo.digital;

public class DemoDigitalAdapterConfiguration {

    private static final int SLEEP_TIME_MS = 1000;

    private static final int EMULATED_ACTION_COUNT = 5;

    private final static double TEMPERATURE_MIN_VALUE = 20;

    private final static double TEMPERATURE_MAX_VALUE = 30;

    private int sleepTimeMs = SLEEP_TIME_MS;

    private int emulatedActionCount = EMULATED_ACTION_COUNT;

    private double temperatureMinValue = TEMPERATURE_MIN_VALUE;

    private double temperatureMaxValue = TEMPERATURE_MAX_VALUE;

    public DemoDigitalAdapterConfiguration() {
    }

    public DemoDigitalAdapterConfiguration(int sleepTimeMs, int emulatedActionCount, double temperatureMinValue, double temperatureMaxValue) {
        this.sleepTimeMs = sleepTimeMs;
        this.emulatedActionCount = emulatedActionCount;
        this.temperatureMinValue = temperatureMinValue;
        this.temperatureMaxValue = temperatureMaxValue;
    }

    public int getSleepTimeMs() {
        return sleepTimeMs;
    }

    public void setSleepTimeMs(int sleepTimeMs) {
        this.sleepTimeMs = sleepTimeMs;
    }

    public int getEmulatedActionCount() {
        return emulatedActionCount;
    }

    public void setEmulatedActionCount(int emulatedActionCount) {
        this.emulatedActionCount = emulatedActionCount;
    }

    public double getTemperatureMinValue() {
        return temperatureMinValue;
    }

    public void setTemperatureMinValue(double temperatureMinValue) {
        this.temperatureMinValue = temperatureMinValue;
    }

    public double getTemperatureMaxValue() {
        return temperatureMaxValue;
    }

    public void setTemperatureMaxValue(double temperatureMaxValue) {
        this.temperatureMaxValue = temperatureMaxValue;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DemoDigitalAdapterConfiguration{");
        sb.append("sleepTimeMs=").append(sleepTimeMs);
        sb.append(", emulatedActionCount=").append(emulatedActionCount);
        sb.append(", temperatureMinValue=").append(temperatureMinValue);
        sb.append(", temperatureMaxValue=").append(temperatureMaxValue);
        sb.append('}');
        return sb.toString();
    }
}
