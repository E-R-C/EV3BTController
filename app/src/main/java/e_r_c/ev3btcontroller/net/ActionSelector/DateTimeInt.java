package e_r_c.ev3btcontroller.net.ActionSelector;

import org.joda.time.LocalDateTime;

public class DateTimeInt {
        private LocalDateTime ldt;
        private int stamp;

    public DateTimeInt(LocalDateTime ldt, int stamp) {
        this.ldt = ldt;
        this.stamp = stamp;
    }

    public LocalDateTime getTime() {return ldt;}
    public int getStamp() {return stamp;}

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(25);
        String slash = "/";
        sb.append(ldt.getMonthOfYear()); // 2
        sb.append(slash); // 1
        sb.append(ldt.getDayOfMonth()); // 2
        sb.append(slash); // 1
        sb.append(ldt.getYear()); // 4
        sb.append(" "); // 1
        sb.append(ldt.getHourOfDay()); // 2
        sb.append(":"); // 1
        sb.append((ldt.getMinuteOfHour())); // 2
        sb.append(" Rev: "); // 6
        sb.append(stamp); // 3
        return sb.toString();
    }

}
