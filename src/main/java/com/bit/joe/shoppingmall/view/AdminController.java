package com.bit.joe.shoppingmall.view;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private JdbcTemplate jdbcTemplate;

    @GetMapping("/insight")
    public String showDashboard(Model model) {
        // SQL for average and total sales by category
        String salesSql =
                "SELECT c.category_name AS categoryName, "
                        + "ROUND(AVG(COALESCE(oi.price * oi.quantity, 0))) AS averageSales, "
                        + "ROUND(SUM(COALESCE(oi.price * oi.quantity, 0))) AS totalSales "
                        + "FROM users u "
                        + "JOIN orders o ON u.id = o.user_id "
                        + "JOIN order_items oi ON o.id = oi.order_id "
                        + "JOIN products p ON oi.product_id = p.product_id "
                        + "JOIN categories c ON p.category_id = c.category_id "
                        + "GROUP BY c.category_name "
                        + "ORDER BY totalSales DESC";

        // SQL for top sales products by category
        String topSalesSql =
                "SELECT "
                        + "c.category_name AS categoryName, "
                        + "(SELECT p1.name "
                        + " FROM products p1 "
                        + " LEFT JOIN order_items oi1 ON oi1.product_id = p1.product_id "
                        + " WHERE p1.category_id = c.category_id "
                        + " GROUP BY p1.product_id "
                        + " ORDER BY SUM(oi1.price * oi1.quantity) DESC "
                        + " LIMIT 1) AS firstProduct, "
                        + "(SELECT ROUND(SUM(oi1.price * oi1.quantity)) "
                        + " FROM order_items oi1 "
                        + " JOIN products p1 ON oi1.product_id = p1.product_id "
                        + " WHERE p1.category_id = c.category_id "
                        + " GROUP BY p1.product_id "
                        + " ORDER BY SUM(oi1.price * oi1.quantity) DESC "
                        + " LIMIT 1) AS firstProductSales, "
                        + "(SELECT p2.name "
                        + " FROM products p2 "
                        + " LEFT JOIN order_items oi2 ON oi2.product_id = p2.product_id "
                        + " WHERE p2.category_id = c.category_id "
                        + " GROUP BY p2.product_id "
                        + " ORDER BY SUM(oi2.price * oi2.quantity) DESC "
                        + " LIMIT 1 OFFSET 1) AS secondProduct, "
                        + "(SELECT ROUND(SUM(oi2.price * oi2.quantity)) "
                        + " FROM order_items oi2 "
                        + " JOIN products p2 ON oi2.product_id = p2.product_id "
                        + " WHERE p2.category_id = c.category_id "
                        + " GROUP BY p2.product_id "
                        + " ORDER BY SUM(oi2.price * oi2.quantity) DESC "
                        + " LIMIT 1 OFFSET 1) AS secondProductSales, "
                        + "(SELECT p3.name "
                        + " FROM products p3 "
                        + " LEFT JOIN order_items oi3 ON oi3.product_id = p3.product_id "
                        + " WHERE p3.category_id = c.category_id "
                        + " GROUP BY p3.product_id "
                        + " ORDER BY SUM(oi3.price * oi3.quantity) DESC "
                        + " LIMIT 1 OFFSET 2) AS thirdProduct, "
                        + "(SELECT ROUND(SUM(oi3.price * oi3.quantity)) "
                        + " FROM order_items oi3 "
                        + " JOIN products p3 ON oi3.product_id = p3.product_id "
                        + " WHERE p3.category_id = c.category_id "
                        + " GROUP BY p3.product_id "
                        + " ORDER BY SUM(oi3.price * oi3.quantity) DESC "
                        + " LIMIT 1 OFFSET 2) AS thirdProductSales "
                        + "FROM categories c "
                        + "ORDER BY (SELECT SUM(oi1.price * oi1.quantity) FROM order_items oi1 "
                        + "JOIN products p1 ON oi1.product_id = p1.product_id WHERE p1.category_id = c.category_id "
                        + "GROUP BY p1.product_id ORDER BY SUM(oi1.price * oi1.quantity) DESC LIMIT 1) DESC";

        // Fetching both sales data and top products data
        List<Map<String, Object>> salesData = jdbcTemplate.queryForList(salesSql);
        List<Map<String, Object>> topSalesData = jdbcTemplate.queryForList(topSalesSql);

        // format with commas
        DecimalFormat df = new DecimalFormat("#,###");

        for (Map<String, Object> row : salesData) {
            row.put("averageSales", df.format(row.get("averageSales")) + " 원");
            row.put("totalSales", df.format(row.get("totalSales")) + " 원");
        }

        for (Map<String, Object> row : topSalesData) {
            row.put("firstProductSales", df.format(row.get("firstProductSales")) + " 원");
            row.put("secondProductSales", df.format(row.get("secondProductSales")) + " 원");
            row.put("thirdProductSales", df.format(row.get("thirdProductSales")) + " 원");
        }

        // Log to verify the results
        System.out.println("Sales by Category (Avg and Total): " + salesData);
        System.out.println("Top Sales Products by Category: " + topSalesData);

        // Add both datasets to the model
        model.addAttribute("salesData", salesData);
        model.addAttribute("topSalesData", topSalesData);

        return "admin/insight"; // Display the results in the dashboard view
    }


    @GetMapping("/inventory")
    public String inventory() {
        return "admin/inventory";
    }

    @GetMapping("/member")
    public String member() {
        return "admin/member";
    }

    @GetMapping("/update-inventory")
    public String updateInventory() {
        return "admin/update-inventory";
    }

    @GetMapping("/add-product")
    public String addProduct() {
        return "admin/add-inventory";
    }
}
