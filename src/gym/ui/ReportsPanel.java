package gym.ui;

import gym.service.GymService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/** GUI panel for simple summary reports: revenue, member/trainer counts. */
public class ReportsPanel extends JPanel {

    private final GymService service = new GymService();

    private final JLabel revenueLabel = new JLabel("Total Revenue: -");
    private final JLabel memberCountLabel = new JLabel("Total Members: -");
    private final JLabel trainerCountLabel = new JLabel("Total Trainers: -");

    public ReportsPanel() {
        setLayout(new GridLayout(4, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        Font bigFont = new Font("SansSerif", Font.BOLD, 16);
        revenueLabel.setFont(bigFont);
        memberCountLabel.setFont(bigFont);
        trainerCountLabel.setFont(bigFont);

        JButton refreshBtn = new JButton("Refresh Reports");
        refreshBtn.addActionListener(e -> refresh());

        add(revenueLabel);
        add(memberCountLabel);
        add(trainerCountLabel);
        add(refreshBtn);
    }

    private void refresh() {
        try {
            revenueLabel.setText("Total Revenue: Rs." + service.getTotalRevenue());
            memberCountLabel.setText("Total Members: " + service.listMembers().size());
            trainerCountLabel.setText("Total Trainers: " + service.listTrainers().size());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
