package gym.ui;

import gym.model.Trainer;
import gym.service.GymService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** GUI panel for adding, viewing and deleting trainers. */
public class TrainerPanel extends JPanel {

    private final Runnable onTrainerChanged;
    private final GymService service = new GymService();

    private final JTextField idField = new JTextField(5);
    private final JTextField nameField = new JTextField(12);
    private final JTextField phoneField = new JTextField(10);
    private final JTextField emailField = new JTextField(12);
    private final JTextField specField = new JTextField(12);
    private final JTextField expField = new JTextField(3);

    private DefaultTableModel tableModel;
    private JTable table;

    public TrainerPanel(Runnable onTrainerChanged) {
        this.onTrainerChanged = onTrainerChanged;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        refreshTable();
    }

    public TrainerPanel() {
        this(null);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel row1 = new JPanel();
        row1.add(new JLabel("ID:")); row1.add(idField);
        row1.add(new JLabel("Name:")); row1.add(nameField);
        row1.add(new JLabel("Phone:")); row1.add(phoneField);
        row1.add(new JLabel("Email:")); row1.add(emailField);
        row1.add(new JLabel("Specialization:")); row1.add(specField);
        row1.add(new JLabel("Experience (yrs):")); row1.add(expField);

        JPanel row2 = new JPanel();
        JButton addBtn = new JButton("Add Trainer");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton clearBtn = new JButton("Clear Form");
        JButton refreshBtn = new JButton("Refresh");
        row2.add(addBtn); row2.add(deleteBtn); row2.add(clearBtn); row2.add(refreshBtn);

        addBtn.addActionListener(e -> onAdd());
        deleteBtn.addActionListener(e -> onDelete());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> refreshTable());

        panel.add(row1);
        panel.add(row2);
        return panel;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Phone", "Email", "Specialization", "Experience"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        return new JScrollPane(table);
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        phoneField.setText(tableModel.getValueAt(row, 2).toString());
        emailField.setText(tableModel.getValueAt(row, 3).toString());
        specField.setText(tableModel.getValueAt(row, 4).toString());
        expField.setText(tableModel.getValueAt(row, 5).toString());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Trainer> trainers = service.listTrainers();
            for (Trainer t : trainers) {
                tableModel.addRow(new Object[]{
                        t.getId(), t.getName(), t.getPhone(), t.getEmail(),
                        t.getSpecialization(), t.getExperienceYears()
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void onAdd() {
        try {
            int exp = Integer.parseInt(expField.getText().trim());
            int id = service.addTrainer(
                    nameField.getText().trim(), phoneField.getText().trim(),
                    emailField.getText().trim(), specField.getText().trim(), exp);
            JOptionPane.showMessageDialog(this, "Trainer added. ID = " + id);
            clearForm();
            refreshTable();
            if (onTrainerChanged != null) {
                onTrainerChanged.run();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Experience must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onDelete() {
        if (idField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Select a trainer from the table first.");
            return;
        }
        try {
            boolean ok = service.removeTrainer(Integer.parseInt(idField.getText().trim()));
            JOptionPane.showMessageDialog(this, ok ? "Deleted successfully." : "Delete failed.");
            clearForm();
            refreshTable();
            if (onTrainerChanged != null) {
                onTrainerChanged.run();
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        specField.setText("");
        expField.setText("");
        table.clearSelection();
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
