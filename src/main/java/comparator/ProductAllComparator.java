package comparator;

import com.how2java.tmall.pojo.Product;

import java.util.Comparator;

/**
 */
public class ProductAllComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        return p1.getSaleCount() * p1.getReviewCount() - p2.getSaleCount() * p2.getReviewCount();
    }
}
