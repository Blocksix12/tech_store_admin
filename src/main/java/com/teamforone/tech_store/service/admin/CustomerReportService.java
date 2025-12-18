package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.response.CustomerReportDTO;
import com.teamforone.tech_store.model.User;
import com.teamforone.tech_store.repository.admin.crud.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerReportService {

    private final UserRepository userRepository;

    /**
     * Lấy thống kê tổng quan khách hàng
     */
    public Map<String, Object> getCustomerStatistics() {
        // Tổng khách hàng
        long totalCustomers = userRepository.count();

        // Khách VIP
        long vipCustomers = userRepository.countByCustomerType(User.CustomerType.VIP);

        // Khách hàng mới trong 30 ngày
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = cal.getTime();
        long newCustomers = userRepository.countNewCustomers(thirtyDaysAgo);

        // Tính tỷ lệ quay lại (khách có >= 2 đơn / tổng khách có đơn)
        long totalWithOrders = userRepository.countCustomersWithOrders();
        long returningCustomers = userRepository.countReturningCustomers();

        String returnRate = "0%";
        if (totalWithOrders > 0) {
            double rate = (returningCustomers * 100.0) / totalWithOrders;
            returnRate = String.format("%.0f%%", rate);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", totalCustomers);
        stats.put("vipCustomers", vipCustomers);
        stats.put("newCustomers", newCustomers);
        stats.put("returnRate", returnRate);

        return stats;
    }

    /**
     * Lấy danh sách top khách hàng
     */
    public List<CustomerReportDTO> getTopCustomers(int limit) {
        List<User> topUsers = userRepository.findTop10ByOrderByTotalSpentDesc();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        return topUsers.stream()
                .limit(limit)
                .map(user -> CustomerReportDTO.builder()
                        .userId(user.getId())
                        .fullName(user.getFullname())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .customerType(user.getCustomerType().name())
                        .totalOrders(user.getTotalOrders())
                        .totalSpent(user.getTotalSpent())
                        .formattedTotalSpent(currencyFormat.format(user.getTotalSpent()) + " ₫")
                        .registeredDate(sdf.format(user.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Xuất Excel báo cáo khách hàng (optional - implement sau nếu cần)
     */
    // public void exportCustomerReport(HttpServletResponse response) throws IOException {
    //     // Implementation here
    // }
}