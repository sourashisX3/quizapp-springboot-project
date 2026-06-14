package com.sourashis.quizapp.infrastructure.data;

import com.sourashis.quizapp.modules.analytics.entity.UserStatistics;
import com.sourashis.quizapp.modules.analytics.repository.UserStatisticsRepository;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.repository.UserRepository;
import com.sourashis.quizapp.modules.question.entity.DifficultyLevel;
import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.question.entity.QuestionOption;
import com.sourashis.quizapp.modules.question.repository.QuestionOptionRepository;
import com.sourashis.quizapp.modules.question.repository.QuestionRepository;
import com.sourashis.quizapp.modules.quiz.entity.Category;
import com.sourashis.quizapp.modules.quiz.entity.Quiz;
import com.sourashis.quizapp.modules.quiz.entity.QuizQuestion;
import com.sourashis.quizapp.modules.quiz.repository.CategoryRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizQuestionRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizRepository;
import com.sourashis.quizapp.modules.roles.entity.Permission;
import com.sourashis.quizapp.modules.roles.entity.Role;
import com.sourashis.quizapp.modules.roles.repository.PermissionRepository;
import com.sourashis.quizapp.modules.roles.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private PermissionRepository permissionRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private QuestionOptionRepository questionOptionRepository;
    @Autowired private QuizRepository quizRepository;
    @Autowired private QuizQuestionRepository quizQuestionRepository;
    @Autowired private UserStatisticsRepository userStatisticsRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (permissionRepository.count() > 0) {
            log.info("Data already initialized, skipping seeder.");
            return;
        }

        log.info("Seeding permissions...");
        Map<String, String[]> permissionDefs = new LinkedHashMap<>();
        permissionDefs.put("question:read", new String[]{"question", "read", "View questions"});
        permissionDefs.put("question:create", new String[]{"question", "create", "Create questions"});
        permissionDefs.put("question:update", new String[]{"question", "update", "Update questions"});
        permissionDefs.put("question:delete", new String[]{"question", "delete", "Delete questions"});
        permissionDefs.put("quiz:read", new String[]{"quiz", "read", "View quizzes"});
        permissionDefs.put("quiz:create", new String[]{"quiz", "create", "Create quizzes"});
        permissionDefs.put("quiz:update", new String[]{"quiz", "update", "Update quizzes"});
        permissionDefs.put("quiz:delete", new String[]{"quiz", "delete", "Delete quizzes"});
        permissionDefs.put("quiz:attempt", new String[]{"quiz", "attempt", "Attempt and submit quizzes"});
        permissionDefs.put("category:read", new String[]{"category", "read", "View categories"});
        permissionDefs.put("category:create", new String[]{"category", "create", "Create categories"});
        permissionDefs.put("category:update", new String[]{"category", "update", "Update categories"});
        permissionDefs.put("category:delete", new String[]{"category", "delete", "Delete categories"});
        permissionDefs.put("user:read", new String[]{"user", "read", "View users"});
        permissionDefs.put("user:create", new String[]{"user", "create", "Create users"});
        permissionDefs.put("user:update", new String[]{"user", "update", "Update users"});
        permissionDefs.put("user:delete", new String[]{"user", "delete", "Delete users"});
        permissionDefs.put("user:manage", new String[]{"user", "manage", "Manage user roles"});
        permissionDefs.put("role:read", new String[]{"role", "read", "View roles"});
        permissionDefs.put("role:create", new String[]{"role", "create", "Create roles"});
        permissionDefs.put("role:update", new String[]{"role", "update", "Update roles"});
        permissionDefs.put("role:delete", new String[]{"role", "delete", "Delete roles"});
        permissionDefs.put("contest:read", new String[]{"contest", "read", "View contests"});
        permissionDefs.put("contest:create", new String[]{"contest", "create", "Create contests"});
        permissionDefs.put("contest:update", new String[]{"contest", "update", "Update contests"});
        permissionDefs.put("contest:delete", new String[]{"contest", "delete", "Delete contests"});
        permissionDefs.put("contest:join", new String[]{"contest", "join", "Join contests"});
        permissionDefs.put("leaderboard:read", new String[]{"leaderboard", "read", "View leaderboard"});
        permissionDefs.put("notification:read", new String[]{"notification", "read", "View notifications"});
        permissionDefs.put("notification:send", new String[]{"notification", "send", "Send notifications"});
        permissionDefs.put("reward:read", new String[]{"reward", "read", "View rewards"});
        permissionDefs.put("reward:claim", new String[]{"reward", "claim", "Claim rewards"});
        permissionDefs.put("file:upload", new String[]{"file", "upload", "Upload files"});
        permissionDefs.put("file:delete", new String[]{"file", "delete", "Delete files"});
        permissionDefs.put("audit:read", new String[]{"audit", "read", "View audit logs"});
        permissionDefs.put("analytics:read", new String[]{"analytics", "read", "View analytics"});

        List<Permission> permissions = permissionDefs.entrySet().stream()
                .map(entry -> Permission.builder()
                        .name(entry.getKey())
                        .resource(entry.getValue()[0])
                        .action(entry.getValue()[1])
                        .description(entry.getValue()[2])
                        .build())
                .collect(Collectors.toList());

        permissionRepository.saveAll(permissions);
        log.info("Created {} permissions.", permissions.size());

        Map<String, Permission> permMap = permissions.stream()
                .collect(Collectors.toMap(Permission::getName, p -> p));

        log.info("Seeding roles...");

        Set<Permission> userPerms = Set.of(
                permMap.get("question:read"),
                permMap.get("quiz:read"),
                permMap.get("quiz:attempt"),
                permMap.get("category:read"),
                permMap.get("leaderboard:read"),
                permMap.get("notification:read"),
                permMap.get("reward:read"),
                permMap.get("reward:claim"),
                permMap.get("file:upload"),
                permMap.get("contest:read"),
                permMap.get("contest:join")
        );

        Set<Permission> adminPerms = Set.of(
                permMap.get("question:read"), permMap.get("question:create"), permMap.get("question:update"), permMap.get("question:delete"),
                permMap.get("quiz:read"), permMap.get("quiz:create"), permMap.get("quiz:update"), permMap.get("quiz:delete"), permMap.get("quiz:attempt"),
                permMap.get("category:read"), permMap.get("category:create"), permMap.get("category:update"), permMap.get("category:delete"),
                permMap.get("user:read"), permMap.get("user:create"), permMap.get("user:update"), permMap.get("user:delete"),
                permMap.get("role:read"),
                permMap.get("leaderboard:read"),
                permMap.get("notification:read"), permMap.get("notification:send"),
                permMap.get("reward:read"), permMap.get("reward:claim"),
                permMap.get("file:upload"), permMap.get("file:delete"),
                permMap.get("contest:read"), permMap.get("contest:join"),
                permMap.get("audit:read"),
                permMap.get("analytics:read")
        );

        Set<Permission> superAdminPerms = Set.copyOf(permMap.values());

        Role userRole = Role.builder().name("ROLE_USER").description("Regular user with basic permissions").isSystem(true).permissions(userPerms).build();
        Role adminRole = Role.builder().name("ROLE_ADMIN").description("Administrator with elevated permissions").isSystem(true).permissions(adminPerms).build();
        Role superAdminRole = Role.builder().name("ROLE_SUPER_ADMIN").description("Super Administrator with all permissions").isSystem(true).permissions(superAdminPerms).build();

        roleRepository.saveAll(List.of(userRole, adminRole, superAdminRole));
        log.info("Created 3 roles: ROLE_USER, ROLE_ADMIN, ROLE_SUPER_ADMIN");

        log.info("Seeding categories...");

        Category programming = Category.builder().name("Programming").description("").iconUrl("").build();
        Category science = Category.builder().name("Science").description("").iconUrl("").build();
        Category mathematics = Category.builder().name("Mathematics").description("").iconUrl("").build();
        Category generalKnowledge = Category.builder().name("General Knowledge").description("").iconUrl("").build();

        categoryRepository.saveAll(List.of(programming, science, mathematics, generalKnowledge));
        log.info("Created 4 categories: Programming, Science, Mathematics, General Knowledge");

        log.info("Seeding questions...");

        Question q1 = questionRepository.save(Question.builder()
                .title("What is the correct way to declare a variable in Java?")
                .category(programming)
                .difficulty(DifficultyLevel.EASY)
                .points(10)
                .build());

        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q1).optionText("int x = 5;").isCorrect(true).sortOrder(1).build(),
                QuestionOption.builder().question(q1).optionText("variable x = 5;").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q1).optionText("x = 5 int;").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q1).optionText("int x == 5;").isCorrect(false).sortOrder(4).build()
        ));

        Question q2 = questionRepository.save(Question.builder()
                .title("Which of these is not an OOP principle?")
                .category(programming)
                .difficulty(DifficultyLevel.EASY)
                .points(10)
                .build());

        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q2).optionText("Encapsulation").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q2).optionText("Inheritance").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q2).optionText("Polymorphism").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q2).optionText("Compilation").isCorrect(true).sortOrder(4).build()
        ));

        Question q3 = questionRepository.save(Question.builder()
                .title("What is the chemical symbol for water?")
                .category(science)
                .difficulty(DifficultyLevel.EASY)
                .points(10)
                .build());

        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q3).optionText("H2O").isCorrect(true).sortOrder(1).build(),
                QuestionOption.builder().question(q3).optionText("CO2").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q3).optionText("NaCl").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q3).optionText("O2").isCorrect(false).sortOrder(4).build()
        ));

        Question q4 = questionRepository.save(Question.builder()
                .title("What planet is known as the Red Planet?")
                .category(science)
                .difficulty(DifficultyLevel.EASY)
                .points(10)
                .build());

        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q4).optionText("Venus").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q4).optionText("Mars").isCorrect(true).sortOrder(2).build(),
                QuestionOption.builder().question(q4).optionText("Jupiter").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q4).optionText("Saturn").isCorrect(false).sortOrder(4).build()
        ));

        Question q5 = questionRepository.save(Question.builder()
                .title("What is the value of Pi (\u03c0) to 2 decimal places?")
                .category(mathematics)
                .difficulty(DifficultyLevel.EASY)
                .points(10)
                .build());

        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q5).optionText("3.14").isCorrect(true).sortOrder(1).build(),
                QuestionOption.builder().question(q5).optionText("3.16").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q5).optionText("3.12").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q5).optionText("3.18").isCorrect(false).sortOrder(4).build()
        ));

        Question q6 = questionRepository.save(Question.builder()
                .title("What is the square root of 144?")
                .category(mathematics)
                .difficulty(DifficultyLevel.EASY)
                .points(10)
                .build());

        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q6).optionText("10").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q6).optionText("12").isCorrect(true).sortOrder(2).build(),
                QuestionOption.builder().question(q6).optionText("14").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q6).optionText("16").isCorrect(false).sortOrder(4).build()
        ));

        Question q7 = questionRepository.save(Question.builder()
                .title("Which country has the largest population?")
                .category(generalKnowledge)
                .difficulty(DifficultyLevel.MEDIUM)
                .points(10)
                .build());

        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q7).optionText("India").isCorrect(true).sortOrder(1).build(),
                QuestionOption.builder().question(q7).optionText("USA").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q7).optionText("Indonesia").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q7).optionText("China").isCorrect(false).sortOrder(4).build()
        ));

        Question q8 = questionRepository.save(Question.builder()
                .title("What is the capital of Japan?")
                .category(generalKnowledge)
                .difficulty(DifficultyLevel.EASY)
                .points(10)
                .build());

        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q8).optionText("Seoul").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q8).optionText("Beijing").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q8).optionText("Tokyo").isCorrect(true).sortOrder(3).build(),
                QuestionOption.builder().question(q8).optionText("Bangkok").isCorrect(false).sortOrder(4).build()
        ));

        log.info("Created 8 questions with options.");

        log.info("Seeding super admin user...");

        User superUser = User.builder()
                .username("superadmin")
                .password(passwordEncoder.encode("superadmin123"))
                .email("superadmin@quizapp.com")
                .displayName("Super Admin")
                .phoneNumber("0000000000")
                .address("System")
                .role(superAdminRole)
                .build();

        userRepository.save(superUser);
        log.info("Created superadmin user (username: superadmin, password: superadmin123)");

        log.info("Seeding default quiz...");

        Quiz programmingQuiz = Quiz.builder()
                .title("Programming Basics")
                .description("Test your knowledge of basic programming concepts.")
                .category(programming)
                .difficulty("EASY")
                .timeLimitMinutes(10)
                .passingScorePct(60.0)
                .maxAttempts(3)
                .isRandomized(true)
                .isPublished(true)
                .isActive(true)
                .totalQuestions(2)
                .totalPoints(20)
                .createdBy(superUser)
                .build();

        quizRepository.save(programmingQuiz);

        QuizQuestion qq1 = QuizQuestion.builder()
                .quiz(programmingQuiz)
                .questionId(q1.getId())
                .sortOrder(1)
                .build();

        QuizQuestion qq2 = QuizQuestion.builder()
                .quiz(programmingQuiz)
                .questionId(q2.getId())
                .sortOrder(2)
                .build();

        quizQuestionRepository.saveAll(List.of(qq1, qq2));
        log.info("Created quiz 'Programming Basics' with 2 questions.");

        log.info("Seeding user statistics...");

        UserStatistics stats = UserStatistics.builder()
                .userId(superUser.getId())
                .totalQuizzesTaken(0)
                .totalQuizzesPassed(0)
                .totalQuestionsAnswered(0)
                .totalCorrectAnswers(0)
                .totalScore(0L)
                .totalXp(0L)
                .currentStreak(0)
                .longestStreak(0)
                .totalContestsParticipated(0)
                .totalContestsWon(0)
                .badgesCount(0)
                .achievementsCount(0)
                .build();

        userStatisticsRepository.save(stats);
        log.info("Created user statistics for superadmin.");

        log.info("Data initialization complete.");
    }
}
