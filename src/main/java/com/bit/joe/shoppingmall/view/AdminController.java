package com.bit.joe.shoppingmall.view;

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
        String sql =
                "SELECT u.gender, c.category_name AS categoryName, "
                        + "ROUND(AVG(COALESCE(oi.price * oi.quantity, 0))) AS averageSales, "
                        + // Round average sales
                        "ROUND(SUM(COALESCE(oi.price * oi.quantity, 0))) AS totalSales "
                        + // Round total sales
                        "FROM users u "
                        + "JOIN orders o ON u.id = o.user_id "
                        + "JOIN order_items oi ON o.id = oi.order_id "
                        + "JOIN products p ON oi.product_id = p.product_id "
                        + "JOIN categories c ON p.category_id = c.category_id "
                        + "GROUP BY u.gender, c.category_name"; // Group by gender and category

        List<Map<String, Object>> salesData = jdbcTemplate.queryForList(sql);

        // Log the result to verify the data
        System.out.println("Sales by Gender and Category (Avg and Total): " + salesData);

        model.addAttribute("salesData", salesData);
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
