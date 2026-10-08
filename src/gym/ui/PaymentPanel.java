package gym.ui;

import gym.model.Payment;
import gym.service.GymService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** GUI panel for recording payments and viewing payment history / dues. */
public class PaymentPanel extends JPanel {

    private final GymService service = new GymService();

    private final JTextField memberIdField = new JTextField(5);
    private final JTextField amountField = new JTextField(8);
    private final JComboBox<String> modeBox = new JComboBox<>(new String[]{"CASH", "CARD", "UPI"});
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"PAID", "DUE"});
    private final JTextField historyMemberIdField = new JTextField(5);

    private DefaultTableModel tableModel;
    private JTable table;

    public PaymentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));

        JPanel row1 = new JPanel();
        row1.add(new JLabel("Member ID:")); row1.add(memberIdField);
        row1.add(new JLabel("Amount:")); row1.add(amountField);
        row1.add(new JLabel("Mode:")); row1.add(modeBox);
        row1.add(new JLabel("Status:")); row1.add(statusBox);
        JButton recordBtn = new JButton("Record Payment");
        row1.add(recordBtn);

        JPanel row2 = new JPanel();
        row2.add(new JLabel("View history - Member ID:")); row2.add(historyMemberIdField);
        JButton historyBtn = new JButton("View History");
        row2.add(historyBtn);

        JPanel row3 = new JPanel();
        JButton dueBtn = new JButton("Show All Due Payments");
        row3.add(dueBtn);

        recordBtn.addActionListener(e -> onRecord());
        historyBtn.addActionListener(e -> onHistory());
        dueBtn.addActionListener(e -> onShowDue());

        panel.add(row1);
        panel.add(row2);
        panel.add(row3);
        return panel;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"Payment ID", "Member ID", "Amount", "Date", "Mode", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        return new JScrollPane(table);
    }

    private void onRecord() {
        try {
            int memberId = Integer.parseInt(memberIdField.getText().trim());
            double amount = Double.parseDouble(amountField.getText().trim());
            String mode = (String) modeBox.getSelectedItem();
            String status = (String) statusBox.getSelectedItem();
            int id = service.recordPayment(memberId, null, amount, mode, status);
            JOptionPane.showMessageDialog(this, "Payment recorded. ID = " + id);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Member ID and Amount must be numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onHistory() {
        try {
            int memberId = Integer.parseInt(historyMemberIdField.getText().trim());
            List<Payment> payments = service.getPaymentsForMember(memberId);
            tableModel.setRowCount(0);
            for (Payment p : payments) addRow(p);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Member ID must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onShowDue() {
        try {
            List<Payment> due = service.getAllDuePayments();
            tableModel.setRowCount(0);
            for (Payment p : due) addRow(p);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void addRow(Payment p) {
        tableModel.addRow(new Object[]{
                p.getPaymentId(), p.getMemberId(), p.getAmount(),
                p.getPaymentDate(), p.getPaymentMode(), p.getStatus()
        });
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
