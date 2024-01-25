package io.github.wldt.demo.physical;

import io.github.wldt.demo.utils.GlobalKeywords;

public class DemoPhysicalAdapterConfiguration {

    private int messageUpdateTime = GlobalKeywords.MESSAGE_UPDATE_TIME;

    private int messageUpdateNumber = GlobalKeywords.MESSAGE_UPDATE_NUMBER;

    private double temperatureMinValue = GlobalKeywords.TEMPERATURE_MIN_VALUE;

    private double temperatureMaxValue = GlobalKeywords.TEMPERATURE_MAX_VALUE;


    public DemoPhysicalAdapterConfiguration() {
    }

    public DemoPhysicalAdapterConfiguration(int messageUpdateTime, int messageUpdateNumber, double temperatureMinValue, double temperatureMaxValue) {
        this.messageUpdateTime = messageUpdateTime;
        this.messageUpdateNumber = messageUpdateNumber;
        this.temperatureMinValue = temperatureMinValue;
        this.temperatureMaxValue = temperatureMaxValue;
    }

    public int getMessageUpdateTime() {
        return messageUpdateTime;
    }

    public void setMessageUpdateTime(int messageUpdateTime) {
        this.messageUpdateTime = messageUpdateTime;
    }

    public int getMessageUpdateNumber() {
        return messageUpdateNumber;
    }

    public void setMessageUpdateNumber(int messageUpdateNumber) {
        this.messageUpdateNumber = messageUpdateNumber;
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
        final StringBuilder sb = new StringBuilder("DemoPhysicalAdapterConfiguration{");
        sb.append("messageUpdateTime=").append(messageUpdateTime);
        sb.append(", messageUpdateNumber=").append(messageUpdateNumber);
        sb.append(", temperatureMinValue=").append(temperatureMinValue);
        sb.append(", temperatureMaxValue=").append(temperatureMaxValue);
        sb.append('}');
        return sb.toString();
    }
}
