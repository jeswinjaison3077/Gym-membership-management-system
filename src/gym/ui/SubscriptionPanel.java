package gym.ui;

import gym.model.Plan;
import gym.model.Subscription;
import gym.service.GymService;
import gym.util.GymException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** GUI panel for subscribing members to plans, viewing subscriptions, and cancelling them. */
public class SubscriptionPanel extends JPanel {

    private final GymService service = new GymService();

    private final JTextField memberIdField = new JTextField(5);
    private final JComboBox<String> planBox = new JComboBox<>();
    private final JComboBox<String> modeBox = new JComboBox<>(new String[]{"CASH", "CARD", "UPI"});
    private final JTextField viewMemberIdField = new JTextField(5);
    private final JTextField expiringDaysField = new JTextField(5);
    private final JTextField cancelSubIdField = new JTextField(5);

    private DefaultTableModel tableModel;
    private JTable table;

    public SubscriptionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        refreshPlans();
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));

        JPanel row1 = new JPanel();
        row1.add(new JLabel("Member ID:")); row1.add(memberIdField);
        row1.add(new JLabel("Plan:")); row1.add(planBox);
        row1.add(new JLabel("Payment Mode:")); row1.add(modeBox);
        JButton subscribeBtn = new JButton("Subscribe");
        row1.add(subscribeBtn);

        JPanel row2 = new JPanel();
        row2.add(new JLabel("View subscriptions - Member ID:")); row2.add(viewMemberIdField);
        JButton viewBtn = new JButton("View");
        row2.add(viewBtn);

        JPanel row3 = new JPanel();
        row3.add(new JLabel("Show expiring within (days):")); row3.add(expiringDaysField);
        JButton expiringBtn = new JButton("Show Expiring");
        row3.add(expiringBtn);

        JPanel row4 = new JPanel();
        row4.add(new JLabel("Cancel Subscription ID:")); row4.add(cancelSubIdField);
        JButton cancelBtn = new JButton("Cancel Subscription");
        row4.add(cancelBtn);

        subscribeBtn.addActionListener(e -> onSubscribe());
        viewBtn.addActionListener(e -> onView());
        expiringBtn.addActionListener(e -> onExpiring());
        cancelBtn.addActionListener(e -> onCancel());

        panel.add(row1);
        panel.add(row2);
        panel.add(row3);
        panel.add(row4);
        return panel;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"Sub ID", "Member ID", "Plan", "Start Date", "End Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        return new JScrollPane(table);
    }

    private void refreshPlans() {
        planBox.removeAllItems();
        try {
            List<Plan> plans = service.listPlans();
            for (Plan p : plans) {
                planBox.addItem(p.getPlanId() + " - " + p.getPlanName() + " (Rs." + p.getPrice() + ")");
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void onSubscribe() {
        try {
            int memberId = Integer.parseInt(memberIdField.getText().trim());
            String sel = (String) planBox.getSelectedItem();
            int planId = Integer.parseInt(sel.split(" - ")[0]);
            String mode = (String) modeBox.getSelectedItem();
            double amount = service.subscribeMemberToPlan(memberId, planId, mode);
            JOptionPane.showMessageDialog(this, "Subscribed successfully. Charged: Rs." + amount);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Member ID must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | GymException ex) {
            showError(ex);
        }
    }

    private void onView() {
        try {
            int memberId = Integer.parseInt(viewMemberIdField.getText().trim());
            List<Subscription> subs = service.getSubscriptionsForMember(memberId);
            tableModel.setRowCount(0);
            for (Subscription s : subs) addRow(s);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Member ID must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onExpiring() {
        try {
            int days = Integer.parseInt(expiringDaysField.getText().trim());
            List<Subscription> subs = service.getExpiringSubscriptions(days);
            tableModel.setRowCount(0);
            for (Subscription s : subs) addRow(s);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Days must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onCancel() {
        try {
            int subId = Integer.parseInt(cancelSubIdField.getText().trim());
            boolean ok = service.cancelSubscription(subId);
            JOptionPane.showMessageDialog(this, ok ? "Subscription cancelled." : "Cancel failed.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Subscription ID must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void addRow(Subscription s) {
        tableModel.addRow(new Object[]{
                s.getSubscriptionId(), s.getMemberId(), s.getPlanName(),
                s.getStartDate(), s.getEndDate(), s.getStatus()
        });
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
