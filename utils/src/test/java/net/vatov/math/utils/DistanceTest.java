package net.vatov.math.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DistanceTest {

    final private RealVector left;
    final private RealVector right;
    final private double expected;
    
    @Parameters
    public static List<?> data() {
        return Arrays.asList(new Object[][] {
                { MatrixUtils.createRealVector(new double[] { 10, 6 }),
                        MatrixUtils.createRealVector(new double[] { 12, 7 }), 2.2361 },
                { MatrixUtils.createRealVector(new double[] { 10, 6 }),
                        MatrixUtils.createRealVector(new double[] { 12, 5 }), 2.2361 },
                { MatrixUtils.createRealVector(new double[] { 10, 6 }),
                        MatrixUtils.createRealVector(new double[] { 0, 0 }), 11.6619 },
                { MatrixUtils.createRealVector(new double[] { 10, 6 }),
                        MatrixUtils.createRealVector(new double[] { 0, 12 }), 11.6619 }

        });
    }

    public DistanceTest(RealVector left, RealVector right, double expected) {
        this.left = left;
        this.right = right;
        this.expected = expected;
    }
    
    @Test
    public final void testComputeEuclideanDistances() {
        RealMatrix m = MatrixUtils.createRealMatrix(2, left.getDimension());
        m.setRowVector(0, left);
        m.setRowVector(1, right);
        RealMatrix distances = Distance.computeEuclideanDistances(m);
        Assert.assertEquals(expected, distances.getEntry(0, 1), 0.00009);
    }
}
