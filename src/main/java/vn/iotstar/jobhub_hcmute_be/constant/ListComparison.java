package vn.iotstar.jobhub_hcmute_be.constant;

import java.util.List;

public class ListComparison {
    public int countSimilarElements(List<?> list1, List<?> list2) {
        int count = 0;
        for (Object element : list1) {
            if (list2.contains(element)) {
                count++;
            }
        }
        return count;
    }

    public double calculateSimilarityRatio(int count, int size1, int size2) {
        double ratio1 = (double) count / size1;
        double ratio2 = (double) count / size2;
        return (ratio1 + ratio2) / 2;
    }
}
