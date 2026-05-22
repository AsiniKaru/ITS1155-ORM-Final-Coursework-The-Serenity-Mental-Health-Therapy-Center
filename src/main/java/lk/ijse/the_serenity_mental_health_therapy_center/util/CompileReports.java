package lk.ijse.the_serenity_mental_health_therapy_center.util;

import net.sf.jasperreports.engine.JasperCompileManager;
import java.io.File;

public class CompileReports {
    public static void main(String[] args) {
        try {
            File reportsDir = new File("src/main/resources/reports");
            if (!reportsDir.exists()) {
                reportsDir = new File("The Serenity Mental Health Therapy Center/src/main/resources/reports");
            }
            System.out.println("Scanning reports directory: " + reportsDir.getAbsolutePath());
            File[] files = reportsDir.listFiles((dir, name) -> name.endsWith(".jrxml"));
            if (files != null) {
                for (File file : files) {
                    String jasperPath = file.getAbsolutePath().replace(".jrxml", ".jasper");
                    System.out.println("Compiling " + file.getName() + " -> " + new File(jasperPath).getName());
                    JasperCompileManager.compileReportToFile(file.getAbsolutePath(), jasperPath);
                }
                System.out.println("Compilation completed successfully.");
            } else {
                System.out.println("No JRXML files found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
