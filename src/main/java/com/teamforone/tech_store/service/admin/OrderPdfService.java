package com.teamforone.tech_store.service.admin;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.teamforone.tech_store.model.OrderItem;
import com.teamforone.tech_store.model.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OrderPdfService {
    private final OrderItemService orderItemService;

    public byte[] generateOrderListPdf(List<Orders> orders) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // Load font từ resources
            String fontPath = "fonts/times.ttf";
            PdfFont font = PdfFontFactory.createFont(
                    getClass().getClassLoader().getResource(fontPath).getPath(),
                    PdfEncodings.IDENTITY_H
            );
            document.setFont(font);
        } catch (Exception e) {
            System.out.println("Could not load font: " + e.getMessage());
        }

        addHeader(document);
        addStatistics(document, orders);
        addOrderTable(document, orders);
        addFooter(document);

        document.close();
        return baos.toByteArray();
    }

    public byte[] generateOrderDetailPdf(Orders order) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
        document.setFont(font);

        addDetailHeader(document, order);
        addOrderDetails(document, order);

        List<OrderItem> items = orderItemService.getOrderItemsByOrderId(order.getOrderID());
        if (items != null && !items.isEmpty()) {
            addOrderItemsTable(document, items);
        }

        document.close();
        return baos.toByteArray();
    }

    private void addOrderItemsTable(Document document, List<OrderItem> items) {
        Paragraph itemsHeader = new Paragraph("CHI TIẾT SẢN PHẨM")
                .setFontSize(14)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(10);
        document.add(itemsHeader);

        float[] columnWidths = {8, 22, 20, 20, 15, 15};
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth();

        table.addHeaderCell(createHeaderCell("STT"));
        table.addHeaderCell(createHeaderCell("Màu sắc"));
        table.addHeaderCell(createHeaderCell("Kích thước màn hình"));
        table.addHeaderCell(createHeaderCell("RAM/ROM"));
        table.addHeaderCell(createHeaderCell("Số lượng"));
        table.addHeaderCell(createHeaderCell("Thành tiền"));

        int index = 1;
        for (OrderItem item : items) {
            table.addCell(createDataCell(String.valueOf(index++)));

            // Màu sắc
            String colorName = item.getColor() != null && item.getColor().getColorName() != null
                    ? item.getColor().getColorName()
                    : "N/A";
            table.addCell(createDataCell(colorName));

            // Kích thước màn hình
            String displayInfo = "N/A";
            if (item.getDisplaySize() != null) {
                if (item.getDisplaySize().getSizeInch() != null) {
                    displayInfo = item.getDisplaySize().getSizeInch() + "\"";
                    if (item.getDisplaySize().getResolution() != null) {
                        displayInfo += " - " + item.getDisplaySize().getResolution();
                    }
                }
            }
            table.addCell(createDataCell(displayInfo));

            // RAM/ROM
            String storageInfo = "N/A";
            if (item.getStorage() != null) {
                String ram = item.getStorage().getRam() != null ? item.getStorage().getRam() : "";
                String rom = item.getStorage().getRom() != null ? item.getStorage().getRom() : "";
                if (!ram.isEmpty() && !rom.isEmpty()) {
                    storageInfo = ram + "/" + rom;
                } else if (!ram.isEmpty()) {
                    storageInfo = ram;
                } else if (!rom.isEmpty()) {
                    storageInfo = rom;
                }
            }
            table.addCell(createDataCell(storageInfo));

            // Số lượng
            table.addCell(createDataCell(String.valueOf(item.getQuantity() != null ? item.getQuantity() : 0)));

            // Thành tiền
            table.addCell(createDataCell(formatCurrency(item.getSubTotal())));
        }

        document.add(table);
    }

    private void addHeader(Document document) {
        Paragraph header = new Paragraph("TECH STORE")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(102, 126, 234));

        Paragraph subHeader = new Paragraph("DANH SÁCH ĐƠN HÀNG")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);

        Paragraph date = new Paragraph("Ngày xuất: " + getCurrentDateTime())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);

        document.add(header);
        document.add(subHeader);
        document.add(date);
    }

    private void addStatistics(Document document, List<Orders> orders) {
        int totalOrders = orders.size();
        int pendingOrders = (int) orders.stream()
                .filter(o -> "PENDING".equals(o.getStatus().toString()))
                .count();
        int deliveredOrders = (int) orders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus().toString()))
                .count();

        double totalRevenue = orders.stream()
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() : 0)
                .sum();

        Table statsTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        statsTable.addCell(createStatCell("Tổng đơn", totalOrders + ""));
        statsTable.addCell(createStatCell("Chờ xử lý", pendingOrders + ""));
        statsTable.addCell(createStatCell("Đã giao", deliveredOrders + ""));
        statsTable.addCell(createStatCell("Tổng doanh thu", formatCurrency(totalRevenue)));

        document.add(statsTable);
    }

    private Cell createStatCell(String label, String value) {
        Paragraph labelPara = new Paragraph(label)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);

        Paragraph valuePara = new Paragraph(value)
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(102, 126, 234));

        return new Cell()
                .add(labelPara)
                .add(valuePara)
                .setBackgroundColor(new DeviceRgb(247, 250, 252))
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private void addOrderTable(Document document, List<Orders> orders) {
        float[] columnWidths = {15, 20, 20, 15, 15, 15};
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth();

        table.addHeaderCell(createHeaderCell("STT"));
        table.addHeaderCell(createHeaderCell("Mã đơn hàng"));
        table.addHeaderCell(createHeaderCell("Khách hàng"));
        table.addHeaderCell(createHeaderCell("Tổng tiền"));
        table.addHeaderCell(createHeaderCell("Thanh toán"));
        table.addHeaderCell(createHeaderCell("Trạng thái"));

        int index = 1;
        for (Orders order : orders) {
            table.addCell(createDataCell(String.valueOf(index++)));
            table.addCell(createDataCell(order.getOrderNo() != null ? order.getOrderNo() : "N/A"));

            // FIX: Xử lý User object
            String customerName = "N/A";
            if (order.getUser() != null) {
                customerName = order.getUser().getFullname() != null
                        ? order.getUser().getFullname()
                        : "Khách lẻ";
            }
            table.addCell(createDataCell(customerName));

            table.addCell(createDataCell(formatCurrency(order.getTotalAmount())));
            table.addCell(createDataCell(formatPaymentMethod(order.getPaymentMethod())));
            table.addCell(createStatusCell(order.getStatus().toString()));
        }

        document.add(table);
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontSize(11))
                .setBackgroundColor(new DeviceRgb(102, 126, 234))
                .setFontColor(ColorConstants.WHITE)
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createDataCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(10))
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createStatusCell(String status) {
        DeviceRgb bgColor;
        String statusText;

        switch (status) {
            case "PENDING":
                bgColor = new DeviceRgb(254, 243, 199);
                statusText = "Chờ xử lý";
                break;
            case "PROCESSING":
                bgColor = new DeviceRgb(219, 234, 254);
                statusText = "Đang xử lý";
                break;
            case "SHIPPED":
                bgColor = new DeviceRgb(224, 231, 255);
                statusText = "Đang giao";
                break;
            case "DELIVERED":
                bgColor = new DeviceRgb(209, 250, 229);
                statusText = "Đã giao";
                break;
            case "CANCELLED":
                bgColor = new DeviceRgb(254, 226, 226);
                statusText = "Đã hủy";
                break;
            default:
                bgColor = new DeviceRgb(243, 244, 246);
                statusText = status;
        }

        return new Cell()
                .add(new Paragraph(statusText).setFontSize(10).setBold())
                .setBackgroundColor(bgColor)
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private void addDetailHeader(Document document, Orders order) {
        Paragraph header = new Paragraph("TECH STORE")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(102, 126, 234));

        Paragraph subHeader = new Paragraph("HÓA ĐƠN BÁN HÀNG")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);

        Paragraph orderNo = new Paragraph("Mã đơn: " + (order.getOrderNo() != null ? order.getOrderNo() : "N/A"))
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);

        document.add(header);
        document.add(subHeader);
        document.add(orderNo);
    }

    private void addOrderDetails(Document document, Orders order) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Table detailTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        detailTable.addCell(createDetailLabelCell("Ngày tạo:"));
        detailTable.addCell(createDetailValueCell(
                order.getCreatedAt() != null ? sdf.format(order.getCreatedAt()) : "N/A"));

        // FIX: Xử lý User object
        String customerName = "N/A";
        if (order.getUser() != null) {
            customerName = order.getUser().getFullname() != null
                    ? order.getUser().getFullname()
                    : "Khách lẻ";
        }
        detailTable.addCell(createDetailLabelCell("Khách hàng:"));
        detailTable.addCell(createDetailValueCell(customerName));

        detailTable.addCell(createDetailLabelCell("Thanh toán:"));
        detailTable.addCell(createDetailValueCell(formatPaymentMethod(order.getPaymentMethod())));

        detailTable.addCell(createDetailLabelCell("Trạng thái:"));
        detailTable.addCell(createDetailValueCell(
                order.getStatus() != null ? order.getStatus().toString() : "N/A"));

        detailTable.addCell(createDetailLabelCell("Tổng tiền:"));
        detailTable.addCell(createDetailValueCell(formatCurrency(order.getTotalAmount()))
                .setBold()
                .setFontSize(14));

        document.add(detailTable);
    }

    private void addOrderItems(Document document, Orders order) {
        Paragraph itemsHeader = new Paragraph("CHI TIẾT SẢN PHẨM")
                .setFontSize(14)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(10);

        document.add(itemsHeader);
    }

    private Cell createDetailLabelCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontSize(11))
                .setBackgroundColor(new DeviceRgb(247, 250, 252))
                .setPadding(8);
    }

    private Cell createDetailValueCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(11))
                .setPadding(8);
    }

    private void addFooter(Document document) {
        Paragraph footer = new Paragraph("\n--- Cảm ơn quý khách đã sử dụng dịch vụ ---")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20)
                .setItalic();

        document.add(footer);
    }

    private String formatCurrency(Object amount) {
        if (amount == null) return "0 đ";
        double value = amount instanceof Number ? ((Number) amount).doubleValue() : 0;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(value);
    }

    private String formatPaymentMethod(Orders.PaymentMethod method) {
        if (method == null) return "N/A";
        switch (method) {
            case COD: return "Tiền mặt";
            case VNPAY: return "VNPay";
            case MOMO: return "MoMo";
            case STRIPE: return "Stripe";
            default: return method.toString();
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new java.util.Date());
    }
}