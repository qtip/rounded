package com.mrdanielsnider.rounded;

public class ArcUtil {
    private static double solve1(double a, double b, double c, double d, double e, double f) {
        return (c * e - b * f) / (a * e - b * d);
    }

    private static double solve2(double a, double b, double c, double d, double e, double f) {
        return (c * d - a * f) / (b * d - a * e);
    }

    public static Arc roundedEdge(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double radius) {

        // compute normal direction vectors for each of the given lines
        double mag_a = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        double mag_b = Math.sqrt((x4 - x3) * (x4 - x3) + (y4 - y3) * (y4 - y3));
        double vec_a_x = (x2 - x1) / mag_a;
        double vec_a_y = (y2 - y1) / mag_a;
        double vec_b_x = (x4 - x3) / mag_b;
        double vec_b_y = (y4 - y3) / mag_b;

        // bisect the given lines at their intersection
        double bisect_x = vec_a_x + vec_b_x;
        double bisect_y = vec_a_y + vec_b_y;

        // flip the bisector so the output circle is inset
        bisect_x *= -1;
        bisect_y *= -1;

        // find the point where the given lines intersect
        double t = solve1(vec_a_x, -vec_b_x, x3 - x1, vec_a_y, -vec_b_y, y3 - y1);
        double intercept_x = x1 + t * vec_a_x;
        double intercept_y = y1 + t * vec_a_y;

        // find the two points tangent to the circle that intersect the given lines
        double u = solve1(vec_a_x, -bisect_x, radius * vec_a_y, vec_a_y, -bisect_y, radius * -vec_a_x);
        double tangent_a_x = -Math.abs(u) * vec_a_x + intercept_x;
        double tangent_a_y = -Math.abs(u) * vec_a_y + intercept_y;
        double tangent_b_x = -Math.abs(u) * vec_b_x + intercept_x;
        double tangent_b_y = -Math.abs(u) * vec_b_y + intercept_y;

        // find a center point for a circle with the given radius, such that the given lines are both tangent to it
        double v = solve2(vec_a_x, -bisect_x, radius * vec_a_y, vec_a_y, -bisect_y, radius * -vec_a_x);
        double center_x = Math.abs(v) * bisect_x + intercept_x;
        double center_y = Math.abs(v) * bisect_y + intercept_y;

        // compute the angle distance between the two tangent points
        double clock_a_x = tangent_a_x - center_x;
        double clock_a_y = tangent_a_y - center_y;
        double clock_b_x = tangent_b_x - center_x;
        double clock_b_y = tangent_b_y - center_y;
        double arcAngle = Math.acos((clock_a_x * clock_b_x + clock_a_y * clock_b_y) / (radius * radius)) * 180.0 / Math.PI;

        return new Arc(center_x, center_y, tangent_a_x, tangent_a_y, arcAngle);
    }

    public static class Arc {
        public double centerX, centerY, startX, startY, arcAngle;

        public Arc(double centerX, double centerY, double startX, double startY, double arcAngle) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.startX = startX;
            this.startY = startY;
            this.arcAngle = arcAngle;
        }
    }
}
