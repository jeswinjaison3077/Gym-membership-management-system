package gym.service;

import gym.dao.DBConnection;
import gym.dao.GymDAO;
import gym.model.*;
import gym.util.GymException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================================
 * SERVICE LAYER (BUSINESS LOGIC)
 * Coordinates validation, transactions, and delegates database calls to GymDAO.
 * ============================================================================
 */
public class GymService {

    private final GymDAO dao = new GymDAO();

    // ---------------- MEMBER FEATURES ----------------

    public int registerMember(String name, String phone, String email, int age,
                               String gender, Integer trainerId) throws SQLException, GymException {
        // Business Rule Validation: Minimum age requirement
        if (age < 12) {
            throw new GymException("Member must be at least 12 years old.");
        }
        Member m = new Member(0, name, phone, email, age, gender, LocalDate.now(), trainerId);
        return dao.addMember(m);
    }

    public boolean updateMember(Member m) throws SQLException {
        return dao.updateMember(m);
    }

    public boolean removeMember(int memberId) throws SQLException {
        return dao.deleteMember(memberId);
    }

    public Member findMember(int memberId) throws SQLException {
        return dao.getMemberById(memberId);
    }

    public List<Member> listMembers() throws SQLException {
        return dao.getAllMembers();
    }

    public List<Member> searchMembers(String keyword) throws SQLException {
        return dao.searchMembers(keyword);
    }

    // ---------------- TRAINER FEATURES ----------------

    public int addTrainer(String name, String phone, String email, String specialization, int expYears) throws SQLException {
        Trainer t = new Trainer(0, name, phone, email, specialization, expYears);
        return dao.addTrainer(t);
    }

    public List<Trainer> listTrainers() throws SQLException {
        return dao.getAllTrainers();
    }

    public boolean removeTrainer(int trainerId) throws SQLException {
        return dao.deleteTrainer(trainerId);
    }

    // ---------------- SUBSCRIPTION FEATURES ----------------

    public List<Plan> listPlans() throws SQLException {
        return dao.getAllPlans();
    }

    /**
     * Subscribes a member to a plan AND immediately records payment.
     * Demonstrates Database Transaction Control (Commit / Rollback).
     */
    public double subscribeMemberToPlan(int memberId, int planId, String paymentMode) throws SQLException, GymException {
        Plan plan = dao.getPlanById(planId);
        if (plan == null) {
            throw new GymException("Selected plan does not exist.");
        }
        Member member = dao.getMemberById(memberId);
        if (member == null) {
            throw new GymException("Member not found.");
        }

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(plan.getDurationMonths());

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // START TRANSACTION

            int subId = dao.addSubscription(con, memberId, planId, Date.valueOf(start), Date.valueOf(end));

            Payment payment = new Payment(0, memberId, subId, plan.getPrice(), start, paymentMode, "PAID");
            dao.addPayment(con, payment);

            con.commit(); // COMMIT TRANSACTION
            return plan.getPrice();
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public List<Subscription> getSubscriptionsForMember(int memberId) throws SQLException {
        return dao.getSubscriptionsByMember(memberId);
    }

    public List<Subscription> getExpiringSubscriptions(int daysAhead) throws SQLException {
        return dao.getExpiringSubscriptions(daysAhead);
    }

    public boolean cancelSubscription(int subscriptionId) throws SQLException {
        return dao.updateSubscriptionStatus(subscriptionId, "CANCELLED");
    }

    // ---------------- WORKOUT PLAN FEATURES ----------------

    public int assignWorkoutPlan(int memberId, int trainerId, String title, String description, int daysPerWeek) throws SQLException {
        WorkoutPlan w = new WorkoutPlan(0, memberId, trainerId, title, description, daysPerWeek, LocalDate.now());
        return dao.addWorkoutPlan(w);
    }

    public List<WorkoutPlan> getWorkoutPlansForMember(int memberId) throws SQLException {
        return dao.getWorkoutPlansByMember(memberId);
    }

    // ---------------- PAYMENT FEATURES ----------------

    public int recordPayment(int memberId, Integer subscriptionId, double amount, String mode, String status) throws SQLException {
        Payment p = new Payment(0, memberId, subscriptionId, amount, LocalDate.now(), mode, status);
        return dao.addPayment(p);
    }

    public List<Payment> getPaymentsForMember(int memberId) throws SQLException {
        return dao.getPaymentsByMember(memberId);
    }

    public List<Payment> getAllDuePayments() throws SQLException {
        return dao.getAllDuePayments();
    }

    public double getTotalRevenue() throws SQLException {
        return dao.getTotalRevenue();
    }
}
