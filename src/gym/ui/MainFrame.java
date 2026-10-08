package gym.ui;

import javax.swing.*;

/**
 * The main application window. Holds one tab per feature area.
 * Each tab is a self-contained JPanel that talks only to GymService
 * (never directly to a DAO) - same layered rule as the console version.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Gym Membership Management System");
        setSize(1000, 700);
        setMinimumSize(new java.awt.Dimension(850, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        setLayout(new java.awt.BorderLayout());

        // Header Panel styling
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new java.awt.Color(41, 128, 185)); // Brand blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        headerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        
        JLabel titleLabel = new JLabel("Gym Management Pro");
        titleLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 26));
        titleLabel.setForeground(java.awt.Color.WHITE);
        headerPanel.add(titleLabel);

        add(headerPanel, java.awt.BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Breathing room
        MemberPanel memberPanel = new MemberPanel();
        TrainerPanel trainerPanel = new TrainerPanel(memberPanel::refreshTrainerList);
        tabs.addTab("Members", memberPanel);
        tabs.addTab("Trainers", trainerPanel);
        tabs.addTab("Subscriptions", new SubscriptionPanel());
        tabs.addTab("Workout Plans", new WorkoutPanel());
        tabs.addTab("Payments", new PaymentPanel());
        tabs.addTab("Reports", new ReportsPanel());

        add(tabs, java.awt.BorderLayout.CENTER);
    }
}
