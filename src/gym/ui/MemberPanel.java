package gym.ui;

import gym.model.Member;
import gym.model.Trainer;
import gym.service.GymService;
import gym.util.GymException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * GUI panel for registering, viewing, searching, updating and deleting members.
 * Every button click here calls a GymService method - no SQL, no business
 * rules live in this class, same principle as the old console Main.java.
 */
public class MemberPanel extends JPanel {

    private final GymService service = new GymService();

    private final JTextField idField = new JTextField(5);
    private final JTextField nameField = new JTextField(12);
    private final JTextField phoneField = new JTextField(10);
    private final JTextField emailField = new JTextField(12);
    private final JTextField ageField = new JTextField(3);
    private final JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private final JComboBox<String> trainerBox = new JComboBox<>();
    private final JTextField searchField = new JTextField(10);

    private DefaultTableModel tableModel;
    private JTable table;

    public MemberPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        refreshTrainerList();
        refreshTable();
    }

    public void refreshTrainerList() {
        trainerBox.removeAllItems();
        trainerBox.addItem("None");
        try {
            List<Trainer> trainers = service.listTrainers();
            for (Trainer t : trainers) {
                trainerBox.addItem(t.getId() + " - " + t.getName());
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private JPanel buildFormPanel() {
        // Wrapper panel to stop components from stretching across the entire width
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ), 
                "Member Details", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("SansSerif", Font.BOLD, 14), 
                new Color(41, 128, 185)
        ));
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1
        gbc.gridy = 0;
        gbc.gridx = 0; panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; panel.add(idField, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 3; panel.add(nameField, gbc);
        gbc.gridx = 4; panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 5; panel.add(phoneField, gbc);
        gbc.gridx = 6; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 7; panel.add(emailField, gbc);

        // Row 2
        gbc.gridy = 1;
        gbc.gridx = 0; panel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1; panel.add(ageField, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 3; panel.add(genderBox, gbc);
        gbc.gridx = 4; panel.add(new JLabel("Trainer:"), gbc);
        gbc.gridx = 5; panel.add(trainerBox, gbc);

        // Buttons row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton registerBtn = new JButton("Register");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        JButton refreshBtn = new JButton("Refresh");
        btnPanel.add(registerBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn); btnPanel.add(refreshBtn);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("Go");
        searchPanel.add(searchBtn);
        
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomRow.add(btnPanel, BorderLayout.WEST);
        bottomRow.add(searchPanel, BorderLayout.EAST);
        
        wrapper.add(panel, BorderLayout.WEST);
        wrapper.add(bottomRow, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> onRegister());
        updateBtn.addActionListener(e -> onUpdate());
        deleteBtn.addActionListener(e -> onDelete());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> refreshTable());
        searchBtn.addActionListener(e -> onSearch());

        return wrapper;
    }

    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Phone", "Email", "Age", "Gender", "Trainer"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // table is read-only; editing happens through the form
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
        ageField.setText(tableModel.getValueAt(row, 4).toString());
        genderBox.setSelectedItem(tableModel.getValueAt(row, 5).toString());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        // Using SwingWorker to perform DB operations on a background thread
        // This prevents the UI from freezing while loading data (Multithreading topic)
        SwingWorker<List<Member>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Member> doInBackground() throws Exception {
                return service.listMembers();
            }

            @Override
            protected void done() {
                try {
                    List<Member> members = get();
                    for (Member m : members) addRow(m);
                } catch (Exception e) {
                    showError(e);
                }
            }
        };
        worker.execute();
    }

    private void addRow(Member m) {
        tableModel.addRow(new Object[]{
                m.getId(), m.getName(), m.getPhone(), m.getEmail(),
                m.getAge(), m.getGender(),
                m.getTrainerName() != null ? m.getTrainerName() : "None"
        });
    }

    private Integer selectedTrainerId() {
        String sel = (String) trainerBox.getSelectedItem();
        if (sel == null || sel.equals("None")) return null;
        return Integer.parseInt(sel.split(" - ")[0]);
    }

    private void onRegister() {
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            int id = service.registerMember(
                    nameField.getText().trim(), phoneField.getText().trim(), emailField.getText().trim(),
                    age, (String) genderBox.getSelectedItem(), selectedTrainerId());
            JOptionPane.showMessageDialog(this, "Member registered. ID = " + id);
            clearForm();
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | GymException ex) {
            showError(ex);
        }
    }

    private void onUpdate() {
        if (idField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Select a member from the table first.");
            return;
        }
        try {
            Member m = service.findMember(Integer.parseInt(idField.getText().trim()));
            if (m == null) {
                JOptionPane.showMessageDialog(this, "Member not found.");
                return;
            }
            m.setName(nameField.getText().trim());
            m.setPhone(phoneField.getText().trim());
            m.setEmail(emailField.getText().trim());
            m.setAge(Integer.parseInt(ageField.getText().trim()));
            m.setGender((String) genderBox.getSelectedItem());
            m.setTrainerId(selectedTrainerId());
            boolean ok = service.updateMember(m);
            JOptionPane.showMessageDialog(this, ok ? "Updated successfully." : "Update failed.");
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onDelete() {
        if (idField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Select a member from the table first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this member?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = service.removeMember(Integer.parseInt(idField.getText().trim()));
            JOptionPane.showMessageDialog(this, ok ? "Deleted successfully." : "Delete failed.");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onSearch() {
        try {
            List<Member> results = service.searchMembers(searchField.getText().trim());
            tableModel.setRowCount(0);
            for (Member m : results) addRow(m);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        ageField.setText("");
        genderBox.setSelectedIndex(0);
        trainerBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
