package gym.service;

import gym.dao.*;
import gym.model.*;
import gym.util.GymException;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service layer: contains business rules and coordinates multiple DAOs.
 * The UI (Main.java) talks only to this class, never directly to a DAO.
 * This is standard layered architecture (UI -> Service -> DAO -> DB).
 */
public class GymService {

    private final MemberDAO memberDAO = new MemberDAO();
    private final TrainerDAO trainerDAO = new TrainerDAO();
    private final PlanDAO planDAO = new PlanDAO();
    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
    private final WorkoutPlanDAO workoutPlanDAO = new WorkoutPlanDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    // ---------------- MEMBER ----------------

    public int registerMember(String name, String phone, String email, int age,
                               String gender, Integer trainerId) throws SQLException, GymException {
        if (age < 12) {
            throw new GymException("Member must be at least 12 years old.");
        }
        Member m = new Member(0, name, phone, email, age, gender, LocalDate.now(), trainerId);
        return memberDAO.addMember(m);
    }

    public boolean updateMember(Member m) throws SQLException {
        return memberDAO.updateMember(m);
    }

    public boolean removeMember(int memberId) throws SQLException {
        return memberDAO.deleteMember(memberId);
    }

    public Member findMember(int memberId) throws SQLException {
        return memberDAO.getMemberById(memberId);
    }

    public List<Member> listMembers() throws SQLException {
        return memberDAO.getAllMembers();
    }

    public List<Member> searchMembers(String keyword) throws SQLException {
        return memberDAO.searchByNameOrId(keyword);
    }

    // ---------------- TRAINER ----------------

    public int addTrainer(String name, String phone, String email, String specialization, int expYears) throws SQLException {
        Trainer t = new Trainer(0, name, phone, email, specialization, expYears);
        return trainerDAO.addTrainer(t);
    }

    public List<Trainer> listTrainers() throws SQLException {
        return trainerDAO.getAllTrainers();
    }

    public boolean removeTrainer(int trainerId) throws SQLException {
        return trainerDAO.deleteTrainer(trainerId);
    }

    // ---------------- SUBSCRIPTION ----------------

    public List<Plan> listPlans() throws SQLException {
        return planDAO.getAllPlans();
    }

    /**
     * Subscribes a member to a plan AND immediately records the payment.
     * This method shows how the service layer coordinates two DAOs
     * so that the two related actions stay consistent.
     */
    public double subscribeMemberToPlan(int memberId, int planId, String paymentMode) throws SQLException, GymException {
        Plan plan = planDAO.getPlanById(planId);
        if (plan == null) {
            throw new GymException("Selected plan does not exist.");
        }
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) {
            throw new GymException("Member not found.");
        }

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(plan.getDurationMonths());

        java.sql.Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // START TRANSACTION

            int subId = subscriptionDAO.addSubscription(con, memberId, planId, Date.valueOf(start), Date.valueOf(end));

            Payment payment = new Payment(0, memberId, subId, plan.getPrice(), start, paymentMode, "PAID");
            paymentDAO.addPayment(con, payment);

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
        return subscriptionDAO.getSubscriptionsByMember(memberId);
    }

    public List<Subscription> getExpiringSubscriptions(int daysAhead) throws SQLException {
        return subscriptionDAO.getExpiringSoon(daysAhead);
    }

    public boolean cancelSubscription(int subscriptionId) throws SQLException {
        return subscriptionDAO.updateStatus(subscriptionId, "CANCELLED");
    }

    // ---------------- WORKOUT PLAN ----------------

    public int assignWorkoutPlan(int memberId, int trainerId, String title, String description, int daysPerWeek) throws SQLException {
        WorkoutPlan w = new WorkoutPlan(0, memberId, trainerId, title, description, daysPerWeek, LocalDate.now());
        return workoutPlanDAO.addWorkoutPlan(w);
    }

    public List<WorkoutPlan> getWorkoutPlansForMember(int memberId) throws SQLException {
        return workoutPlanDAO.getWorkoutPlansByMember(memberId);
    }

    // ---------------- PAYMENT ----------------

    public int recordPayment(int memberId, Integer subscriptionId, double amount, String mode, String status) throws SQLException {
        Payment p = new Payment(0, memberId, subscriptionId, amount, LocalDate.now(), mode, status);
        return paymentDAO.addPayment(p);
    }

    public List<Payment> getPaymentsForMember(int memberId) throws SQLException {
        return paymentDAO.getPaymentsByMember(memberId);
    }

    public List<Payment> getAllDuePayments() throws SQLException {
        return paymentDAO.getAllDuePayments();
    }

    public double getTotalRevenue() throws SQLException {
        return paymentDAO.getTotalRevenue();
    }
}
