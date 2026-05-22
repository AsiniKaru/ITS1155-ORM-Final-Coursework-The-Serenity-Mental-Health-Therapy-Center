package lk.ijse.the_serenity_mental_health_therapy_center.util;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public class ReportUtil {
    /**
     * Compiles a JRXML file from resources, fills it with parameters and a bean collection data source,
     * and shows it in the JasperViewer without closing the application on exit.
     */
    public static void showReport(String jrxmlResourcePath, Map<String, Object> parameters, Collection<?> data) {
        try {
            String jasperResourcePath = jrxmlResourcePath.replace(".jrxml", ".jasper");
            InputStream reportStream = ReportUtil.class.getResourceAsStream(jasperResourcePath);
            JasperReport jasperReport;
            if (reportStream != null) {
                // Load precompiled report
                jasperReport = (JasperReport) net.sf.jasperreports.engine.util.JRLoader.loadObject(reportStream);
            } else {
                // Fall back to compiling JRXML dynamically
                reportStream = ReportUtil.class.getResourceAsStream(jrxmlResourcePath);
                if (reportStream == null) {
                    throw new RuntimeException("Report template not found in resources: " + jrxmlResourcePath);
                }
                jasperReport = JasperCompileManager.compileReport(reportStream);
            }
            
            JRDataSource dataSource = (data == null || data.isEmpty())
                    ? new JREmptyDataSource()
                    : new JRBeanCollectionDataSource(data);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            // Run on Swing thread to ensure UI safety
            java.awt.EventQueue.invokeLater(() -> {
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setTitle("Serenity Mental Health Therapy Center - Report Viewer");
                viewer.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate and display report: " + e.getMessage(), e);
        }
    }
}
