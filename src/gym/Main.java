package gym;

import gym.ui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Entry point for the desktop GUI application.
 * All actual UI is built in gym.ui.MainFrame; this class only starts it
 * on Swing's event dispatch thread (required for any Swing app).
 */
public class Main {
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    
                    // Custom Theme Overrides within standard scope
                    javax.swing.UIManager.put("control", new java.awt.Color(245, 246, 250));
                    javax.swing.UIManager.put("info", new java.awt.Color(245, 246, 250));
                    javax.swing.UIManager.put("nimbusBase", new java.awt.Color(41, 128, 185));
                    javax.swing.UIManager.put("nimbusAlertYellow", new java.awt.Color(248, 187, 86));
                    javax.swing.UIManager.put("nimbusDisabledText", new java.awt.Color(128, 128, 128));
                    javax.swing.UIManager.put("nimbusFocus", new java.awt.Color(41, 128, 185));
                    javax.swing.UIManager.put("nimbusGreen", new java.awt.Color(46, 204, 113));
                    javax.swing.UIManager.put("nimbusInfoBlue", new java.awt.Color(52, 152, 219));
                    javax.swing.UIManager.put("nimbusLightBackground", new java.awt.Color(255, 255, 255));
                    javax.swing.UIManager.put("nimbusOrange", new java.awt.Color(230, 126, 34));
                    javax.swing.UIManager.put("nimbusRed", new java.awt.Color(231, 76, 60));
                    javax.swing.UIManager.put("nimbusSelectedText", new java.awt.Color(255, 255, 255));
                    javax.swing.UIManager.put("nimbusSelectionBackground", new java.awt.Color(52, 152, 219));
                    javax.swing.UIManager.put("text", new java.awt.Color(44, 62, 80));
                    
                    // Global Font setup
                    java.awt.Font f = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14);
                    java.util.Enumeration<Object> keys = javax.swing.UIManager.getDefaults().keys();
                    while (keys.hasMoreElements()) {
                        Object key = keys.nextElement();
                        Object value = javax.swing.UIManager.get(key);
                        if (value instanceof javax.swing.plaf.FontUIResource) {
                            javax.swing.UIManager.put(key, new javax.swing.plaf.FontUIResource(f));
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            // Fall back to default if any error
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
