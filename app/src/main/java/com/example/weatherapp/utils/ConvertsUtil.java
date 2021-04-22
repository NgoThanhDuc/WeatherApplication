package com.example.weatherapp.utils;

import com.example.weatherapp.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertsUtil {

    public String convertKelvinToCelsius(String temp) {
        Double kelvin = Double.valueOf(temp) - 273.15; // °K
        String celsius = String.valueOf(kelvin.intValue());

        return celsius;
    }

    public String convertKelvinToFahrenheit(String temp) {
        Double kelvin = (Double.valueOf(temp) * 1.8 - 459.67); // °F
        String fahrenheit = String.valueOf(kelvin.intValue());

        return fahrenheit;
    }

    public String convertTime(String time, String dateFormat) {

        Long l = Long.valueOf(time);
        Date date = new Date(l * 1000); // mm
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        String getDateOfUpdate = simpleDateFormat.format(date);

        return getDateOfUpdate;
    }

    public String convertMeterToKilometer(String meter) {
        Double met = Double.parseDouble(meter) / 1000; // km
        DecimalFormat df = new DecimalFormat("0.0");
        String kilometer = String.valueOf(df.format(met));

        return kilometer;
    }

    public String convertMetersPerSecondToKilometersPerHour(String MetersPerSecond) {
        Double metersPerSecond = Double.parseDouble(MetersPerSecond) * 3.6; // km/h
        DecimalFormat df = new DecimalFormat(".0");
        String kilometersPerHour = String.valueOf(df.format(metersPerSecond));

        return kilometersPerHour;
    }

    public float convertDoOutOf(float value) {
        float doOutOf = (float) (Math.ceil(value * 1000) / 1000);
        return doOutOf;
    }

    public int setImageWidget(String groupWeather, String descWeather, String codeIcon) {
        // Clouds
        if (groupWeather.equals("Clouds")) {
            if (descWeather.equals("broken clouds")) {
                if (codeIcon.contains("d")) {
                    return R.drawable.broken_clouds_mor;

                } else {
                    return R.drawable.broken_clouds_night;
                }

            } else if (descWeather.equals("overcast clouds")) {
                return R.drawable.overcast_clouds;

            } else if (descWeather.equals("few clouds")) {
                if (codeIcon.contains("d")) {
                    return R.drawable.few_clouds_mor;
                } else {
                    return R.drawable.few_clouds_night;
                }

            } else if (descWeather.equals("scattered clouds")) {
                if (codeIcon.contains("d")) {
                    return R.drawable.scattered_clouds_mor;
                } else {
                    return R.drawable.scattered_clouds_night;
                }
            }

            // Clear
        } else if (groupWeather.equals("Clear")) {
            if (codeIcon.contains("d")) {
                return R.drawable.clear_sky_mor;
            } else {
                return R.drawable.clear_sky_night;
            }

            // Rain
        } else if (groupWeather.equals("Rain")) {
            if (descWeather.equals("freezing rain")) {
                return R.drawable.freezing_rain;
            } else if (descWeather.contains("shower rain")) {
                return R.drawable.shower_rain;
            } else {
                if (codeIcon.contains("d")) {
                    return R.drawable.rain_mor;
                } else {
                    return R.drawable.rain_night;
                }
            }

            // Drizzle
        } else if (groupWeather.equals("Drizzle")) {
            return R.drawable.shower_rain;

            // Thunderstorm
        } else if (groupWeather.equals("Thunderstorm")) {

            if (descWeather.equals("thunderstorm with light rain") || descWeather.equals("thunderstorm with light drizzle")) {
                if (codeIcon.contains("d"))
                    return R.drawable.thunderstorm_with_light_rain_mor;
                else
                    return R.drawable.thunderstorm_with_light_rain_night;
            } else if (descWeather.equals("thunderstorm") || descWeather.equals("heavy thunderstorm") || descWeather.equals("ragged thunderstorm")) {
                return R.drawable.thunderstorm;
            } else {
                return R.drawable.thunderstorm_with_rain;
            }

            // Atmosphere
        } else if (groupWeather.equals("Mist") || groupWeather.equals("Smoke") || groupWeather.equals("Haze") || groupWeather.equals("Fog")) {
            return R.drawable.atmosphere_icon;

        } else if (groupWeather.equals("Dust") || groupWeather.equals("Sand") || groupWeather.equals("Ash") || groupWeather.equals("Tornado")) {
            return R.drawable.wind_icon;

        } else if (groupWeather.equals("Squall")) {
            return R.drawable.freezing_rain;

        }// Snow
        else if (groupWeather.equals("Snow")) {
            if (descWeather.equals("light snow") || descWeather.equals("Snow") || descWeather.equals("Heavy snow")) {
                return R.drawable.light_snow;

            } else if (descWeather.equals("Sleet")) {
                return R.drawable.freezing_rain;

            } else {
                return R.drawable.rain_and_snow;
            }
        }
        return 0;
    }

}
