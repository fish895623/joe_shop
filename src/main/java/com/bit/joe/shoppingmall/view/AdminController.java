package com.bit.joe.shoppingmall.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/insight")
    public String showDashboard(Model model) {
        String sql = "SELECT u.gender, c.category_name AS categoryName, " +
                "AVG(COALESCE(oi.price * oi.quantity, 0)) AS averageSales " +
                "FROM users u " +
                "JOIN orders o ON u.id = o.user_id " +
                "JOIN order_items oi ON o.id = oi.order_id " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "JOIN categories c ON p.category_id = c.category_id " +
                "GROUP BY u.gender, c.category_name";

        List<Map<String, Object>> averageSales = jdbcTemplate.queryForList(sql);
        model.addAttribute("averageSales", averageSales);

        return "admin/insight";
    }

    @GetMapping("/inventory")
    public String inventory() {
        return "admin/inventory";
    }

    @GetMapping("/member")
    public String member() {
        return "admin/member";
    }
}
