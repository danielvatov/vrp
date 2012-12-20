package net.vatov.math.utils;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Distance {
    /**
     * 
     * @param coordinatesM
     *            (n,m) matrix with coordinates of n vectors in m-dimensional
     *            euclidean space.
     * @return (n,n) matrix with the distances between each pair of vectors
     */
    public static RealMatrix computeEuclideanDistances(RealMatrix coordinatesM) {
        final int DIMENSIONS = coordinatesM.getColumnDimension();
        if (0 != (DIMENSIONS % 2)) {
            throw new DimensionMismatchException(DIMENSIONS, DIMENSIONS + 1);
        }
        final int VECTORS_COUNT = coordinatesM.getRowDimension(); 
        if (2 > VECTORS_COUNT) {
            throw new DimensionMismatchException(VECTORS_COUNT, 2);
        }
        RealMatrix ret = MatrixUtils.createRealMatrix(VECTORS_COUNT, VECTORS_COUNT);
        
        for (int v1 = 0; v1<VECTORS_COUNT; ++v1) {
            for (int v2 = v1 + 1; v2<VECTORS_COUNT; ++v2) {
                RealVector left = coordinatesM.getRowVector(v1);
                RealVector right = coordinatesM.getRowVector(v2);
                ret.setEntry(v1, v2, left.getDistance(right));
                ret.setEntry(v2, v1, right.getDistance(left));
            }
        }
        return ret;
    }
}
