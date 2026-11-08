package net.chaolux.delightlegacy.common;

public class BlockShapes {
    public static final double[] TRAY_OUTER_SHAPE;
    public static final double[] TRAY_INNER_SHAPE;
    public static final double[][] TRAY_SHAPE;
    public static final double[][][] ROAST_CHICKEN_SHAPES;
    public static final double[][][] HONEY_GLAZED_HAM_SHAPES;
    public static final double[][][] SHEPHERDS_PIE_SHAPES;

    static {
        TRAY_OUTER_SHAPE=new double[]{1,0,1,15,2,15};
        TRAY_INNER_SHAPE=new double[]{2,1,2,14,2,14};
        TRAY_SHAPE= new double[][]{{1,0,1,15,1,15},{1,1,1,2,2,15},{14,1,1,15,2,15},{2,1,1,14,2,2},{2,1,14,14,2,15}};
        ROAST_CHICKEN_SHAPES= new double[][][]{{{4,1,10,12,8,12}},{{4,1,8,12,8,12}},{{4,1,6,12,8,12}},{{4,1,4,12,8,12}}};
        HONEY_GLAZED_HAM_SHAPES= new double[][][]{{{4,1,3,12,3,11}},{{4,1,6,12,9,10}},{{4,2,4,12,10,10}},{{4,2,2,12,10,10}}};
        SHEPHERDS_PIE_SHAPES= new double[][][]{{{2,1,8,8,8,14}},{{2,1,8,14,8,14}},{{8,1,2,14,8,8},{2,1,8,14,8,14}},{{2,1,2,14,8,14}}};
    }
}
