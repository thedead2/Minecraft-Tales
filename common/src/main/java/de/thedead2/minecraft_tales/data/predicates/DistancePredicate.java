package de.thedead2.minecraft_tales.data.predicates;


public class DistancePredicate implements SimpleTriggerPredicate<DistancePredicate.DistanceInfo> {
    
    public static final DistancePredicate ANY = new DistancePredicate(MinMax.ANY_DOUBLE, MinMax.ANY_DOUBLE, MinMax.ANY_DOUBLE, MinMax.ANY_DOUBLE, MinMax.ANY_DOUBLE);

    private final MinMax<Double> x;

    private final MinMax<Double> y;

    private final MinMax<Double> z;

    private final MinMax<Double> horizontal;

    private final MinMax<Double> absolute;


    public DistancePredicate(MinMax<Double> x, MinMax<Double> y, MinMax<Double> z, MinMax<Double> horizontal, MinMax<Double> absolute) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.horizontal = horizontal;
        this.absolute = absolute;
    }


    @Override
    public boolean matches(DistanceInfo distanceInfo) {
        double xDistance = distanceInfo.getXDistance();
        double yDistance = distanceInfo.getYDistance();
        double zDistance = distanceInfo.getZDistance();

        if(this.x.matches(Math.abs(xDistance)) && this.y.matches(Math.abs(yDistance)) && this.z.matches(Math.abs(zDistance))) {
            if(!this.horizontal.matchesSqr(xDistance * xDistance + zDistance * zDistance)) {
                return false;
            }
            else {
                return this.absolute.matchesSqr(xDistance * xDistance + yDistance * yDistance + zDistance * zDistance);
            }
        }
        else {
            return false;
        }
    }

    public static class DistanceInfo {

        private final double xDistance;

        private final double yDistance;

        private final double zDistance;


        public DistanceInfo(Number x1, Number y1, Number z1, Number x2, Number y2, Number z2) {
            this(x1.doubleValue() - x2.doubleValue(), y1.doubleValue() - y2.doubleValue(), z1.doubleValue() - z2.doubleValue());
        }


        public DistanceInfo(double xDistance, double yDistance, double zDistance) {
            this.xDistance = xDistance;
            this.yDistance = yDistance;
            this.zDistance = zDistance;
        }


        public double getXDistance() {
            return xDistance;
        }


        public double getYDistance() {
            return yDistance;
        }


        public double getZDistance() {
            return zDistance;
        }
    }
}
