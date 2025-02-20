package com.example.winlowcustomer.modal;

//import static com.example.winlowcustomer.modal.CartRecyclerViewAdapter.checkoutProductList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.CartDTO;
import com.example.winlowcustomer.dto.GetReceiptItems;

import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.content.FileProvider;

import com.example.winlowcustomer.dto.WeightCategoryDTO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PdfOperations {
    static List<CartDTO> productList1;
    public static void printReceipt(String htmlContent,Activity activity) {

        WebView webView = new WebView(activity);
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
                PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter("Receipt");
                printManager.print("Receipt", adapter, new PrintAttributes.Builder().build());
            }
        });
    }
    
    private static ArrayList<GetReceiptItems> getReceiptItems(List<CartDTO> productList){

        ArrayList<GetReceiptItems> items = new ArrayList<>();
        for(CartDTO cartDTO : productList){

            double totalWeightPrice = 0.0;

            List<WeightCategoryDTO> weightCategoryDTOList = cartDTO.getProduct().getWeightCategoryDTOList();
            for(WeightCategoryDTO weightCategoryDTO : weightCategoryDTOList){

                GetReceiptItems item = new GetReceiptItems();
                item.setId(cartDTO.getProduct().getId());
                item.setName(cartDTO.getProduct().getName());
                item.setWeight(String.valueOf(weightCategoryDTO.getWeight()));
                item.setUnitPrice(String.valueOf(weightCategoryDTO.getUnitPrice()));
                

                int qty = cartDTO.getCartWeightCategoryDTOList().get(0).getQty();
                totalWeightPrice += weightCategoryDTO.getUnitPrice() * qty;

                item.setQty(String.valueOf(qty));
                item.setTotalWeightPrice("Rs. "+String.valueOf(totalWeightPrice));
                
                items.add(item);

            }

        }
        
        return items;
        
    }

    public static String generateReceiptHtml(String customerName, String orderId, String total,List<CartDTO> productList) {

        productList1 = productList;
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>Purchase Receipt</h2>");
        html.append("<p>Customer: ").append(customerName).append("</p>");
        html.append("<h2>Order Id: ").append(orderId).append("</h2>");

        html.append("<table border='1'>" +
                "<tr>" +
                    "<th>#</th>" +
                    "<th>Product</th>" +
                    "<th>Weight</th>" +
                    "<th>Unit Price</th>" +
                    "<th>Quantity</th>" +
                    "<th>Price</th>" +
                "</tr>");

        ArrayList<GetReceiptItems> items = getReceiptItems(productList);
        
        for (GetReceiptItems item : items) {

            html.append("<tr>");

            // product name
            html.append("<td>").append(item.getId()).append("</td>");
            html.append("<td>").append(item.getName()).append("</td>");
            html.append("<td>").append(item.getWeight()).append("</td>");
            html.append("<td>").append(item.getUnitPrice()).append("</td>");
            html.append("<td>").append(item.getQty()).append("</td>");
            html.append("<td>").append(item.getTotalWeightPrice()).append("</td>");

            html.append("</tr>");

        }

        html.append("</table>");
        html.append("<h3>Total: ").append(total).append("</h3>");
        html.append("</body></html>");

        return html.toString();
    }

    public static void emailReceipt(Context context, String htmlReceipt,String subject,String[] email, Activity activity) {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(htmlReceipt, Html.FROM_HTML_MODE_COMPACT));
//        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//        Intent sendEmail = ;
        activity.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.send_email)));
//        activity.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));

    }

    public static void shareReceipt(Context context, File receiptFile, Activity activity) {
        if (receiptFile == null || !receiptFile.exists()) {
            return; // File not found
        }

        Uri pdfUri = FileProvider.getUriForFile(context, "com.example.winlowcustomer", receiptFile);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, pdfUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activity.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
    }
    public static File generateReceiptPDF(Context context, String customerName, String orderId, String total) {
        File receiptFile = null;
        try {
            // Define file path (make sure this directory exists, you can modify it as needed)
            File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Receipts");
            if (!dir.exists()) dir.mkdirs();  // Ensure directory exists

            receiptFile = new File(dir, "receipt.pdf");

            // Writing to the PDF
            PdfWriter writer = new PdfWriter(new FileOutputStream(receiptFile));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add Title & Details
            document.add(new Paragraph("Purchase Receipt").setBold().setFontSize(18));
            document.add(new Paragraph("Customer: " + customerName).setFontSize(14));
            document.add(new Paragraph("Order Id: " + orderId).setFontSize(14));

            // Create Table
            float[] columnWidths = {50f, 150f, 80f, 80f, 80f, 80f};
            Table table = new Table(columnWidths);
            table.addCell("#");
            table.addCell("Product");
            table.addCell("Weight");
            table.addCell("Unit Price");
            table.addCell("Quantity");
            table.addCell("Price");

            ArrayList<GetReceiptItems> receiptItems = getReceiptItems(productList1);
            for (GetReceiptItems item : receiptItems) {
                table.addCell(item.getId());
                table.addCell(item.getName());
                table.addCell(item.getWeight());
                table.addCell(item.getUnitPrice());
                table.addCell(item.getQty());
                table.addCell(item.getTotalWeightPrice());
            }

            // Add Total Price
            document.add(table);
            document.add(new Paragraph("\nTotal: " + total).setBold());

            // Close document
            document.close();
            Log.d("Receipt", "Receipt generated at: " + receiptFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return receiptFile;
    }

//    public static File generateReceiptPDF(Context context, String customerName, String orderId, String total) {
//        File receiptFile = null;
//        try {
//            // Define file path
//            File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Receipts");
//            if (!dir.exists()) dir.mkdirs();  // Ensure directory exists
//
//            receiptFile = new File(dir, "receipt.pdf");
//
//            PdfWriter writer = new PdfWriter(new FileOutputStream(receiptFile));
//            PdfDocument pdfDoc = new PdfDocument(writer);
//            Document document = new Document(pdfDoc);
//
//            // Add Title & Details
//            document.add(new Paragraph("Purchase Receipt").setBold().setFontSize(18));
//            document.add(new Paragraph("Customer: " + customerName).setFontSize(14));
//            document.add(new Paragraph("Order Id: " + orderId).setFontSize(14));
//
//            // Create Table
//            float[] columnWidths = {50f, 150f, 80f, 80f, 80f, 80f};
//            Table table = new Table(columnWidths);
//            table.addCell("#");
//            table.addCell("Product");
//            table.addCell("Weight");
//            table.addCell("Unit Price");
//            table.addCell("Quantity");
//            table.addCell("Price");
//
//            ArrayList<GetReceiptItems> receiptItems = getReceiptItems();
//            for (GetReceiptItems item : receiptItems) {
//                table.addCell(item.getId());
//                table.addCell(item.getName());
//                table.addCell(item.getWeight());
//                table.addCell(item.getUnitPrice());
//                table.addCell(item.getQty());
//                table.addCell(item.getTotalWeightPrice());
//            }
//
//            // Add Total Price
//            document.add(table);
//            document.add(new Paragraph("\nTotal: " + total).setBold());
//
//            // Close document
//            document.close();
//            Log.d("Receipt", "Receipt generated at: " + receiptFile.getAbsolutePath());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return receiptFile;
//    }
}
