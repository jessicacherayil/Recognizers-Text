package com.microsoft.recognizers.text.datetime.utilities;

import com.google.common.collect.ImmutableMap;
import com.microsoft.recognizers.text.datetime.Constants;
import com.microsoft.recognizers.text.datetime.DatePeriodTimexType;
import com.microsoft.recognizers.text.utilities.StringUtility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimexUtility {

    public static String generateDatePeriodTimex(LocalDateTime begin, LocalDateTime end, DatePeriodTimexType timexType) {
        String datePeriodTimex;

        if (timexType == DatePeriodTimexType.ByDay) {
            datePeriodTimex = "P" + ChronoUnit.DAYS.between(begin, end) + "D";
        } else if (timexType == DatePeriodTimexType.ByWeek) {
            datePeriodTimex = "P" + (ChronoUnit.WEEKS.between(begin, end)) + "W";
        } else if (timexType == DatePeriodTimexType.ByMonth) {
            datePeriodTimex = "P" + ChronoUnit.MONTHS.between(begin, end) + "M"; // ((end.getYear() - begin.getYear()) * 12) + (end.getMonthValue() - begin.getMonthValue());
        } else {
            double yearDiff = (end.getYear() - begin.getYear()) + (end.getMonthValue() - begin.getMonthValue()) / 12.0;
            datePeriodTimex = "P" + yearDiff + "Y";
        }

        return "(" + FormatUtil.luisDate(begin) + "," + FormatUtil.luisDate(end) + "," + datePeriodTimex + ")";
    }

    public static String generateWeekTimex() {
        return "XXXX-WXX";
    }

    public static String generateWeekTimex(LocalDateTime monday) {
        int isoWeek = LocalDate.of(monday.getYear(), monday.getMonthValue(), monday.getDayOfMonth()).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return String.format("%04d-W%02d", monday.getYear(), isoWeek);
    }

    public static String generateWeekendTimex() {
        return "XXXX-WXX-WE";
    }

    public static String generateWeekendTimex(LocalDateTime date) {
        int isoWeek = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth()).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return String.format("%04d-W%02d-WE", date.getYear(), isoWeek);
    }

    public static String generateMonthTimex() {
        return "XXXX-XX";
    }

    public static String generateMonthTimex(LocalDateTime date) {
        return String.format("%04d-%02d", date.getYear(), date.getMonthValue());
    }

    public static String generateYearTimex() {
        return "XXXX";
    }

    public static String generateYearTimex(LocalDateTime date) {
        return String.format("%04d", date.getYear());
    }

    public static String generateCompoundDurationTimex(Map<String, String> unitToTimexComponents, ImmutableMap<String, Long> unitValueMap) {
        List<String> unitList = new ArrayList<>(unitToTimexComponents.keySet());
        unitList.sort((x, y) -> unitValueMap.get(x) < unitValueMap.get(y) ? 1 : -1);
        boolean isTimeDurationAlreadyExist = false;
        StringBuilder timexBuilder = new StringBuilder(Constants.GeneralPeriodPrefix);

        for (String unitKey : unitList) {
            String timexComponent = unitToTimexComponents.get(unitKey);

            // The Time Duration component occurs first time
            if (!isTimeDurationAlreadyExist && isTimeDurationTimex(timexComponent)) {
                timexBuilder.append(Constants.TimeTimexPrefix);
                timexBuilder.append(getDurationTimexWithoutPrefix(timexComponent));
                isTimeDurationAlreadyExist = true;
            } else {
                timexBuilder.append(getDurationTimexWithoutPrefix(timexComponent));
            }
        }
        return timexBuilder.toString();
    }

    private static boolean isTimeDurationTimex(String timex) {
        return timex.startsWith(Constants.GeneralPeriodPrefix + Constants.TimeTimexPrefix);
    }

    private static String getDurationTimexWithoutPrefix(String timex) {
        // Remove "PT" prefix for TimeDuration, Remove "P" prefix for DateDuration
        return timex.substring(isTimeDurationTimex(timex) ? 2 : 1);
    }

    public static String generateDurationTimex(double number, String unitStr, boolean isLessThanDay) {
        if (!Constants.TimexBusinessDay.equals(unitStr)) {
            if (Constants.DECADE_UNIT.equals(unitStr)) {
                number = number * 10;
                unitStr = Constants.TimexYear;

            } else {
                unitStr = unitStr.substring(0, 1);
            }
        }

        return  String.format("%s%s%s%s",
                Constants.GeneralPeriodPrefix,
                isLessThanDay ? Constants.TimeTimexPrefix : "",
                StringUtility.format(number),
                unitStr);
    }

    public static TimeOfDayResolutionResult parseTimeOfDay(String tod) {
        switch (tod) {
            case Constants.EarlyMorning:
                return new TimeOfDayResolutionResult(Constants.EarlyMorning, 4, 8, 0);
            case Constants.Morning:
                return new TimeOfDayResolutionResult(Constants.Morning, 8, 12, 0);
            case Constants.Afternoon:
                return new TimeOfDayResolutionResult(Constants.Afternoon, 12, 16, 0);
            case Constants.Evening:
                return new TimeOfDayResolutionResult(Constants.Evening, 16, 20, 0);
            case Constants.Daytime:
                return new TimeOfDayResolutionResult(Constants.Daytime, 8, 18, 0);
            case Constants.BusinessHour:
                return new TimeOfDayResolutionResult(Constants.BusinessHour, 8, 18, 0);
            case Constants.Night:
                return new TimeOfDayResolutionResult(Constants.Night, 20, 23, 59);
            default:
                return new TimeOfDayResolutionResult();
        }
    }
}
