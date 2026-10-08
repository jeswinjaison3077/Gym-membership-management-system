package gym.ui;

import gym.model.WorkoutPlan;
import gym.service.GymService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** GUI panel for assigning and viewing workout plans. */
public class WorkoutPanel extends JPanel {

    private final GymService service = new GymService();

    private final JTextField memberIdField = new JTextField(5);
    private final JTextField trainerIdField = new JTextField(5);
    private final JTextField titleField = new JTextField(15);
    private final JTextField descField = new JTextField(20);
    private final JTextField daysField = new JTextField(3);
    private final JTextField viewMemberIdField = new JTextField(5);

    private DefaultTableModel tableModel;
    private JTable table;

    public WorkoutPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));

        JPanel row1 = new JPanel();
        row1.add(new JLabel("Member ID:")); row1.add(memberIdField);
        row1.add(new JLabel("Trainer ID:")); row1.add(trainerIdField);
        row1.add(new JLabel("Days/Week:")); row1.add(daysField);

        JPanel row2 = new JPanel();
        row2.add(new JLabel("Title:")); row2.add(titleField);
        row2.add(new JLabel("Description:")); row2.add(descField);
        JButton assignBtn = new JButton("Assign Plan");
        row2.add(assignBtn);

        JPanel row3 = new JPanel();
        row3.add(new JLabel("View plans - Member ID:")); row3.add(viewMemberIdField);
        JButton viewBtn = new JButton("View");
        row3.add(viewBtn);

        assignBtn.addActionListener(e -> onAssign());
        viewBtn.addActionListener(e -> onView());

        panel.add(row1);
        panel.add(row2);
        panel.add(row3);
        return panel;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"Workout ID", "Title", "Description", "Days/Week", "Created Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        return new JScrollPane(table);
    }

    private void onAssign() {
        try {
            int memberId = Integer.parseInt(memberIdField.getText().trim());
            int trainerId = Integer.parseInt(trainerIdField.getText().trim());
            int days = Integer.parseInt(daysField.getText().trim());
            int id = service.assignWorkoutPlan(
                    memberId, trainerId, titleField.getText().trim(), descField.getText().trim(), days);
            JOptionPane.showMessageDialog(this, "Workout plan assigned. ID = " + id);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Member ID, Trainer ID and Days must be whole numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onView() {
        try {
            int memberId = Integer.parseInt(viewMemberIdField.getText().trim());
            List<WorkoutPlan> plans = service.getWorkoutPlansForMember(memberId);
            tableModel.setRowCount(0);
            for (WorkoutPlan w : plans) {
                tableModel.addRow(new Object[]{
                        w.getWorkoutId(), w.getTitle(), w.getDescription(),
                        w.getDaysPerWeek(), w.getCreatedDate()
                });
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Member ID must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
